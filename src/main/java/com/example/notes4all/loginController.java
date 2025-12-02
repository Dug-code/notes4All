package com.example.notes4all;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class loginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    @FXML
    protected void onLoginButtonClick() throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Static users for testing
        if ((email.equals("admin") && password.equals("1234")) ||
                (email.equals("testuser") && password.equals("abcd"))) {

            // Load home page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            Scene scene = new Scene(loader.load(), 500, 350);

            // Get the controller and pass the username
            homeController controller = loader.getController();
            controller.setUser(email);

            // Switch the stage to home
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Home - Notes4All");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    void onRegisterButtonClick(ActionEvent event) {
        registerUser();
    }

    private boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(emailField.getText())
                .setEmailVerified(false)
                .setPassword(passwordField.getText())
                .setDisplayName("John Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = mainApplication.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }
    }



    }

