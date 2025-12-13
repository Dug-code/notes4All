package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.dao.UserDAO;
import com.example.notes4all.model.User;
import com.example.notes4all.service.AuthService;
import com.example.notes4all.service.Session;
import com.example.notes4all.service.UserService;
import com.example.notes4all.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.time.Instant;
import java.util.ArrayList;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void initialize() {
        emailField.requestFocus();
    }


    @FXML
    private void handleRegister() {

        String fullName = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtil.error("Please fill out all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            AlertUtil.error("Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            AlertUtil.error("Password must be at least 6 characters long.");
            return;
        }

        boolean success = AuthService.register(email, password);

        if (!success) {
            AlertUtil.error("Registration failed. Email may already be in use.");
            return;
        }

        User newUser = new User(
                Session.getUid(),
                username,
                fullName,
                email,
                "Hello, I'm new here!",
                Instant.now().toString(),
                new ArrayList<>()
        );

        boolean saved = UserService.createUser(newUser);

        if (!saved) {
            AlertUtil.error("Failed to save user.");
            return;
        }

        AlertUtil.info("Registration successful!");
        try {
            App.setRoot("dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            App.setRoot("login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}