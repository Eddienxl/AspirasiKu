package com.pmob.aspirasiku.data.model;

import com.google.gson.annotations.SerializedName; // Diperlukan jika nama field API berbeda

// data/model/InteraksiResponse.java
// Contoh jika API mengembalikan status interaksi user dan jumlah upvote/downvote terbaru
public class InteraksiResponse {
    private boolean hasUserUpvoted;    // Jika API memberitahu apakah user sudah upvote
    private boolean hasUserDownvoted; // Jika API memberitahu apakah user sudah downvote
    private int upvotes;             // Jumlah upvote terbaru
    private int downvotes;           // Jumlah downvote terbaru
    private String message;          // Pesan dari server (misal: "Vote berhasil")
    private boolean success;         // Indikator sukses/gagal dari server

    // Pastikan nama field ini sesuai dengan JSON dari API Anda.
    // Jika nama di JSON berbeda, gunakan @SerializedName("nama_di_json")

    // Constructor (buat sesuai kebutuhan Anda)
    public InteraksiResponse(boolean hasUserUpvoted, boolean hasUserDownvoted, int upvotes, int downvotes, String message, boolean success) {
        this.hasUserUpvoted = hasUserUpvoted;
        this.hasUserDownvoted = hasUserDownvoted;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.message = message;
        this.success = success;
    }

    // Getters
    public boolean hasUserUpvoted() {
        return hasUserUpvoted;
    }

    public boolean hasUserDownvoted() {
        return hasUserDownvoted;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    // Setters (opsional, jika diperlukan untuk deserialisasi oleh library seperti Gson)
    public void setHasUserUpvoted(boolean hasUserUpvoted) {
        this.hasUserUpvoted = hasUserUpvoted;
    }

    public void setHasUserDownvoted(boolean hasUserDownvoted) {
        this.hasUserDownvoted = hasUserDownvoted;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}