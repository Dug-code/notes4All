module com.example.notes4all {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.notes4all to javafx.fxml;
    exports com.example.notes4all;
}