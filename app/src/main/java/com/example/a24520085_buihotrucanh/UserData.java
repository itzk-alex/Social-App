package com.example.a24520085_buihotrucanh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserData {
    public static String userId = "";
    public static String registeredName = "";
    public static String registeredEmail = "";
    public static String registeredPassword = "";
    public static String Address = "";
    public static String Description = "";
    public static String Phone = "";
    public static String AvatarUrl = "";
    public static List<Post> globalPostList = new ArrayList<>();

    public static List<String[]> userList = new ArrayList<>();

    public static Map<String, Set<Post>> hiddenPostsMap = new HashMap<>();

    public static boolean register(String name, String email, String password) {
        for (String[] user : userList) {
            if (user[1].equals(email)) return false;
        }
        userList.add(new String[]{name, email, password, "", ""});
        hiddenPostsMap.put(email, new HashSet<>());
        return true;
    }

    public static String[] login(String email, String password) {
        for (String[] user : userList) {
            if (user[1].equals(email) && user[2].equals(password)) return user;
        }
        return null;
    }

    public static void hidePost(Post post) {
        Set<Post> hidden = hiddenPostsMap.get(registeredEmail);
        if (hidden == null) {
            hidden = new HashSet<>();
            hiddenPostsMap.put(registeredEmail, hidden);
        }
        hidden.add(post);
    }

    public static boolean isPostHidden(Post post) {
        Set<Post> hidden = hiddenPostsMap.get(registeredEmail);
        return hidden != null && hidden.contains(post);
    }

    public static void clearSession() {
        userId = "";
        registeredName = "";
        registeredEmail = "";
        registeredPassword = "";
        Address = "";
        Description = "";
        Phone = "";
        AvatarUrl = "";
        globalPostList.clear();
        hiddenPostsMap.clear();
    }
}