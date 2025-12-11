package com.example.notes4all.service;

import com.example.notes4all.dao.NoteDAO;
import com.example.notes4all.model.Note;

import java.util.List;

public class NoteService {

    public static List<Note> getMyNotes(String uid) throws Exception {
        return NoteDAO.getMyNotes(uid);
    }

    public static void save(Note note) throws Exception {
        NoteDAO.save(note);
    }

    public static void update(String noteId, String title, String content) throws Exception {
        NoteDAO.update(noteId, title, content);
    }

    public static void delete(String noteId) throws Exception {
        NoteDAO.delete(noteId);
    }
}
