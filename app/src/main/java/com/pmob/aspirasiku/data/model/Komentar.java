package com.pmob.aspirasiku.data.model;

import com.google.gson.annotations.SerializedName;

public class Komentar {
    private int id;
    private int id_pengguna;
    private int id_postingan;
    private String konten;
    private boolean anonim;
    private String created_at;

    @SerializedName("penulis")
    private Pengguna penulisKomentar;


    // 1. Konstruktor LENGKAP (untuk menerima data dari API - GET)
    public Komentar(int id, int id_pengguna, int id_postingan, String konten, boolean anonim, String created_at, Pengguna penulisKomentar) {
        this.id = id;
        this.id_pengguna = id_pengguna;
        this.id_postingan = id_postingan;
        this.konten = konten;
        this.anonim = anonim;
        this.created_at = created_at;
        this.penulisKomentar = penulisKomentar;
    }

    // 2. Konstruktor Parsial (untuk membuat komentar baru - POST)
    public Komentar(int id_postingan, String konten) {
        this.id = 0;
        this.id_pengguna = 0;
        this.id_postingan = id_postingan;
        this.konten = konten;
        this.anonim = false;
        this.created_at = null;
        this.penulisKomentar = null;
    }

    // Getters
    public int getId() { return id; }
    public int getId_pengguna() { return id_pengguna; } // <-- KEMBALI KE NAMA GETTER YANG BENAR
    public int getId_postingan() { return id_postingan; }
    public String getKonten() { return konten; }
    public boolean isAnonim() { return anonim; }
    public String getCreated_at() { return created_at; }

    // Getter untuk penulis komentar
    public Pengguna getPenulis() { return penulisKomentar; }
}