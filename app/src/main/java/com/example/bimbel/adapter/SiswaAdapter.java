package com.example.bimbel.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.bimbel.R;
import com.example.bimbel.response.Siswa.Siswa;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SiswaAdapter extends BaseAdapter implements Filterable {

    private List<Siswa> siswa, filterd;
    private Context context;

    public SiswaAdapter(Context context, List<Siswa> siswa){
        this.siswa = siswa;
        this.context = context;
        this.filterd = this.siswa;
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

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.tmp_data_siswa, parent, false);
        }

        TextView txtNama = convertView.findViewById(R.id.textNama);
        ImageView imageSiswa = convertView.findViewById(R.id.imageSiswa);
        TextView txtAlamat = convertView.findViewById(R.id.txtAlamat);
        TextView txtAgama = convertView.findViewById(R.id.txtAgama);
        Button btnDetailSiswa = convertView.findViewById(R.id.btnDetailSiswa);
        final Siswa thisSiswa = filterd.get(position);
        btnDetailSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Detail Siswa");

                //menempelkan layout dari layout form segitiga
                View viewInflated = LayoutInflater.from(context).inflate(R.layout.detail_siswa, parent, false);
                final TextView tmp_nama = (TextView) viewInflated.findViewById(R.id.tmp_nama);
                final TextView tmp_tanggal_lahir = (TextView) viewInflated.findViewById(R.id.tmp_tgl_lahir);
                final TextView tmp_jenkel = (TextView) viewInflated.findViewById(R.id.tmp_jenkel);
                final TextView tmp_alamat = (TextView) viewInflated.findViewById(R.id.tmp_alamat);
                final ImageView tmp_image = (ImageView) viewInflated.findViewById(R.id.tmp_image);

                tmp_nama.setText(thisSiswa.getNama());
                tmp_tanggal_lahir.setText(thisSiswa.getTanggal_lahir_siswa());
                tmp_jenkel.setText(thisSiswa.getJenkel_siswa());
                tmp_alamat.setText(thisSiswa.getAlamat_siswa());

                if(thisSiswa.getFoto_siswa() != null && thisSiswa.getFoto_siswa().length() > 0){
                    Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" + thisSiswa.getFoto_siswa()).placeholder(R.drawable.placeholder).into(tmp_image);
                } else{
                    Picasso.get().load(R.drawable.placeholder).into(imageSiswa);
                }

                //set layout segitiga di alert dialog
                builder.setView(viewInflated);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        });
        txtNama.setText(thisSiswa.getNama());
        txtAlamat.setText(thisSiswa.getAlamat_siswa());
        txtAgama.setText(thisSiswa.getAgama_siswa());

        if(thisSiswa.getFoto_siswa() != null && thisSiswa.getFoto_siswa().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" + thisSiswa.getFoto_siswa()).placeholder(R.drawable.placeholder).into(imageSiswa);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(imageSiswa);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        SiswaFilter filter = new SiswaFilter();
        return filter;
    }

    private class SiswaFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Siswa> filterData = new ArrayList<Siswa>();
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (Siswa bk: siswa){
                if(bk.getNama().toString().toLowerCase().contains(filterString)){
                    filterData.add(bk);
                }
            }
            results.count = filterData.size();
            results.values = filterData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterd = (List<Siswa>) results.values;
            notifyDataSetChanged();
        }
    }
}
