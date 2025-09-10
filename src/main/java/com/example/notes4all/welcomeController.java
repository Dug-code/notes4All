package com.example.notes4all;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class welcomeController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to Notes4All!");
    }
}