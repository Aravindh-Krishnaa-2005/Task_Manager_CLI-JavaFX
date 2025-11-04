package com.taskmanager;

public class Session {
    private static Session instance;
    private int userId;
    private String username;

    private Session() {
        // private constructor to prevent external instantiation
    }

    // Singleton accessor
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // To clear session (logout)
    public void clear() {
        userId = -1;
        username = null;
    }


}
