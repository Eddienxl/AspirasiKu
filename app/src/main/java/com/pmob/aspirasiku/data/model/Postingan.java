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

    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getKonten() { return konten; }
    public int getId_kategori() { return id_kategori; }
    public String getTipe() { return tipe; }
    public List<Komentar> getKomentar() { return komentar; }
    public int getUpvotes() { return upvotes; }
    public int getDownvotes() { return downvotes; }
}