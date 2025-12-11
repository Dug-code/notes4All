package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.service.AuthService;
import com.example.notes4all.service.UserService;
import com.example.notes4all.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            AlertUtil.error("Please enter both email and password.");
            return;
        }

        boolean success = AuthService.login(email, password);

        if (success) {
            AlertUtil.info("Login successful!");
            UserService.updateStatus("Online");

            try {
                App.setRoot("dashboard");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            AlertUtil.error("Login failed. Please check your credentials.");
        }
    }

    @FXML
    private void handleGoToRegister() {
        try {
            App.setRoot("register");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}