package com.example.bimbel.response.Berita;

public class Berita {
    private String id, nama_berita, tanggal_berita, foto_berita, deskripsi;

    public Berita(String id, String nama_berita, String tanggal_berita, String foto_berita, String deskripsi) {
        this.id = id;
        this.nama_berita = nama_berita;
        this.tanggal_berita = tanggal_berita;
        this.foto_berita = foto_berita;
        this.deskripsi = deskripsi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama_berita() {
        return nama_berita;
    }

    public void setNama_berita(String nama_berita) {
        this.nama_berita = nama_berita;
    }

    public String getTanggal_berita() {
        return tanggal_berita;
    }

    public void setTanggal_berita(String tanggal_berita) {
        this.tanggal_berita = tanggal_berita;
    }

    public String getFoto_berita() {
        return foto_berita;
    }

    public void setFoto_berita(String foto_berita) {
        this.foto_berita = foto_berita;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
