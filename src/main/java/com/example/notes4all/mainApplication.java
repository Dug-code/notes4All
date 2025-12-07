package com.example.notes4all;

import com.example.notes4all.util.FirebaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class mainApplication extends Application {
    public static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            FirebaseConfig.initialize();

            URL url = mainApplication.class.getResource("/com/example/notes4all/views/login.fxml"
            );


            if (url == null) {
                throw new RuntimeException("Cannot find: /com.example.notes4all/views/login.fxml");
            }

            Parent root = FXMLLoader.load(url);
            scene = new Scene(root, 700, 400);

            stage.setTitle("Login - Notes4All");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Failed to start app");
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) {
        try {
            Parent root = FXMLLoader.load(
                    mainApplication.class.getResource("/com/example/notes4all/views/" + fxml)
            );
            scene.setRoot(root);
            System.out.println("Loading view: " + fxml);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {launch();}
}