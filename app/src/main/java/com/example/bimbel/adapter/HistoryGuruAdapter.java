package com.example.bimbel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bimbel.Funct;
import com.example.bimbel.R;
import com.example.bimbel.response.History.HistoryGuru;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HistoryGuruAdapter extends BaseAdapter implements Filterable {

    Funct funct;
    Context context;
    List<HistoryGuru> list, filterd;

    public HistoryGuruAdapter(Context context, List<HistoryGuru> list) {
        this.context = context;
        this.list = list;
        this.filterd = this.list;
    }

    @Override
    public int getCount() {
        return filterd.size();
    }

    @Override
    public Object getItem(int position) {
        return filterd.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater =LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.tmp_history_guru, null);
        }

        funct = new Funct();

        HistoryGuru historyGuru = filterd.get(position);

        String status = "";
        if(historyGuru.getStatus_history().equals("0")){
            status = "Segera Setujui";
        }

        if(historyGuru.getStatus_history().equals("2")){
            status = "Berlangsung";
        }

        if(historyGuru.getStatus_history().equals("3")){
            status = "Selesai";
        }

        ImageView imageHistorySiswa = convertView.findViewById(R.id.imageHistorySiswa);
        TextView txtHistoryNamaSiswa = convertView.findViewById(R.id.txtHistoryNamaSiswa);
        TextView txtHistoryAlamatSiswa = convertView.findViewById(R.id.txtHistoryAlamatSiswa);
        TextView txtTanggalHistoryGuru = convertView.findViewById(R.id.txtTanggalHistoryGuru);
        TextView txtstatushistoryguru = convertView.findViewById(R.id.txtstatushistoryguru);
        TextView txtHistoryKodeSiswa = convertView.findViewById(R.id.txtHistoryKodeSiswa);
        TextView txtidHistorySiswa = convertView.findViewById(R.id.txtidHistorySiswa);

        txtHistoryKodeSiswa.setText(historyGuru.getKode_siswa());
        txtidHistorySiswa.setText(historyGuru.getId_history());

        txtHistoryNamaSiswa.setText(historyGuru.getNama());
        txtTanggalHistoryGuru.setText(funct.TanggalIndo(historyGuru.getTgl_transaksi()));

        txtHistoryAlamatSiswa.setText(historyGuru.getAlamat_siswa());
        txtstatushistoryguru.setText(status);
        if(historyGuru.getFoto_siswa() != null && historyGuru.getFoto_siswa().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" + historyGuru.getFoto_siswa()).placeholder(R.drawable.placeholder).into(imageHistorySiswa);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(imageHistorySiswa);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        HistoryGuruFilter filter = new HistoryGuruFilter();
        return filter;
    }

    private class HistoryGuruFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<HistoryGuru> filterData = new ArrayList<HistoryGuru>();
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (HistoryGuru bk: list){
                if(bk.getNama().toLowerCase().contains(filterString)){
                    filterData.add(bk);
                }
            }
            results.count = filterData.size();
            results.values = filterData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterd = (List<HistoryGuru>) results.values;
            notifyDataSetChanged();
        }
    }
}
