package com.example.notes4all.model;

import com.google.cloud.Timestamp;
import java.util.List;

public class Note {

    private String noteId;      // optional (can be set manually)
    private String ownerId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private List<String> sharedWith; // list of user UIDs

    // ✅ REQUIRED for Firestore
    public Note() {}

    public Note(String ownerId, String title, String content,
                Timestamp createdAt, List<String> sharedWith) {
        this.ownerId = ownerId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.sharedWith = sharedWith;
    }

    // -------- GETTERS & SETTERS --------

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public boolean isShared() {
        return sharedWith != null && !sharedWith.isEmpty();
    }

    @Override
    public String toString() {
        return title;
    }
}

