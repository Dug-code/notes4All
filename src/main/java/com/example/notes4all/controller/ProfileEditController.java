package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.model.User;
import com.example.notes4all.service.UserService;
import com.example.notes4all.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;

public class ProfileEditController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField bioField;

    private User currentUser;

    @FXML
    private void initialize() {
        currentUser = UserService.getCurrentUser();

        if (currentUser == null) {
            AlertUtil.error("Could not load profile.");
            return;
        }

        usernameField.setText(currentUser.getUsername());
        fullNameField.setText(currentUser.getFullName());
        bioField.setText(currentUser.getBio());
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) return;

        currentUser.setUsername(usernameField.getText().trim());
        currentUser.setFullName(fullNameField.getText().trim());
        currentUser.setBio(bioField.getText().trim());

        boolean success = UserService.updateUser(currentUser);

        if (success) {
            AlertUtil.success("Profile updated!");
        } else {
            AlertUtil.error("Failed to update profile.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/com/example/notes4all/views/dashboard.fxml"));
            App.scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
