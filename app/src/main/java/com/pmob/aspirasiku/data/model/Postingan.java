package com.pmob.aspirasiku.data.model;

public class Postingan {
    private int id;
    private String judul;
    private String konten;
    private int id_kategori;
    private boolean anonim;
    private String status;
    private String created_at;
    private int upvotes;

    public Postingan(int id, String judul, String konten, int id_kategori, String status, String created_at, int upvotes) {
        this.id = id;
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.anonim = status != null && status.equals("anonim");
        this.status = status;
        this.created_at = created_at;
        this.upvotes = upvotes;
    }

    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getKonten() { return konten; }
    public int getId_kategori() { return id_kategori; }
    public boolean isAnonim() { return anonim; }
    public String getStatus() { return status; }
    public String getCreated_at() { return created_at; }
    public int getUpvotes() { return upvotes; }
}