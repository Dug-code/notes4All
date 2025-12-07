package com.example.notes4all.dao;

import com.example.notes4all.model.User;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserDAO {

    public static void createProfile(String uid, String email, String username)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("username", username);
        data.put("createdAt", Timestamp.now());
        data.put("status", "offline");

        db.collection("users").document(uid).set(data).get();
    }

    public static User getById(String uid)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot doc =
                db.collection("users").document(uid).get().get();

        if (doc.exists()) {
            return doc.toObject(User.class);
        }

        return null;
    }

    public static void updateUsername(String uid, String newUsername)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> update = new HashMap<>();
        update.put("username", newUsername);

        db.collection("users").document(uid).update(update).get();
    }

    public static void updateStatus(String uid, String status)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> update = new HashMap<>();
        update.put("status", status);

        db.collection("users").document(uid).update(update).get();
    }
}
