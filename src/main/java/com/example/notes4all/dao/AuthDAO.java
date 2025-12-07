package com.example.notes4all.dao;
import com.example.notes4all.mainApplication;
import com.example.notes4all.util.FirebaseAuthService;
import com.example.notes4all.util.Session;
import com.google.firebase.auth.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthDAO {

    private static final String API_KEY = "AIzaSyC_iCeuQwgDiCAT7xZb59huv5ox3c7p1m0";


    public static String register(String email, String password) throws Exception {

        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("Email is required");

        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters");

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setEmailVerified(false)
                .setDisabled(false);

        UserRecord user = FirebaseAuth.getInstance().createUser(request);

        return user.getUid();
    }

    public static boolean login(String email, String password) {

        if (email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            return false;
        }

        try {
            String endpoint =
                    "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String payload = "{"
                    + "\"email\":\"" + email + "\","
                    + "\"password\":\"" + password + "\","
                    + "\"returnSecureToken\":true"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
            }

            int code = conn.getResponseCode();

            InputStream stream =
                    (code == 200) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            if (code == 200) {
                JsonObject json =
                        JsonParser.parseString(response.toString()).getAsJsonObject();

                Session.userId  = json.get("localId").getAsString();
                Session.email   = json.get("email").getAsString();
                Session.idToken = json.get("idToken").getAsString();

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void logout() {
        Session.userId = null;
        Session.email = null;
        Session.idToken = null;
    }

    public static boolean isLoggedIn() {
        return Session.userId != null &&
                Session.email != null &&
                Session.idToken != null;
    }
}
