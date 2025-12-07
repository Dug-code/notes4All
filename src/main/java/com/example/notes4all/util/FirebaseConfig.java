package com.example.notes4all.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLOutput;

public class FirebaseConfig {
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) return;
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/com/example/notes4all/key.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            initialized = true;
            System.out.println("Firebase initialized");
        } catch (Exception e) {
            System.out.println("Firebase failed to initialize");
            e.printStackTrace();

        }
    }
}