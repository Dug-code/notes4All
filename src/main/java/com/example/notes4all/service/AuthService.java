package com.example.notes4all.service;
import com.example.notes4all.dao.AuthDAO;
import org.json.JSONObject;

public class AuthService {

    public static boolean login(String email, String password) {
        try {
            JSONObject response = AuthDAO.login(email, password);

            if (response.has("error")) {
                System.out.println("Login error: " + response.get("error"));
                return false;
            }

            String idToken = response.getString("idToken");
            String uid = response.getString("localId");

            Session.start(idToken, uid, email);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean register(String email, String password) {
        try {
            JSONObject response = AuthDAO.register(email, password);

            if (response.has("error")) {
                System.out.println("Registration error: " + response.get("error"));
                return false;
            }

            Session.start(
                    response.getString("idToken"),
                    response.getString("localId"),
                    email
            );

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
