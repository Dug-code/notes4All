package com.example.notes4all;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.IOException;

public class homeController {

    @FXML
    private Label welcomeLabel;

    private String currentUsername;

    public void setUser(String username) {
        welcomeLabel.setText("User: " + username);
    }

    @FXML
    protected void onLogoutButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login - Notes4All");
    }

    @FXML
    protected void onOpenNotesClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("note-view.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);

        Stage noteStage = new Stage();
        noteStage.setTitle("Notes - Notes4All");
        noteStage.setScene(scene);
        noteStage.show();
    }

    @FXML
    protected void onOpenProfileClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
        Scene scene = new Scene(loader.load(), 500, 400);

        // Get the controller and pass the username
        profileController controller = loader.getController();
        controller.setUserData(currentUsername);

        Stage profileStage = new Stage();
        profileStage.setTitle("Profile - Notes4All");
        profileStage.setScene(scene);
        profileStage.show();
    }
}

