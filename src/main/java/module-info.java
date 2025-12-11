module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires org.json;
    requires java.net.http;
    requires com.github.librepdf.openpdf;


    opens com.example.notes4all to javafx.fxml;
    opens com.example.notes4all.controller to javafx.fxml;
    opens com.example.notes4all.util to javafx.fxml;

    exports com.example.notes4all;
    exports com.example.notes4all.model;
    exports com.example.notes4all.controller;
    exports com.example.notes4all.util;
}