package com.pmob.aspirasiku.data.model;

public class Komentar {
    private int id;
    private int id_pengguna;
    private int id_postingan;
    private String konten;
    private boolean anonim;
    private String created_at;

    public Komentar(int id, int id_pengguna, int id_postingan, String konten, boolean anonim, String created_at) {
        this.id = id;
        this.id_pengguna = id_pengguna;
        this.id_postingan = id_postingan;
        this.konten = konten;
        this.anonim = anonim;
        this.created_at = created_at;
    }

    public int getId() { return id; }
    public int getId_pengguna() { return id_pengguna; }
    public int getId_postingan() { return id_postingan; }
    public String getKonten() { return konten; }
    public boolean isAnonim() { return anonim; }
    public String getCreated_at() { return created_at; }
}