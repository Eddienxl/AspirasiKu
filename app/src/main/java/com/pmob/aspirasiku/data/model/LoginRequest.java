package com.pmob.aspirasiku.data.model;

public class LoginRequest {
    private String email;
    private String kata_sandi;

    public LoginRequest(String email, String kata_sandi) {
        this.email = email;
        this.kata_sandi = kata_sandi;
    }

    // Getter & Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKata_sandi() {
        return kata_sandi;
    }

    public void setKata_sandi(String kata_sandi) {
        this.kata_sandi = kata_sandi;
    }
}
