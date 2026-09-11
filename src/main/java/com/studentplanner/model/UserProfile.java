package com.studentplanner.model;

public class UserProfile {

    private int id;
    private String displayName;
    private String avatar;

    public UserProfile(String displayName, String avatar) {
        this.displayName = displayName;
        this.avatar = avatar;
    }

    public UserProfile(int id, String displayName, String avatar) {
        this.id = id;
        this.displayName = displayName;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return displayName;
    }
}