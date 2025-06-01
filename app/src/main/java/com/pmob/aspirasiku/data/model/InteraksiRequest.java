package com.pmob.aspirasiku.data.model;

// data/model/InteraksiRequest.java
public class InteraksiRequest {
    private int postId; // Pastikan nama ini sesuai dengan yang diharapkan API Anda (misal: post_id)
    private String type; // Nilai bisa "upvote" atau "downvote"

    public InteraksiRequest(int postId, String type) {
        this.postId = postId;
        this.type = type;
    }

    // Getters
    public int getPostId() {
        return postId;
    }

    public String getType() {
        return type;
    }

    // Setters (opsional, tapi seringkali dibutuhkan oleh library JSON seperti Gson)
    public void setPostId(int postId) {
        this.postId = postId;
    }

    public void setType(String type) {
        this.type = type;
    }
}