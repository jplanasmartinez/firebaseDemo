package com.example.firebaseapplication.models;

import java.util.UUID;

public class Comment {
    public String commentId;
    public String postId;
    public String commentText;
    public String userId;

    public Comment(String postId, String commentText, String userId) {
        this.commentId = UUID.randomUUID().toString();
        this.postId = postId;
        this.commentText = commentText;
        this.userId = userId;
    }
    public Comment(){
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
