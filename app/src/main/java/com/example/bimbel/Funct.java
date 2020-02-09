package com.example.bimbel;

import java.util.Calendar;

public class Funct {
    public String TanggalIndo(String data){
        String[] strMonths = new String[]{
                "Januari",
                "Februari",
                "Maret",
                "April",
                "Mei",
                "Juni",
                "Juli",
                "Agustus",
                "September",
                "oktober",
                "November",
                "Desember"
        };

        String year = data.substring(0,4);
        String bulan = strMonths[Integer.parseInt(data.substring(5,7)) - 1];
        String tanggal = data.substring(8,10);

        return tanggal + " " + bulan + " " + year;
    }
}
