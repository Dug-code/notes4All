package com.example.notes4all.dao;

import com.example.notes4all.util.FirebaseConstants;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthDAO {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static JSONObject login(String email, String password) throws Exception{
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                + FirebaseConstants.API_KEY;

        String jsonBody = """
                {
                  "email": "%s",
                  "password": "%s",
                  "returnSecureToken": true
                }
                """.formatted(email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        return new JSONObject(response);
    }

    public static JSONObject register(String email, String password) throws Exception {

        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                + FirebaseConstants.API_KEY;

        String jsonBody = """
                {
                  "email": "%s",
                  "password": "%s",
                  "returnSecureToken": true
                }
                """.formatted(email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        return new JSONObject(response);
    }
}
