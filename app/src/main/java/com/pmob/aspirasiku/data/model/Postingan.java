package com.pmob.aspirasiku.data.model;

import java.util.List;

public class Postingan {
    private int id;
    private String judul;
    private String konten;
    private int id_kategori;
    private String tipe;
    private List<Komentar> komentar;
    private int upvotes;
    private int downvotes;

    // Konstruktor untuk data dummy
    public Postingan(int id, String judul, String konten, int id_kategori, String tipe, List<Komentar> komentar, int upvotes, int downvotes) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.tipe = tipe;
        this.komentar = komentar;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
    }

    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getKonten() { return konten; }
    public int getId_kategori() { return id_kategori; }
    public String getTipe() { return tipe; }
    public List<Komentar> getKomentar() { return komentar; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }
}