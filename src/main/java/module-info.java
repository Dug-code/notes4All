module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens com.example.notes4all to javafx.fxml;
    exports com.example.notes4all;
}