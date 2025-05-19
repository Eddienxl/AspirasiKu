package com.pmob.aspirasiku.data.model;

public class RegisterRequest {
    private String nim;
    private String nama;
    private String email;
    private String kata_sandi;

    public RegisterRequest(String nim, String nama, String email, String kata_sandi) {
        this.nim = nim;
        this.nama = nama;
        this.email = email;
        this.kata_sandi = kata_sandi;
    }

    // Getter & Setter
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getKata_sandi() { return kata_sandi; }
    public void setKata_sandi(String kata_sandi) { this.kata_sandi = kata_sandi; }
}
