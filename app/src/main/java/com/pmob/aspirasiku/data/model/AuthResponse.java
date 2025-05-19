package com.pmob.aspirasiku.data.model;

public class AuthResponse {
    private String token;
    private String message;
    private Pengguna pengguna;

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public Pengguna getPengguna() {
        return pengguna;
    }
}
