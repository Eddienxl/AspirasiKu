package com.pmob.aspirasiku.data.model;

public class UpdatePasswordRequest {
    private String kata_sandi_lama;
    private String kata_sandi_baru;

    public UpdatePasswordRequest(String kata_sandi_lama, String kata_sandi_baru) {
        this.kata_sandi_lama = kata_sandi_lama;
        this.kata_sandi_baru = kata_sandi_baru;
    }

    public String getKata_sandi_lama() { return kata_sandi_lama; }
    public void setKata_sandi_lama(String kata_sandi_lama) { this.kata_sandi_lama = kata_sandi_lama; }

    public String getKata_sandi_baru() { return kata_sandi_baru; }
    public void setKata_sandi_baru(String kata_sandi_baru) { this.kata_sandi_baru = kata_sandi_baru; }
}