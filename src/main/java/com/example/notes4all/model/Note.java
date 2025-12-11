package com.example.notes4all.model;

import java.util.List;

public class Note {
    private String noteId;
    private String ownerUid;
    private String title;
    private String content;
    private String createdAt;
    private List<String> sharedWith;

    public Note(String noteId,
                String ownerUid,
                String title,
                String content,
                String createdAt,
                List<String> sharedWith) {
        this.noteId = noteId;
        this.ownerUid = ownerUid;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.sharedWith = sharedWith;
    }

    // convenience constructor for new notes before noteId is known
    public Note(String ownerUid,
                String title,
                String content,
                String createdAt,
                List<String> sharedWith) {
        this(null, ownerUid, title, content, createdAt, sharedWith);
    }

    public String getNoteId() { return noteId; }
    public String getOwnerUid() { return ownerUid; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public List<String> getSharedWith() { return sharedWith; }

    public void setNoteId(String noteId) { this.noteId = noteId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}
