package com.example.firebaseapplication.models;

public class User {
    public String userid;
    public String username;
    public String email;
    public String password;
    public String profileImageURL;

    public User(String userid, String username, String email, String password, String profileImageURL) {
        this.userid = userid;
        this.username = username;
        this.profileImageURL = profileImageURL;
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
