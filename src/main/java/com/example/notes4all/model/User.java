package com.example.notes4all.model;

import java.util.List;

public class User {

    private String uid;
    private String username;
    private String fullName;
    private String email;
    private String bio;
    private String status;
    private String createdAt;
    private List<String> friends;

    public User(String uid, String username, String fullName, String email, String bio,
                String status, String createdAt, List<String> friends) {

        this.uid = uid;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.bio = bio;
        this.status = status;
        this.createdAt = createdAt;
        this.friends = friends;
    }

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public List<String> getFriends() { return friends; }

    public void setUsername(String username) { this.username = username; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setBio(String bio) { this.bio = bio; }
    public void setStatus(String status) { this.status = status; }
    public void setFriends(List<String> friendUids) { this.friends = friends; }
}
