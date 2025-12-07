package com.example.notes4all.controller;

import com.example.notes4all.mainApplication;
import com.example.notes4all.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class dashboardController {

    @FXML
    private StackPane contentPane;

    // ========== LOAD NOTES ==========
    @FXML
    private void openNotes() {
        loadView("note.fxml");
    }

    // ========== LOAD PROFILE ==========
    @FXML
    private void openProfile() {
        loadView("profile.fxml");
    }

    // ========== LOAD FRIENDS ==========
//    @FXML
//    private void openFriends() {
//        loadView("friends.fxml");
//    }

    // ========== LOAD ADD FRIEND ==========
//    @FXML
//    private void openAddFriend() {
//        loadView("add-friend-view.fxml");
//    }

    // ========== LOGOUT ==========
    @FXML
    private void handleLogout() {
        Session.userId = null;
        Session.email = null;
        Session.idToken = null;

        mainApplication.setRoot("login-view.fxml");
    }

    // ========== INTERNAL LOADER ==========
    private void loadView(String fxml) {
        try {
            Parent view = FXMLLoader.load(
                    mainApplication.class.getResource("/com/example/notes4all/views/" + fxml)
            );

            contentPane.getChildren().setAll(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
