package com.example.notes4all.util;

import com.google.gson.*;
import java.io.*;
import java.net.*;

public class FirebaseAuthService {

    private static final String API_KEY = "AIzaSyC_iCeuQwgDiCAT7xZb59huv5ox3c7p1m0";

    public static boolean login(String email, String password) {
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

            System.out.println("Firebase Response: " + response); // ✅ DEBUG LINE

            if (code == 200) {
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                String idToken = json.get("idToken").getAsString();
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

