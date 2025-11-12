module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires com.google.auth;


    opens com.example.notes4all to javafx.fxml;
    exports com.example.notes4all;
}