module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.web;

    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;
    requires com.google.auth;

    // Apache POI – DOCX support
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.xmlbeans;

    // OpenPDF
    requires com.github.librepdf.openpdf;

    opens com.example.notes4all to javafx.fxml;
    exports com.example.notes4all;
}
