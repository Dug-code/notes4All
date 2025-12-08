package com.example.notes4all;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class homeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private TextField newFriendField;

    private String currentUsername;
    private ObservableList<String> friends = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        friendsListView.setItems(friends);

        // Right-click menu to remove friend
        friendsListView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("Remove");
            removeItem.setOnAction(e -> {
                String item = cell.getItem();
                friends.remove(item);
                saveFriends();
            });
            contextMenu.getItems().add(removeItem);

            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                cell.setContextMenu(isNowEmpty ? null : contextMenu);
            });
            return cell;
        });
    }

    public void setUser(String username) {
        currentUsername = username;
        welcomeLabel.setText("User: " + username);
        loadFriends();
    }

    @FXML
    public void onAddFriendClick() {
        String friend = newFriendField.getText().trim();
        if (!friend.isEmpty() && !friends.contains(friend)) {
            friends.add(friend);
            newFriendField.clear();
            saveFriends();
        } else {
            showAlert("Friend already added or empty name.");
        }
    }

    private void saveFriends() {
        File file = new File("friends_" + currentUsername + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String friend : friends) {
                writer.write(friend);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFriends() {
        File file = new File("friends_" + currentUsername + ".txt");
        friends.clear();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                List<String> lines = reader.lines().collect(Collectors.toList());
                friends.addAll(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveOnLogout() {
        saveFriends();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notes4All");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ---------------------- Button Handlers ----------------------

    @FXML
    public void onLogoutButtonClick() {
        saveOnLogout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login - Notes4All");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenNotesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("note-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            Stage noteStage = new Stage();
            noteStage.setScene(scene);
            noteStage.setTitle("Notes - Notes4All");
            noteStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOpenProfileClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            Scene scene = new Scene(loader.load(), 500, 400);

            profileController controller = loader.getController();
            controller.setUserData(currentUsername);

            Stage profileStage = new Stage();
            profileStage.setScene(scene);
            profileStage.setTitle("Profile - Notes4All");
            profileStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSendNoteClick() {
        String selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            showAlert("Please select a friend to send a note.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("note-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);

            noteController controller = loader.getController();
           // broke controller.setRecipient(selectedFriend); // pass friend as recipient

            Stage noteStage = new Stage();
            noteStage.setScene(scene);
            noteStage.setTitle("Send Note to " + selectedFriend);
            noteStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




