package com.example.notes4all.controller;

import com.example.notes4all.dao.AuthDAO;
import com.example.notes4all.dao.UserDAO;
import com.example.notes4all.mainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;



    @FXML
    private void handleRegister() {

        String email = emailField.getText();
        String password = passwordField.getText();
        String username = usernameField.getText();

        try {
            String uid = AuthDAO.register(email, password);

            UserDAO.createProfile(uid, email, username);

            mainApplication.setRoot("login.fxml");

        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }





}
