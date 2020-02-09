package com.example.bimbel.response.History;

public class HistoryGuru {
    String alamat_siswa;
    String foto_siswa;
    String kode_siswa;
    String nama;
    String jenkel_siswa;
    String tanggal_lahir_siswa;
    String status_history;
    String tgl_transaksi;
    String no_handphone_siswa;
    String id_history;

    public HistoryGuru(String alamat_siswa, String foto_siswa, String kode_siswa, String nama, String status_history, String tgl_transaksi) {
        this.alamat_siswa = alamat_siswa;
        this.foto_siswa = foto_siswa;
        this.kode_siswa = kode_siswa;
        this.nama = nama;
        this.status_history = status_history;
        this.tgl_transaksi = tgl_transaksi;
    }


    public String getId_history() {
        return id_history;
    }

    public void setId_history(String id_history) {
        this.id_history = id_history;
    }

    public String getJenkel_siswa() {
        return jenkel_siswa;
    }

    public void setJenkel_siswa(String jenkel_siswa) {
        this.jenkel_siswa = jenkel_siswa;
    }

    public String getTanggal_lahir_siswa() {
        return tanggal_lahir_siswa;
    }

    public void setTanggal_lahir_siswa(String tanggal_lahir_siswa) {
        this.tanggal_lahir_siswa = tanggal_lahir_siswa;
    }

    public String getNo_handphone_siswa() {
        return no_handphone_siswa;
    }

    public void setNo_handphone_siswa(String no_handphone_siswa) {
        this.no_handphone_siswa = no_handphone_siswa;
    }

    public String getAlamat_siswa() {
        return alamat_siswa;
    }

    public void setAlamat_siswa(String alamat_siswa) {
        this.alamat_siswa = alamat_siswa;
    }

    public String getFoto_siswa() {
        return foto_siswa;
    }

    public void setFoto_siswa(String foto_siswa) {
        this.foto_siswa = foto_siswa;
    }

    public String getKode_siswa() {
        return kode_siswa;
    }

    public void setKode_siswa(String kode_siswa) {
        this.kode_siswa = kode_siswa;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getStatus_history() {
        return status_history;
    }

    public void setStatus_history(String status_history) {
        this.status_history = status_history;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public void setTgl_transaksi(String tgl_transaksi) {
        this.tgl_transaksi = tgl_transaksi;
    }
}
