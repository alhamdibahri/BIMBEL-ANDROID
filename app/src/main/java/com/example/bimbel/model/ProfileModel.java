package com.example.bimbel.model;

public class ProfileModel {

    private int image;
    private String judul;
    private String hasil;


    public ProfileModel(int image,String judul, String hasil) {
        this.image = image;
        this.judul = judul;
        this.hasil = hasil;
    }

    public String getJudul() {
        return judul;
    }

    public int getImage() {
        return image;
    }

    public String getHasil() {
        return hasil;
    }
}
