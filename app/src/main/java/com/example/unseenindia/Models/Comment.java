package com.example.unseenindia.Models;

public class Comment {

    public String comment,publisherId,postId,commentId;


    public Comment() {
    }

    public Comment(String comment, String publisherId, String postId, String commentId) {
        this.comment = comment;
        this.publisherId = publisherId;
        this.postId = postId;
        this.commentId = commentId;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
