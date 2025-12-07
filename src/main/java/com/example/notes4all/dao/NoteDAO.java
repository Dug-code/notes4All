package com.example.notes4all.dao;

import com.example.notes4all.model.Note;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class NoteDAO {

    public static void save(Note note)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        db.collection("notes").add(note).get();
    }

    public static List<Note> getMyNotes(String userId)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        QuerySnapshot snapshot = db.collection("notes")
                .whereEqualTo("ownerId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();

        List<Note> notes = new ArrayList<>();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Note note = doc.toObject(Note.class);
            if (note != null) {
                note.setNoteId(doc.getId()); // ✅ store Firestore doc ID
                notes.add(note);
            }
        }

        return notes;
    }

    public static void update(String noteId, String title, String content)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> update = Map.of(
                "title", title,
                "content", content
        );

        db.collection("notes").document(noteId).update(update).get();
    }

    public static void delete(String noteId)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();
        db.collection("notes").document(noteId).delete().get();
    }

    public static void shareNote(String noteId, String friendUid)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        db.collection("notes")
                .document(noteId)
                .update("sharedWith", FieldValue.arrayUnion(friendUid))
                .get();
    }

    public static void unshareNote(String noteId, String friendUid)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        db.collection("notes")
                .document(noteId)
                .update("sharedWith", FieldValue.arrayRemove(friendUid))
                .get();
    }

    public static List<Note> getSharedNotes(String userId)
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        QuerySnapshot snapshot = db.collection("notes")
                .whereArrayContains("sharedWith", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .get();

        List<Note> notes = new ArrayList<>();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Note note = doc.toObject(Note.class);
            if (note != null) {
                note.setNoteId(doc.getId());
                notes.add(note);
            }
        }

        return notes;
    }
}

