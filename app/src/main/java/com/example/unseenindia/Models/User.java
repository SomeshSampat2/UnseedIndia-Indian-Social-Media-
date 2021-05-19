package com.example.unseenindia.Models;

public class User {

    String email,password,profileImage,quote,userid,username;

    public User() {
    }

    public User(String email, String password, String profileImage, String quote, String userid, String username) {
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.quote = quote;
        this.userid = userid;
        this.username = username;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
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
}
