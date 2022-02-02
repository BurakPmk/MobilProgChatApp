package com.example.mobilprogchatapp.Utills;

public class Users {
    private String user,status,profileImage,connection;

    public Users() {
    }

    public Users(String user, String status, String profileImage, String connection) {
        this.user = user;
        this.status = status;
        this.profileImage = profileImage;
        this.connection = connection;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }
}
