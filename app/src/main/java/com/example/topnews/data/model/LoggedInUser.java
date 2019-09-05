package com.example.topnews.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {
    public final static LoggedInUser defaultUser = new LoggedInUser("default");

    private String userId;
    private String displayName;

    public LoggedInUser(String userId){
        this(userId,userId);
    }

    public LoggedInUser(String userId, String displayName) {
        this.userId = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}