package com.example.unseenindia.Models;

public class Post {

    private String description,imageurl,postid,publisher,impressionCount;

    public Post() {
    }

    public Post(String description, String imageurl, String postid, String publisher, String impressionCount, String reports) {
        this.description = description;
        this.imageurl = imageurl;
        this.postid = postid;
        this.publisher = publisher;
        this.impressionCount = impressionCount;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImpressionCount() {
        return impressionCount;
    }

    public void setImpressionCount(String impressionCount) {
        this.impressionCount = impressionCount;
    }


}
