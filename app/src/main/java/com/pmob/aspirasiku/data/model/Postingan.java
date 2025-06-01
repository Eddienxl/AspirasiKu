package com.pmob.aspirasiku.data.model;

import java.util.List;
import com.google.gson.annotations.SerializedName; // Pastikan import ini ada

public class Postingan {
    private int id;
    private String judul;
    private String konten;
    private int id_kategori;

    @SerializedName("kategori")
    private Kategori kategori;

    private boolean anonim;

    @SerializedName("dibuat_pada") // <-- TAMBAHKAN ANOTASI INI!
    private String created_at; // <-- Nama variabel Java Anda

    private String status; // Jika "status" di API adalah "publik" atau "anonim"
    // dan terpisah dari "anonim" boolean, pastikan pemetaan di sini.
    // Jika "anonim" boolean diisi dari "status", mungkin "status" tidak perlu.

    private int upvotes;
    private int downvotes;
    private List<Komentar> komentar;

    @SerializedName("penulis")
    private Pengguna penulis;

    // --- Konstruktor yang Direkomendasikan (lebih lengkap dan konsisten) ---
    public Postingan(int id, String judul, String konten, int id_kategori, Kategori kategori, boolean anonim, String status,
                     String created_at, int upvotes, int downvotes, List<Komentar> komentar, Pengguna penulis) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.kategori = kategori;
        this.anonim = anonim;
        this.status = status;
        this.created_at = created_at; // Nilai dari JSON "dibuat_pada" akan masuk ke sini
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.komentar = komentar;
        this.penulis = penulis;
        if (status != null && status.equalsIgnoreCase("anonim")) {
            this.anonim = true;
        }
    }

    // --- Pertahankan konstruktor lain jika masih digunakan ---
    public Postingan(int id, String judul, String konten, int id_kategori, String status, String created_at, int upvotes, int downvotes, List<Komentar> komentar) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.anonim = status != null && status.equals("anonim");
        this.status = status;
        this.created_at = created_at; // Nilai dari JSON "dibuat_pada" akan masuk ke sini
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.komentar = komentar;
    }

    public Postingan(int id, String judul, String konten, int idKategori, String publik, Object createdAt, int upvotes) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = idKategori;
        this.status = publik;
        this.created_at = createdAt != null ? String.valueOf(createdAt) : null;
        this.upvotes = upvotes;
    }

    // --- Getter dan Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getKonten() { return konten; }
    public void setKonten(String konten) { this.konten = konten; }

    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }

    public int getId_kategori() { return id_kategori; }
    public void setId_kategori(int id_kategori) { this.id_kategori = id_kategori; }

    public boolean isAnonim() { return anonim; }
    public void setAnonim(boolean anonim) { this.anonim = anonim; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreated_at() { return created_at; } // Getter ini akan mengembalikan nilai dari "dibuat_pada"
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public int getUpvotes() { return upvotes; }
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
    public int getDownvotes() { return downvotes; }
    public void setDownvotes(int downvotes) { this.downvotes = downvotes; }
    public List<Komentar> getKomentar() { return komentar; }
    public void setKomentar(List<Komentar> komentar) { this.komentar = komentar; }

    public Pengguna getPenulis() { return penulis; }
    public void setPenulis(Pengguna penulis) { this.penulis = penulis; }
}