package com.example.notes4all.controller;

import com.example.notes4all.dao.AuthDAO;
import com.example.notes4all.util.FirebaseAuthService;
import com.example.notes4all.mainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class loginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {

        String email = emailField.getText();
        String password = passwordField.getText();

        boolean success = AuthDAO.login(email, password);
        if (success) {
            mainApplication.setRoot("dashboard.fxml");
        }
        else {
            errorLabel.setText("Invalid email or password.");
        }

    }


    @FXML
    private void goToRegister() {
        mainApplication.setRoot("register.fxml");
    }





}