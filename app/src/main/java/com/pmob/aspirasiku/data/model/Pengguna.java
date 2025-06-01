package com.pmob.aspirasiku.data.model;

import com.google.gson.annotations.SerializedName; // PENTING: Tambahkan import ini!

public class Pengguna {
    private int id;
    private String nim;
    private String nama;
    private String email;
    private String password;
    private String peran;

    @SerializedName("profile_picture") // <-- PENTING: Pastikan anotasi ini ada
    private String profilePicture;

    // Konstruktor yang lebih lengkap, jika Anda menggunakannya untuk manual object creation
    public Pengguna(int id, String nama, String nim, String email, String password, String peran, String profilePicture) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.email = email;
        this.password = password;
        this.peran = peran;
        this.profilePicture = profilePicture; // Inisialisasi profilePicture
    }

    // Jika Anda hanya menggunakan konstruktor 6 argumen, dan mengandalkan setter,
    // pastikan objek Pengguna yang datang dari API memang mengisi profilePicture
    // (melalui @SerializedName atau nama field yang cocok)
    public Pengguna(int id, String nama, String nim, String email, String password, String peran) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.email = email;
        this.password = password;
        this.peran = peran;
        this.profilePicture = null; // Default null jika tidak ada di konstruktor ini
    }


    // Getters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; } // Tambahkan setter
    public String getNim() { return nim; }
    public void setNim(String nim) { this.nim = nim; } // Tambahkan setter
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; } // Tambahkan setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; } // Tambahkan setter
    public String getPassword() { return password; } // Jika Anda ingin getter ini, tambahkan setter juga
    public void setPassword(String password) { this.password = password; } // Tambahkan setter
    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; } // Tambahkan setter

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}