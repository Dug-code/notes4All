package com.example.notes4all.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class profileController {

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea bioArea;

    @FXML
    private Label statusLabel;

    private String username;

    // Method to set user data when profile is opened
    public void setUserData(String username) {
        this.username = username;
        usernameLabel.setText(username);

        // Load existing profile data (in a real app, this would come from a database)
        loadProfileData();
    }

    private void loadProfileData() {
        // Placeholder: In a real application, you'd load this from a database
        // For now, we'll set some example data
        fullNameField.setText("Admin User");
        emailField.setText("admin@notes4all.com");
        bioArea.setText("I love taking notes!");
    }

    @FXML
    protected void onSaveProfileClick() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String bio = bioArea.getText();

        // Validate input
        if (fullName.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Full name and email are required!");
            return;
        }

        // Save profile data (in a real app, this would save to a database)
        saveProfileData(fullName, email, bio);

        statusLabel.setText("Profile updated successfully!");

        // Clear status message after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> statusLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveProfileData(String fullName, String email, String bio) {
        // Placeholder: In a real application, you'd save this to a database
        System.out.println("Saving profile for: " + username);
        System.out.println("Full Name: " + fullName);
        System.out.println("Email: " + email);
        System.out.println("Bio: " + bio);
    }

    @FXML
    protected void onCloseClick() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
