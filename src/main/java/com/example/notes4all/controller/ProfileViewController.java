package com.example.notes4all.controller;

import com.example.notes4all.App;
import com.example.notes4all.dao.UserDAO;
import com.example.notes4all.model.User;
import com.example.notes4all.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ProfileViewController {

    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label bioLabel;
    @FXML private Label createdAtLabel;

    private String viewedUserUid;

    public void setViewedUser(String uid) {
        this.viewedUserUid = uid;
    }

    void loadProfile() {
        User user = UserService.getUserProfile(viewedUserUid);

        usernameLabel.setText(user.getUsername());
        fullNameLabel.setText(user.getFullName());
        bioLabel.setText(user.getBio());

        // FORMAT CREATED DATE
        try {
            Instant instant = Instant.parse(user.getCreatedAt());
            LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            String formatted = date.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            createdAtLabel.setText(formatted);
        } catch (Exception e) {
            createdAtLabel.setText("Unknown");
        }
    }


    @FXML
    private void onBackClick() {
        App.setRoot("dashboard");
    }
}
