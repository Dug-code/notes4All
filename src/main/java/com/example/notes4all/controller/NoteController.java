package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.dao.NoteDAO;
import com.example.notes4all.model.Note;
import com.example.notes4all.util.AlertUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class NoteController {

    /* =========================
       UNDO / REDO STACKS
       ========================= */
    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new ArrayDeque<>();
    private boolean ignoreChanges = false;

    /* =========================
       CURRENT USER + NOTE STATE
       ========================= */
    private Note currentNote;
    private String currentUserId; // Set via loadNotes()

    /* =========================
       FXML COMPONENTS
       ========================= */
    @FXML private HTMLEditor noteEditor;
    @FXML private TextField noteNameField;
    @FXML private ListView<Note> noteListView;

    /* =========================
       INITIALIZE
       ========================= */
    @FXML
    public void initialize() {

        // Auto-capture undo snapshots when typing
        noteEditor.setOnKeyReleased(e -> {
            if (ignoreChanges) return;

            String html = noteEditor.getHtmlText();
            if (!undoStack.isEmpty() && undoStack.peek().equals(html)) return;

            undoStack.push(html);
            redoStack.clear();
        });

        // Display note titles in the ListView
        noteListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                setText(empty || note == null ? null : note.getTitle());
            }
        });
    }

    /* =========================
       LOAD NOTES FOR USER
       ========================= */
    public void loadNotes(String userId) {
        this.currentUserId = userId;

        new Thread(() -> {
            try {
                List<Note> notes = NoteDAO.getMyNotes(userId);

                Platform.runLater(() ->
                        noteListView.getItems().setAll(notes)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /* =========================
       NEW NOTE
       ========================= */
    @FXML
    protected void onNewNoteClick() {
        currentNote = null;

        ignoreChanges = true;
        noteEditor.setHtmlText("");
        noteNameField.clear();
        undoStack.clear();
        redoStack.clear();
        ignoreChanges = false;

        noteListView.getSelectionModel().clearSelection();
    }

    /* =========================
       SAVE NOTE
       ========================= */
    @FXML
    protected void onSaveNoteClick() {
        String title = noteNameField.getText().trim();

        if (title.isEmpty()) {
            AlertUtil.error("Please enter a note name.");
            return;
        }

        String content = noteEditor.getHtmlText();

        new Thread(() -> {
            try {
                if (currentNote == null) {
                    // Creating new note
                    currentNote = new Note(
                            currentUserId,
                            title,
                            content,
                            Instant.now().toString(),
                            List.of()
                    );
                    NoteDAO.save(currentNote);
                } else {
                    // Updating existing note
                    NoteDAO.update(currentNote.getNoteId(), title, content);
                    currentNote.setTitle(title);
                    currentNote.setContent(content);
                }

                loadNotes(currentUserId);

                Platform.runLater(() -> AlertUtil.success("Note saved successfully!"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /* =========================
       CLICK LIST ITEM
       ========================= */
    @FXML
    protected void onListClick(javafx.scene.input.MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) return;

        Note selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected != null) openNote(selected);
    }

    private void openNote(Note note) {
        currentNote = note;

        ignoreChanges = true;
        noteEditor.setHtmlText(note.getContent());
        noteNameField.setText(note.getTitle());
        undoStack.clear();
        redoStack.clear();
        ignoreChanges = false;
    }

    /* =========================
       DELETE NOTE
       ========================= */
    @FXML
    protected void onDeleteClick() {
        Note selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        new Thread(() -> {
            try {
                NoteDAO.delete(selected.getNoteId());
                loadNotes(currentUserId);

                Platform.runLater(this::onNewNoteClick);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /* =========================
       UNDO / REDO
       ========================= */
    @FXML
    protected void onUndoClick() {
        if (undoStack.isEmpty()) return;

        ignoreChanges = true;
        redoStack.push(noteEditor.getHtmlText());
        noteEditor.setHtmlText(undoStack.pop());
        ignoreChanges = false;
    }

    @FXML
    protected void onRedoClick() {
        if (redoStack.isEmpty()) return;

        ignoreChanges = true;
        undoStack.push(noteEditor.getHtmlText());
        noteEditor.setHtmlText(redoStack.pop());
        ignoreChanges = false;
    }

    /* =========================
       BACK TO DASHBOARD
       ========================= */
    @FXML
    private void onBackClick() {
        App.setRoot("dashboard");
    }

    public void openNoteFromDashboard(Note note) {
        this.currentNote = note; // includes noteId
        ignoreChanges = true;
        noteEditor.setHtmlText(note.getContent());
        noteNameField.setText(note.getTitle());
        undoStack.clear();
        redoStack.clear();
        ignoreChanges = false;
    }


    @FXML
    private void onShareNoteClick() {
        if (currentNote == null) {
            AlertUtil.error("Save the note before sharing it.");
            return;
        }

        // Ask for friend username
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Share Note");
        dialog.setHeaderText("Enter your friend's username:");
        dialog.setContentText("Username:");

        dialog.showAndWait().ifPresent(username -> {
            boolean success = NoteDAO.shareNoteWithUser(currentNote.getNoteId(), username);

            if (success) {
                AlertUtil.success("Note successfully shared with " + username + "!");
            } else {
                AlertUtil.error("Failed to share note — user may not exist.");
            }
        });
    }

    @FXML
    private void onExportPdfClick() {
        if (currentNote == null) {
            AlertUtil.error("Please select or create a note first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Note as PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF File", "*.pdf"));

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            exportNoteToPdf(currentNote, file.getAbsolutePath());
            AlertUtil.success("PDF exported successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.error("Failed to export PDF.");
        }
    }

    private void exportNoteToPdf(Note note, String filePath) throws Exception {
        String title = note.getTitle();
        String contentHtml = note.getContent();

        // Very simple HTML → text conversion
        String textContent = contentHtml.replaceAll("<[^>]*>", "");

        com.lowagie.text.Document document = new com.lowagie.text.Document();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(filePath));

        document.open();
        document.add(new com.lowagie.text.Paragraph(title));
        document.add(new com.lowagie.text.Paragraph("\n"));
        document.add(new com.lowagie.text.Paragraph(textContent));
        document.close();
    }



}
