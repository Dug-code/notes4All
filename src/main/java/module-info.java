module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.desktop;

    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires google.cloud.core;
    requires com.google.api.apicommon;
    requires javafx.graphics;
    requires java.logging;
    requires com.google.gson;
    requires java.sql;


    opens com.example.notes4all to javafx.fxml;
    exports com.example.notes4all;
    exports com.example.notes4all.model;
    opens com.example.notes4all.model to javafx.fxml;
    exports com.example.notes4all.controller;
    opens com.example.notes4all.controller to javafx.fxml;
    exports com.example.notes4all.util;
    opens com.example.notes4all.util to javafx.fxml;
}