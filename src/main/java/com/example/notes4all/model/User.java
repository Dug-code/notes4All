package com.example.notes4all.model;

import com.google.cloud.Timestamp;

public class User {
    private String uid;
    private String username;
    private String email;
    private Timestamp createdAt;
    private String status; // online, offline

    public User() {}

    public User(String uid, String username, String email, Timestamp createdAt, String status) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getUid() {return uid;}

    public void setUid(String uid) {this.uid = uid;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public Timestamp getCreatedAt() {return createdAt;}

    public void setCreatedAt(Timestamp createdAt) {this.createdAt = createdAt;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    @Override
    public String toString() {
        return username + " (" + email + ")";
    }
}
