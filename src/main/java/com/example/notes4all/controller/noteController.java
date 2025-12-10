package com.example.notes4all.controller;

import com.example.notes4all.dao.NoteDAO;
import com.example.notes4all.model.Note;
import com.google.cloud.Timestamp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class noteController {

    /* =========================
       UNDO / REDO
       ========================= */
    private final Deque<String> undoStack = new ArrayDeque<>();
    private final Deque<String> redoStack = new ArrayDeque<>();
    private boolean ignoreChanges = false;

    /* =========================
       FIREBASE STATE
       ========================= */
    private Note currentNote;
    private String currentUserId; // set this from login!

    /* =========================
       FXML
       ========================= */
    @FXML private HTMLEditor noteEditor;
    @FXML private TextField noteNameField;
    @FXML private ListView<Note> noteListView;

    @FXML
    public void initialize() {

        // ✅ Undo snapshot
        noteEditor.setOnKeyReleased(e -> {
            if (ignoreChanges) return;

            String html = noteEditor.getHtmlText();
            if (!undoStack.isEmpty() && undoStack.peek().equals(html)) return;

            undoStack.push(html);
            redoStack.clear();
        });

        // ✅ Display note title in ListView
        noteListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                setText(empty || note == null ? null : note.getTitle());
            }
        });
    }

    /* =========================
       LOAD NOTES
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
        undoStack.clear();
        redoStack.clear();
        ignoreChanges = false;

        noteNameField.clear();
        noteListView.getSelectionModel().clearSelection();
    }

    /* =========================
       SAVE NOTE
       ========================= */
    @FXML
    protected void onSaveNoteClick() {
        String title = noteNameField.getText().trim();
        if (title.isEmpty()) {
            showAlert("Please enter a note name.");
            return;
        }

        String html = noteEditor.getHtmlText();

        new Thread(() -> {
            try {
                if (currentNote == null) {
                    // ✅ New note
                    currentNote = new Note(
                            currentUserId,
                            title,
                            html,
                            Timestamp.now(),
                            List.of()
                    );
                    NoteDAO.save(currentNote);
                } else {
                    // ✅ Update
                    NoteDAO.update(
                            currentNote.getNoteId(),
                            title,
                            html
                    );
                    currentNote.setTitle(title);
                    currentNote.setContent(html);
                }

                loadNotes(currentUserId);

                Platform.runLater(() ->
                        showAlert("Saved to Firebase ✅")
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /* =========================
       SELECT NOTE
       ========================= */
    @FXML
    protected void onListClick(javafx.scene.input.MouseEvent event) {
        Note selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (event.getButton() == MouseButton.PRIMARY) {
            openNote(selected);
        }
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
    private void deleteNote(Note note) {
        new Thread(() -> {
            try {
                NoteDAO.delete(note.getNoteId());
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
       ALERT
       ========================= */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notes4All");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







