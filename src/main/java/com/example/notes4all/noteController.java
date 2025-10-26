package com.example.notes4all;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class noteController {

    @FXML
    private TextArea noteArea;

    @FXML
    private Canvas drawCanvas;

    private GraphicsContext gc;

    @FXML
    public void initialize() {
        // Initialize the canvas for drawing
        gc = drawCanvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Mouse pressed starts a path
        drawCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        // Mouse dragged draws line
        drawCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        });
    }

    // Clear both text and drawing
    @FXML
    protected void onNewNoteClick() {
        noteArea.clear();
        gc.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
    }

    // Save text to a file
    @FXML
    protected void onSaveNoteClick() {
        String content = noteArea.getText();
        try (FileWriter writer = new FileWriter("note.txt")) {
            writer.write(content);
            showAlert("Note text saved successfully!");
        } catch (IOException e) {
            showAlert("Error saving note: " + e.getMessage());
        }
    }

    // Clear only drawing
    @FXML
    protected void onClearDrawingClick() {
        gc.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
    }

    // Save drawing as PNG
    @FXML
    protected void onSaveDrawingClick() {
        WritableImage image = drawCanvas.snapshot(null, null);
        File file = new File("drawing.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            showAlert("Drawing saved as drawing.png!");
        } catch (IOException e) {
            showAlert("Error saving drawing: " + e.getMessage());
        }
    }

    // Helper to show messages
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notes4All");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


