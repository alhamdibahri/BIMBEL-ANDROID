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
import com.example.bimbel.response.History.HistorySiswa;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HistorySiswaAdapter extends BaseAdapter implements Filterable {
    Funct funct;
    Context context;
    List<HistorySiswa> list, filterd;

    public HistorySiswaAdapter(Context context, List<HistorySiswa> list) {
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
            convertView = inflater.inflate(R.layout.tmp_history_siswa, null);
        }

        funct = new Funct();

        HistorySiswa historySiswa = filterd.get(position);
        ImageView imageHistoryGuru = convertView.findViewById(R.id.imageHistoryGuru);
        TextView txtHistoryNamaGuru = convertView.findViewById(R.id.txtHistoryNamaGuru);
        TextView txtHistoryKelasGuru = convertView.findViewById(R.id.txtHistoryKelasGuru);
        TextView txtHistoryMataPelajaran = convertView.findViewById(R.id.txtHistoryMataPelajaran);
        TextView txtTanggalHistorySiswa = convertView.findViewById(R.id.txtTanggalHistorySiswa);
        TextView txtstatushistorysiswa = convertView.findViewById(R.id.txtstatushistorysiswa);
        TextView txtHistoryKodeGuru = convertView.findViewById(R.id.txtHistoryKodeGuru);
        TextView txtidHistoryGuru = convertView.findViewById(R.id.txtidHistoryGuru);

        String status = "";
        if(historySiswa.getStatus_history().equals("0")){
            status = "Menungggu Persetujuan";
        }

        if(historySiswa.getStatus_history().equals("1")){
            status = "Ditolak";
        }

        if(historySiswa.getStatus_history().equals("2")){
            status = "Berlangsung";
        }

        if(historySiswa.getStatus_history().equals("3")){
            status = "Selesai";
        }

        txtidHistoryGuru.setText(historySiswa.getId_history());
        txtHistoryNamaGuru.setText(historySiswa.getNama());
        txtHistoryKelasGuru.setText(historySiswa.getNama_category_kelas());
        txtHistoryMataPelajaran.setText(historySiswa.getMata_pelajaran());
        txtTanggalHistorySiswa.setText(funct.TanggalIndo(historySiswa.getTgl_transaksi()));
        txtstatushistorysiswa.setText(status);
        txtHistoryKodeGuru.setText(historySiswa.getKode_guru());

        if(historySiswa.getFoto_guru() != null && historySiswa.getFoto_guru().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + historySiswa.getFoto_guru()).placeholder(R.drawable.placeholder).into(imageHistoryGuru);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(imageHistoryGuru);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        HistorySiswaFilter filter = new HistorySiswaFilter();
        return filter;
    }

    private class HistorySiswaFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<HistorySiswa> filterData = new ArrayList<HistorySiswa>();
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (HistorySiswa bk: list){
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
            filterd = (List<HistorySiswa>) results.values;
            notifyDataSetChanged();
        }
    }
}
