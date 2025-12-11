package com.example.notes4all.service;

public class Session {
    private static String idToken;
    private static String uid;
    private static String email;
    private static String viewedUserUid;

    public static void setViewedUserUid(String uid) { viewedUserUid = uid; }
    public static String getViewedUserUid() { return viewedUserUid; }


    public static void start(String token, String userId, String userEmail) {
        idToken = token;
        uid = userId;
        email = userEmail;
        System.out.println("Session started for UID: " + uid);
    }

    public static String getIdToken() {
        return idToken;
    }

    public static String getUid() {
        return uid;
    }

    public static String getEmail() {
        return email;
    }

    public static boolean isLoggedIn() {
        return idToken != null && uid != null;
    }

    public static void clear() {
        idToken = null;
        uid = null;
        email = null;
        System.out.println("Session cleared.");
    }
}
