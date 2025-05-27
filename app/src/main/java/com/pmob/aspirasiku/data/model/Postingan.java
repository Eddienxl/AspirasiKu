package com.pmob.aspirasiku.data.model;

import java.util.List; // Impor untuk List

public class Postingan {
    private int id;
    private String judul;
    private String konten;
    private int id_kategori;
    private boolean anonim;
    private String status;
    private String created_at;
    private int upvotes;
    private int downvotes; // TAMBAHKAN field downvotes
    private List<Komentar> komentar; // TAMBAHKAN field untuk daftar komentar

    // Constructor diubah untuk menerima downvotes dan komentar
    public Postingan(int id, String judul, String konten, int id_kategori, String status, String created_at, int upvotes, int downvotes, List<Komentar> komentar) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.anonim = status != null && status.equals("anonim"); // Sebaiknya field anonim dikirim langsung dari API jika memungkinkan
        this.status = status;
        this.created_at = created_at;
        this.upvotes = upvotes;
        this.downvotes = downvotes; // Inisialisasi field downvotes
        this.komentar = komentar;   // Inisialisasi field komentar
    }

    public Postingan(int id, String judul, String konten, int idKategori, String publik, Object createdAt, int upvotes) {
    }

    // Getters
    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getKonten() { return konten; }
    public int getId_kategori() { return id_kategori; }
    public boolean isAnonim() { return anonim; }
    public String getStatus() { return status; }
    public String getCreated_at() { return created_at; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; } // Sekarang mengembalikan nilai field
    public List<Komentar> getKomentar() { return komentar; } // Getter untuk komentar
}