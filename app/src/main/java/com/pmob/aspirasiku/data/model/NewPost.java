package com.pmob.aspirasiku.data.model;

public class NewPost {
    private String judul;
    private String konten;
    private int id_kategori;
    private String tipe; // "aspirasi" atau "pertanyaan"

    public NewPost(String judul, String konten, int id_kategori, String tipe) {
        this.judul = judul;
        this.konten = konten;
        this.id_kategori = id_kategori;
        this.tipe = tipe;
    }

    // Getter dan Setter
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getKonten() { return konten; }
    public void setKonten(String konten) { this.konten = konten; }

    public int getId_kategori() { return id_kategori; }
    public void setId_kategori(int id_kategori) { this.id_kategori = id_kategori; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }
}