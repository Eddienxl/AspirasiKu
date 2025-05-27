package com.pmob.aspirasiku.data.model;

public class Pengguna {
    private int id;
    private String nim;
    private String nama;
    private String email;
    private String password; // Field password ditambahkan
    private String peran;

    // Konstruktor diperbarui untuk menyertakan password
    public Pengguna(int id, String nama, String nim, String email, String password, String peran) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.email = email;
        this.password = password; // Inisialisasi password
        this.peran = peran;
    }

    // Getters
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

    // getPassword() sengaja tidak disertakan untuk praktik keamanan dasar.
    // Password biasanya tidak diekspos setelah objek dibuat.
    // Jika Anda memerlukannya untuk kasus penggunaan yang sangat spesifik dan terkontrol,
    // Anda bisa menambahkannya sendiri.

    public String getPeran() {
        return peran;
    }
}