package com.pmob.aspirasiku.data.model;

import com.google.gson.annotations.SerializedName;

public class Kategori {
    private int id;
    private String nama; // Ini field untuk nama kategori

    public Kategori(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}