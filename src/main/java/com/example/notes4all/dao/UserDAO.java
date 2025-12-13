package com.example.notes4all.dao;

import com.example.notes4all.util.FirebaseConstants;
import com.example.notes4all.model.User;
import com.example.notes4all.service.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static boolean createUserDocument(User user) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL + "/users?documentId=" + user.getUid();

            String body = """
        {
          "fields": {
            "uid": {"stringValue": "%s"},
            "email": {"stringValue": "%s"},
            "username": {"stringValue": "%s"},
            "fullName": {"stringValue": "%s"},
            "bio": {"stringValue": "%s"},
            "createdAt": {"timestampValue": "%s"},
            "friends": {"arrayValue": {"values": []}}
          }
        }
        """.formatted(
                    user.getUid(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getBio(),
                    user.getCreatedAt()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

            System.out.println("CREATE USER RESPONSE: " + response);

            return !response.contains("error");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static User getUserProfile(String uid) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL + "/users/" + uid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .GET()
                    .build();

            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("RAW PROFILE JSON for " + uid + ": " + json);
            JSONObject root = new JSONObject(json);

            JSONObject f = root.getJSONObject("fields");

            String username = f.getJSONObject("username").getString("stringValue");
            String fullName = f.getJSONObject("fullName").getString("stringValue");
            String email = f.getJSONObject("email").getString("stringValue");
            String bio = f.getJSONObject("bio").getString("stringValue");
            String createdAt = f.getJSONObject("createdAt").getString("timestampValue");

            // Friends array
            List<String> friends = new ArrayList<>();
            if (f.getJSONObject("friends").getJSONObject("arrayValue").has("values")) {
                JSONArray arr = f.getJSONObject("friends").getJSONObject("arrayValue").getJSONArray("values");
                for (int i = 0; i < arr.length(); i++) {
                    friends.add(arr.getJSONObject(i).getString("stringValue"));
                }
            }



            return new User(uid, username, fullName, email, bio, createdAt, friends);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean updateUserProfile(User user) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL +
                    "/users/" + user.getUid() +
                    "?updateMask.fieldPaths=username" +
                    "&updateMask.fieldPaths=fullName" +
                    "&updateMask.fieldPaths=bio";

            String body = """
        {
          "fields": {
            "username": {"stringValue": "%s"},
            "fullName": {"stringValue": "%s"},
            "bio": {"stringValue": "%s"}
          }
        }
        """.formatted(
                    user.getUsername(),
                    user.getFullName(),
                    user.getBio()
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("UPDATE RESPONSE: " + response);

            return !response.contains("error");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUserByUsername(String username) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL + ":runQuery";

            String body = """
        {
          "structuredQuery": {
            "from": [{"collectionId": "users"}],
            "where": {
              "fieldFilter": {
                "field": {"fieldPath": "username"},
                "op": "EQUAL",
                "value": {"stringValue": "%s"}
              }
            }
          }
        }
        """.formatted(username);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JSONArray arr = new JSONArray(response);

            if (arr.length() == 0 || arr.getJSONObject(0).isEmpty()) return null;

            JSONObject doc = arr.getJSONObject(0).getJSONObject("document");
            return parseUser(doc);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addFriend(String myUid, String friendUid) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL +
                    "/users/" + myUid +
                    "?updateMask.fieldPaths=friends";

            String body = """
        {
          "fields": {
            "friends": {
              "arrayValue": {
                "values": [
                  { "stringValue": "%s" }
                ]
              }
            }
          }
        }
        """.formatted(friendUid);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("ADD FRIEND RESPONSE: " + response);

            return !response.contains("error");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }





    private static User parseUser(JSONObject root) {
        try {
            JSONObject fields = root.getJSONObject("fields");

            String uid = getString(fields, "uid");
            String username = getString(fields, "username");
            String fullName = getString(fields, "fullName");
            String email = getString(fields, "email");
            String bio = getString(fields, "bio");
            String createdAt = getString(fields, "createdAt");

            // Parse friendUids array
            List<String> friendUids = new ArrayList<>();

            if (fields.has("friends")) {
                JSONObject arrayObj = fields.getJSONObject("friends").getJSONObject("arrayValue");

                if (arrayObj.has("values")) {
                    JSONArray arr = arrayObj.getJSONArray("values");
                    for (int i = 0; i < arr.length(); i++) {
                        friendUids.add(arr.getJSONObject(i).getString("stringValue"));
                    }
                }
            }

            return new User(
                    uid,
                    username,
                    fullName,
                    email,
                    bio,
                    createdAt,
                    friendUids
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getString(JSONObject fields, String key) {
        if (!fields.has(key)) return "";
        JSONObject obj = fields.getJSONObject(key);
        return obj.optString("stringValue", obj.optString("timestampValue", ""));
    }

    public static String getUidByUsername(String username) {
        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL +
                    ":runQuery";

            String body = """
        {
          "structuredQuery": {
            "from": [{ "collectionId": "users" }],
            "where": {
              "fieldFilter": {
                "field": { "fieldPath": "username" },
                "op": "EQUAL",
                "value": { "stringValue": "%s" }
              }
            },
            "limit": 1
          }
        }
        """.formatted(username);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("UID LOOKUP RESPONSE: " + json);

            JSONArray arr = new JSONArray(json);

            if (arr.isEmpty())
                return null;

            JSONObject docObj = arr.getJSONObject(0).optJSONObject("document");
            if (docObj == null)
                return null;

            JSONObject fields = docObj.getJSONObject("fields");

            // UID is the documentId OR stored inside fields
            return fields.getJSONObject("uid").getString("stringValue");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
