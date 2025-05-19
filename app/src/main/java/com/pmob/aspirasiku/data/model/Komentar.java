package com.pmob.aspirasiku.data.model;

public class Komentar {
    private int id_postingan;
    private String konten;

    public Komentar(int id_postingan, String konten) {
        this.id_postingan = id_postingan;
        this.konten = konten;
    }

    public int getId_postingan() { return id_postingan; }
    public String getKonten() { return konten; }
}
