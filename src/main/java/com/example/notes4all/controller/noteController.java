package com.example.notes4all.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class noteController {

    @FXML
    private ColorPicker textColorPicker;


    @FXML
    private TextArea noteArea;

    @FXML
    private TextField noteNameField;

    @FXML
    private ListView<String> noteListView;

    private final File notesFolder = new File("notes");

    private String recipient;

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }


    @FXML
    public void initialize() {
        if (!notesFolder.exists()) {
            notesFolder.mkdirs();
        }
        loadNoteList();
    }

    @FXML
    protected void onNewNoteClick() {
        noteArea.clear();
        noteNameField.clear();
        noteListView.getSelectionModel().clearSelection();
    }

    @FXML
    protected void onSaveNoteClick() {
        String content = noteArea.getText();
        String noteName = noteNameField.getText().trim();

        if (noteName.isEmpty()) {
            showAlert("Please enter a name.");
            return;
        }

        if (!noteName.endsWith(".txt")) {
            noteName += ".txt";
        }

        File file = new File(notesFolder, noteName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            showAlert("Saved as: " + noteName);
            loadNoteList();
        } catch (IOException e) {
            showAlert("Error saving note: " + e.getMessage());
        }
    }

    private void loadNoteList() {
        noteListView.getItems().clear();

        File[] files = notesFolder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File f : files) {
                noteListView.getItems().add(f.getName());
            }
        }
    }

    // Load or show delete menu
    @FXML
    protected void onListClick(javafx.scene.input.MouseEvent event) {
        String selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Right-click → Delete menu
        if (event.getButton() == MouseButton.SECONDARY) {
            ContextMenu menu = new ContextMenu();

            MenuItem delete = new MenuItem("Delete Note");
            delete.setOnAction(e -> deleteNote(selected));

            menu.getItems().add(delete);
            menu.show(noteListView, event.getScreenX(), event.getScreenY());
            return;
        }

        // Left-click → Load note
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
            loadNote(selected);
        }
    }

    private void loadNote(String name) {
        File selectedFile = new File(notesFolder, name);

        try {
            String content = new String(Files.readAllBytes(selectedFile.toPath()));
            noteArea.setText(content);
            noteNameField.setText(name.replace(".txt", ""));
        } catch (IOException e) {
            showAlert("Error loading: " + e.getMessage());
        }
    }

    private void deleteNote(String name) {
        File f = new File(notesFolder, name);

        if (f.delete()) {
            showAlert("Deleted: " + name);
            loadNoteList();

            // Clear if user deleted the open note
            if (noteNameField.getText().equals(name.replace(".txt", ""))) {
                noteArea.clear();
                noteNameField.clear();
            }
        } else {
            showAlert("Could not delete the note.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notes4All");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void onColorChange() {
        String color = toRgbString(textColorPicker.getValue());
        noteArea.setStyle("-fx-text-fill: " + color + ";");
    }

    private String toRgbString(javafx.scene.paint.Color c) {
        return "rgb(" +
                (int)(c.getRed() * 255) + "," +
                (int)(c.getGreen() * 255) + "," +
                (int)(c.getBlue() * 255) +
                ")";
    }


}






