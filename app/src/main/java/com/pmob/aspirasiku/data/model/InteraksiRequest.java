package com.pmob.aspirasiku.data.model;

public class InteraksiRequest {
    private int id_postingan;
    private String tipe;

    public InteraksiRequest(int id_postingan, String tipe) {
        this.id_postingan = id_postingan;
        this.tipe = tipe;
    }

    public int getId_postingan() { return id_postingan; }
    public String getTipe() { return tipe; }
}