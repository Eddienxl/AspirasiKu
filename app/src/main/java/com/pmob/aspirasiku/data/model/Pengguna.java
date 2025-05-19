package com.pmob.aspirasiku.data.model;

public class Pengguna {
    private int id;
    private String nim;
    private String nama;
    private String email;
    private String peran;

    // Konstruktor untuk data dummy
    public Pengguna(int id, String nama, String nim, String email, String peran) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.email = email;
        this.peran = peran;
    }

    public int getId() {
        return id;
    }

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public String getEmail() {
        return email;
    }

    public String getPeran() {
        return peran;
    }
}