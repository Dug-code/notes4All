package com.example.notes4all.service;

import com.example.notes4all.dao.UserDAO;
import com.example.notes4all.model.User;

public class UserService {

    public static boolean createUser(User user) {
        return UserDAO.createUserDocument(user);
    }

    public static User getUserProfile(String uid) {
        return UserDAO.getUserProfile(uid);
    }

    public static boolean updateUser(User user) {
        return UserDAO.updateUserProfile(user);
    }

    public static User getCurrentUser() {
        return getUserProfile(Session.getUid());
    }

    public static void updateStatus(String status) {
        UserDAO.updateUserStatus(status);
    }

    public static User getUserByUsername(String username) {
        return UserDAO.getUserByUsername(username);
    }

    public static boolean addFriend(String myUid, String friendUid) {
        return UserDAO.addFriend(myUid, friendUid);
    }

}
