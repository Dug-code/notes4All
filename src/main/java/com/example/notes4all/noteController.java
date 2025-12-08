package com.example.notes4all;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.scene.paint.Color;

import org.apache.poi.xwpf.usermodel.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class noteController {

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private TextField noteNameField;

    @FXML
    private ListView<String> noteListView;

    @FXML
    private ColorPicker textColorPicker;

    @FXML
    private ComboBox<Integer> fontSizeBox;

    @FXML
    private ComboBox<String> fontFamilyBox;

    @FXML
    private TextField searchField;

    private final File notesFolder = new File("notes");

    private Timer autoSaveTimer = new Timer();

    @FXML
    public void initialize() {

        if (!notesFolder.exists()) notesFolder.mkdirs();
        loadNoteList();

        fontSizeBox.getItems().addAll(10, 12, 14, 16, 18, 22, 28, 32, 40);
        fontFamilyBox.getItems().addAll(
                "Arial", "Verdana", "Times New Roman",
                "Georgia", "Courier New", "Tahoma", "Comic Sans MS"
        );

        startAutoSave();
    }

    // -----------------------
    // AUTO SAVE
    // -----------------------
    private void startAutoSave() {
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!noteNameField.getText().trim().isEmpty())
                    saveHTMLSilently();
            }
        }, 5000, 5000); // every 5 seconds
    }

    private void saveHTMLSilently() {
        try {
            String name = noteNameField.getText().trim() + ".html";
            FileWriter writer = new FileWriter(new File(notesFolder, name));
            writer.write(htmlEditor.getHtmlText());
            writer.close();
        } catch (Exception ignored) {}
    }

    // -----------------------
    // NEW NOTE
    // -----------------------
    @FXML
    protected void onNewNoteClick() {
        htmlEditor.setHtmlText("");
        noteNameField.clear();
        noteListView.getSelectionModel().clearSelection();
    }

    // -----------------------
    // SAVE AS HTML
    // -----------------------
    @FXML
    protected void onSaveNoteClick() {
        String noteName = noteNameField.getText().trim();
        if (noteName.isEmpty()) {
            showAlert("Please enter a name.");
            return;
        }

        if (!noteName.endsWith(".html")) noteName += ".html";

        File file = new File(notesFolder, noteName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(htmlEditor.getHtmlText());
            showAlert("Saved as: " + noteName);
            loadNoteList();
        } catch (IOException e) {
            showAlert("Error saving note: " + e.getMessage());
        }
    }

    // -----------------------
    // SAVE AS DOCX
    // -----------------------
    @FXML
    protected void onSaveAsDocxClick() {
        String name = noteNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Enter a name first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(name + ".docx");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            XWPFDocument doc = new XWPFDocument();
            XWPFParagraph p = doc.createParagraph();
            XWPFRun r = p.createRun();

            // Strip HTML Tags
            String textOnly = htmlEditor.getHtmlText()
                    .replaceAll("<[^>]*>", "")
                    .replace("&nbsp;", " ");

            r.setText(textOnly);

            doc.write(new FileOutputStream(file));
            doc.close();
            showAlert("Saved as DOCX.");
        } catch (Exception e) {
            showAlert("DOCX save error: " + e.getMessage());
        }
    }

    // -----------------------
    // SAVE AS PDF
    // -----------------------
    @FXML
    protected void onSaveAsPDFClick() {
        String name = noteNameField.getText().trim();

        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(name + ".pdf");

        File file = chooser.showSaveDialog(null);
        if (file == null) return;

        try {
            com.lowagie.text.Document pdf = new com.lowagie.text.Document();
            com.lowagie.text.pdf.PdfWriter.getInstance(pdf, new FileOutputStream(file));
            pdf.open();

            String text = htmlEditor.getHtmlText()
                    .replaceAll("<[^>]*>", "")
                    .replace("&nbsp;", " ");

            pdf.add(new com.lowagie.text.Paragraph(text));
            pdf.close();

            showAlert("Saved as PDF.");
        } catch (Exception e) {
            showAlert("PDF error: " + e.getMessage());
        }
    }


    // -----------------------
    // LIST + LOAD + DELETE
    // -----------------------
    private void loadNoteList() {
        noteListView.getItems().clear();
        File[] files = notesFolder.listFiles((d, n) -> n.endsWith(".html"));
        if (files != null)
            for (File f : files)
                noteListView.getItems().add(f.getName());
    }

    @FXML
    protected void onListClick(javafx.scene.input.MouseEvent event) {
        String selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (event.getButton() == MouseButton.SECONDARY) {
            ContextMenu menu = new ContextMenu();
            MenuItem delete = new MenuItem("Delete Note");
            delete.setOnAction(e -> deleteNote(selected));
            menu.getItems().add(delete);
            menu.show(noteListView, event.getScreenX(), event.getScreenY());
            return;
        }

        if (event.getButton() == MouseButton.PRIMARY)
            loadNote(selected);
    }

    private void loadNote(String name) {
        File file = new File(notesFolder, name);
        try {
            String content = Files.readString(file.toPath());
            htmlEditor.setHtmlText(content);
            noteNameField.setText(name.replace(".html", ""));
        } catch (Exception e) {
            showAlert("Error loading note.");
        }
    }

    private void deleteNote(String name) {
        File f = new File(notesFolder, name);
        if (f.delete()) {
            showAlert("Deleted: " + name);
            loadNoteList();
            htmlEditor.setHtmlText("");
            noteNameField.clear();
        }
    }

    // -----------------------
    // RICH TEXT CONTROLS
    // -----------------------
    @FXML
    protected void onColorChange() {
        String rgb = toRGB(textColorPicker.getValue());
        htmlEditor.setHtmlText(htmlEditor.getHtmlText() +
                "<span style='color:" + rgb + ";'></span>");
    }

    @FXML
    protected void onFontSize() {
        Integer s = fontSizeBox.getValue();
        if (s != null)
            htmlEditor.setHtmlText(htmlEditor.getHtmlText() +
                    "<span style='font-size:" + s + "px;'></span>");
    }

    @FXML
    protected void onFontFamily() {
        String f = fontFamilyBox.getValue();
        if (f != null)
            htmlEditor.setHtmlText(htmlEditor.getHtmlText() +
                    "<span style='font-family:" + f + ";'></span>");
    }

    @FXML
    protected void onInsertImageClick() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Insert Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File img = chooser.showOpenDialog(null);
        if (img != null) {
            htmlEditor.setHtmlText(
                    htmlEditor.getHtmlText() +
                            "<br><img src='file:" + img.getAbsolutePath() + "' style='max-width:100%;'><br>"
            );
        }
    }

    // -----------------------
    // SEARCH FEATURE
    // -----------------------
    @FXML
    protected void onSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) return;

        String html = htmlEditor.getHtmlText();
        html = html.replaceAll(
                "(?i)" + query,
                "<span style='background:yellow;'>" + query + "</span>"
        );

        htmlEditor.setHtmlText(html);
    }

    // -----------------------
    // LIGHT/DARK MODE
    // -----------------------
    @FXML
    protected void onThemeToggle() {
        boolean dark = htmlEditor.getStyle().contains("background:#222;");

        if (dark) {
            htmlEditor.setStyle("-fx-background-color:#fff; -fx-text-fill:#000;");
        } else {
            htmlEditor.setStyle("-fx-background-color:#222; -fx-text-fill:#fff;");
        }
    }

    // -----------------------
    // UTILS
    // -----------------------
    private String toRGB(Color c) {
        return "rgb(" + (int)(c.getRed()*255) + "," +
                (int)(c.getGreen()*255) + "," +
                (int)(c.getBlue()*255) + ")";
    }

    private void showAlert(String text) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(text);
        a.showAndWait();
    }
}







