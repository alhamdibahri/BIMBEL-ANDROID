package com.example.bimbel.response.History;

public class HistorySiswa {
    String id_history,
            kode_guru,
            nama,
            nama_category_kelas,
            mata_pelajaran,
            tgl_transaksi,
            foto_guru,
            status_history,
            no_handphone_guru,
            alamat_guru;

    public HistorySiswa(String kode_guru, String nama, String nama_category_kelas, String mata_pelajaran, String tgl_transaksi, String foto_guru, String status_history) {
        this.kode_guru = kode_guru;
        this.nama = nama;
        this.nama_category_kelas = nama_category_kelas;
        this.mata_pelajaran = mata_pelajaran;
        this.tgl_transaksi = tgl_transaksi;
        this.foto_guru = foto_guru;
        this.status_history = status_history;
    }

    public String getId_history() {
        return id_history;
    }

    public void setId_history(String id_history) {
        this.id_history = id_history;
    }

    public String getAlamat_guru() {
        return alamat_guru;
    }

    public void setAlamat_guru(String alamat_guru) {
        this.alamat_guru = alamat_guru;
    }

    public String getNo_handphone_guru() {
        return no_handphone_guru;
    }

    public void setNo_handphone_guru(String no_handphone_guru) {
        this.no_handphone_guru = no_handphone_guru;
    }

    public String getKode_guru() {
        return kode_guru;
    }

    public void setKode_guru(String kode_guru) {
        this.kode_guru = kode_guru;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNama_category_kelas() {
        return nama_category_kelas;
    }

    public void setNama_category_kelas(String nama_category_kelas) {
        this.nama_category_kelas = nama_category_kelas;
    }

    public String getMata_pelajaran() {
        return mata_pelajaran;
    }

    public void setMata_pelajaran(String mata_pelajaran) {
        this.mata_pelajaran = mata_pelajaran;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public void setTgl_transaksi(String tgl_transaksi) {
        this.tgl_transaksi = tgl_transaksi;
    }

    public String getFoto_guru() {
        return foto_guru;
    }

    public void setFoto_guru(String foto_guru) {
        this.foto_guru = foto_guru;
    }

    public String getStatus_history() {
        return status_history;
    }

    public void setStatus_history(String status_history) {
        this.status_history = status_history;
    }
}
