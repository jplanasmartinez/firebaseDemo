package com.example.firebaseapplication.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class Post {
    public String postId;
    public String userId;
    public String postImage;
    public String postText;

    public Post(String userId, String postImage, String postText) {
        this.postId = UUID.randomUUID().toString();
        this.userId = userId;
        this.postImage = postImage;
        this.postText = postText;
    }

    public Post() {
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }


}
