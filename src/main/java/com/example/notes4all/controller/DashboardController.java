package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.dao.NoteDAO;
import com.example.notes4all.dao.UserDAO;
import com.example.notes4all.model.Note;
import com.example.notes4all.model.User;
import com.example.notes4all.service.Session;
import com.example.notes4all.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> friendsListView;

    private List<String> friendUids = new ArrayList<>();


    private User currentUser;

    @FXML
    private TextField addFriendField;

    @FXML
    private ListView<Note> dashboardNotesList;


    @FXML
    private StackPane contentPane;

    @FXML
    private void initialize() {
        loadCurrentUser();
        loadFriends();
        loadDashboardNotes();
    }

    private void loadCurrentUser() {
        currentUser = UserService.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername() + "!");
        }
    }

    private void loadFriends() {
        new Thread(() -> {
            try {
                User user = UserDAO.getUserProfile(Session.getUid());

                friendUids = user.getFriends();  // <-- Make sure UserDAO uses correct field name!

                if (friendUids == null) friendUids = new ArrayList<>();

                List<String> friendNames = new ArrayList<>();

                for (String uid : friendUids) {
                    User friend = UserDAO.getUserProfile(uid);
                    if (friend != null)
                        friendNames.add(friend.getUsername());
                }

                Platform.runLater(() -> friendsListView.getItems().setAll(friendNames));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void loadDashboardNotes() {
        new Thread(() -> {
            try {
                List<Note> myNotes = NoteDAO.getMyNotes(Session.getUid());
                List<Note> sharedNotes = NoteDAO.getSharedWithMe(Session.getUid());

                List<Note> all = new java.util.ArrayList<>();
                all.addAll(myNotes);
                all.addAll(sharedNotes);

                Platform.runLater(() -> {
                    dashboardNotesList.getItems().setAll(all);

                    dashboardNotesList.setCellFactory(list -> new ListCell<>() {
                        @Override
                        protected void updateItem(Note note, boolean empty) {
                            super.updateItem(note, empty);
                            if (empty || note == null) {
                                setText(null);
                            } else {
                                String label = note.getTitle();
                                if (!note.getOwnerUid().equals(Session.getUid())) {
                                    label += " (shared)";
                                }
                                setText(label);
                            }
                        }
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }





    // ========== LOGOUT ==========
    @FXML
    private void handleLogout() {
        Session.clear();
        UserService.updateStatus("Offline");
        App.setRoot("login");
    }

    @FXML
    private void handleEditProfile() {
        App.setRoot("profile-edit");  // No .fxml extension
    }


    @FXML
    private void handleAddFriend() {
        String targetUsername = addFriendField.getText().trim();

        if (targetUsername.isEmpty()) {
            System.out.println("No username entered");
            return;
        }

        if (currentUser == null) return;

        // Don't allow adding yourself
        if (targetUsername.equals(currentUser.getUsername())) {
            System.out.println("You cannot add yourself.");
            return;
        }

        // 1. Lookup user by username
        User friend = UserService.getUserByUsername(targetUsername);

        if (friend == null) {
            System.out.println("User not found: " + targetUsername);
            return;
        }

        // 2. Add UID to friend list
        boolean success = UserService.addFriend(currentUser.getUid(), friend.getUid());

        if (success) {
            System.out.println("Friend added!");
            loadFriends();
        } else {
            System.out.println("Failed to add friend.");
        }
    }


    @FXML
    private void handleFriendClick() {
        int index = friendsListView.getSelectionModel().getSelectedIndex();

        if (index < 0) {
            System.out.println("No friend selected.");
            return;
        }

        if (friendUids == null || friendUids.isEmpty()) {
            System.out.println("friendUids is empty — cannot open profile.");
            return;
        }

        if (index >= friendUids.size()) {
            System.out.println("Index mismatch: index = " + index +
                    " size = " + friendUids.size());
            return;
        }

        String uid = friendUids.get(index);
        openFriendProfile(uid);
    }


    private void openFriendProfile(String uid) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(
                    "/com/example/notes4all/views/profile-view.fxml"
            ));
            Parent view = loader.load();

            ProfileViewController controller = loader.getController();
            controller.setViewedUser(uid);
            controller.loadProfile();

            App.setSceneRoot(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleNewNoteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/example/notes4all/views/note.fxml"));
            Parent view = loader.load();

            NoteController controller = loader.getController();
            controller.loadNotes(Session.getUid());

            // Replace entire scene (recommended)
            App.setSceneRoot(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboardNoteClick() {
        Note selected = dashboardNotesList.getSelectionModel().getSelectedItem();
        System.out.println("Clicked: " + selected);

        if (selected == null) return;

        openNoteInEditor(selected);
    }

    private void openNoteInEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/example/notes4all/views/note.fxml"));
            Parent view = loader.load();

            NoteController controller = loader.getController();
            controller.loadNotes(Session.getUid());
            Platform.runLater(() -> controller.openNoteFromDashboard(note));

            App.setSceneRoot(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
