package com.example.mobilprogchatapp.Utills;

public class Friends {
    private String profileImageUrl,status,username;

    public Friends(String profileImageUrl, String status, String username) {

        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.username = username;
    }

    public Friends() {
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
