package com.pmob.aspirasiku.data.model;

public class UpdateProfileRequest {
    private String nama;
    private String email;

    public UpdateProfileRequest(String nama, String email) {
        this.nama = nama;
        this.email = email;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}