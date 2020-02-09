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
import com.example.bimbel.response.Berita.Berita;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RuangBacaAdapter extends BaseAdapter implements Filterable {

    Funct funct;

    Context context;
    List<Berita> list, filterd;

    public RuangBacaAdapter(Context context, List<Berita> list) {
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
            convertView = inflater.inflate(R.layout.tmp_ruang_baca, null);
        }

        funct = new Funct();

        Berita berita = filterd.get(position);

        ImageView imageBaca = convertView.findViewById(R.id.imageBaca);
        TextView txtJudulBaca = convertView.findViewById(R.id.txtJudulBaca);
        TextView txtTanggalBaca = convertView.findViewById(R.id.txtTanggalBaca);
        TextView txtid = convertView.findViewById(R.id.txtid);

        txtJudulBaca.setText(berita.getNama_berita());
        txtTanggalBaca.setText(funct.TanggalIndo(berita.getTanggal_berita()));

        txtid.setText(berita.getId());
        if(berita.getFoto_berita() != null && berita.getFoto_berita().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoberita/" + berita.getFoto_berita()).placeholder(R.drawable.placeholder).into(imageBaca);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(imageBaca);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        BeritaFilter filter = new BeritaFilter();
        return filter;
    }

    private class BeritaFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Berita> filterData = new ArrayList<Berita>();
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (Berita bk: list){
                if(bk.getNama_berita().toString().toLowerCase().contains(filterString)){
                    filterData.add(bk);
                }
            }
            results.count = filterData.size();
            results.values = filterData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterd = (List<Berita>) results.values;
            notifyDataSetChanged();
        }
    }
}
