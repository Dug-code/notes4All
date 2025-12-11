package com.example.notes4all.dao;

import com.example.notes4all.model.Note;
import com.example.notes4all.util.FirebaseConstants;
import com.example.notes4all.service.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteDAO {

    private static final HttpClient client = HttpClient.newHttpClient();

    /* ==========================
       CREATE NOTE
       ========================== */
    public static void save(Note note) throws Exception {

        String docId = note.getOwnerUid() + "_" + UUID.randomUUID();
        note.setNoteId(docId);

        String url = FirebaseConstants.FIRESTORE_BASE_URL + "/notes?documentId=" + docId;

        String escapedTitle = note.getTitle().replace("\"", "\\\"");
        String escapedContent = note.getContent().replace("\"", "\\\"");

        String body = """
    {
      "fields": {
        "ownerUid": {"stringValue": "%s"},
        "title": {"stringValue": "%s"},
        "content": {"stringValue": "%s"},
        "createdAt": {"timestampValue": "%s"},
        "sharedWith": {"arrayValue": {"values": []}}
      }
    }
    """.formatted(
                note.getOwnerUid(),
                escapedTitle,
                escapedContent,
                Instant.now().toString()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Session.getIdToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("SAVE NOTE RESPONSE: " + response.body());
    }


    /* ==========================
       GET NOTES FOR USER
       ========================== */
    public static List<Note> getMyNotes(String uid) throws Exception {
        String url = FirebaseConstants.FIRESTORE_BASE_URL + ":runQuery";

        String body = """
    {
      "structuredQuery": {
        "from": [{ "collectionId": "notes" }],
        "where": {
          "fieldFilter": {
            "field": { "fieldPath": "ownerUid" },
            "op": "EQUAL",
            "value": { "stringValue": "%s" }
          }
        }
      }
    }
    """.formatted(uid);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Session.getIdToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JSONArray arr = new JSONArray(json);

        List<Note> notes = new java.util.ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (!obj.has("document")) continue;
            JSONObject doc = obj.getJSONObject("document");
            notes.add(parseNote(doc));
        }
        return notes;
    }


    /* ==========================
       UPDATE NOTE
       ========================== */
    public static void update(String noteId, String title, String content) throws Exception {
        String url = FirebaseConstants.FIRESTORE_BASE_URL + "/notes/" + noteId + "?updateMask.fieldPaths=title&updateMask.fieldPaths=content";

        String escapedTitle = title.replace("\"", "\\\"");
        String escapedContent = content.replace("\"", "\\\"");

        String body = """
    {
      "fields": {
        "title": {"stringValue": "%s"},
        "content": {"stringValue": "%s"}
      }
    }
    """.formatted(escapedTitle, escapedContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Session.getIdToken())
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .build();

        String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println("UPDATE RESPONSE: " + json);
    }


    /* ==========================
       DELETE NOTE
       ========================== */
    public static void delete(String noteId) throws Exception {
        String url = FirebaseConstants.FIRESTORE_BASE_URL + "/notes/" + noteId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + Session.getIdToken())
                .DELETE()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public static boolean shareNoteWithUser(String noteId, String username) {
        try {
            // 1. Get user UID from username
            String targetUid = UserDAO.getUidByUsername(username);
            if (targetUid == null) return false;

            // 2. Build Firestore patch URL
            String url = FirebaseConstants.FIRESTORE_BASE_URL + "/notes/" + noteId + "?updateMask.fieldPaths=sharedWith";

            // 3. BODY: append the UID
            String body = """
        {
          "fields": {
            "sharedWith": {
              "arrayValue": {
                "values": [
                  {"stringValue": "%s"}
                ]
              }
            }
          }
        }
        """.formatted(targetUid);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            System.out.println("SHARE RESPONSE: " + response);

            return !response.contains("error");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Note> getSharedWithMe(String uid) {
        List<Note> notes = new java.util.ArrayList<>();

        try {
            String url = FirebaseConstants.FIRESTORE_BASE_URL + ":runQuery";

            String body = """
        {
          "structuredQuery": {
            "from": [{ "collectionId": "notes" }],
            "where": {
              "fieldFilter": {
                "field": { "fieldPath": "sharedWith" },
                "op": "ARRAY_CONTAINS",
                "value": { "stringValue": "%s" }
              }
            }
          }
        }
        """.formatted(uid);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + Session.getIdToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            String json = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (!obj.has("document")) continue;
                JSONObject doc = obj.getJSONObject("document");
                notes.add(parseNote(doc));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return notes;
    }



    /* ==========================
       PARSER
       ========================== */
    private static Note parseNote(JSONObject doc) {
        String fullName = doc.getString("name");
        String noteId = fullName.substring(fullName.lastIndexOf("/") + 1);

        JSONObject f = doc.getJSONObject("fields");

        String ownerUid = f.getJSONObject("ownerUid").getString("stringValue");
        String title = f.getJSONObject("title").getString("stringValue");
        String content = f.getJSONObject("content").getString("stringValue");
        String createdAt = f.getJSONObject("createdAt").getString("timestampValue");

        // sharedWith array
        List<String> shared = new java.util.ArrayList<>();
        if (f.has("sharedWith")) {
            JSONObject arrObj = f.getJSONObject("sharedWith").getJSONObject("arrayValue");
            if (arrObj.has("values")) {
                JSONArray values = arrObj.getJSONArray("values");
                for (int i = 0; i < values.length(); i++) {
                    shared.add(values.getJSONObject(i).getString("stringValue"));
                }
            }
        }

        return new Note(
                noteId,
                ownerUid,
                title,
                content,
                createdAt,
                shared
        );
    }

}
