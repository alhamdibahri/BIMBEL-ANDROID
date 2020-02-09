package com.example.bimbel.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.MateriSiswa;
import com.example.bimbel.R;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.MateriGuru.MateriGuru;
import com.example.bimbel.response.TokenManager;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MateriGuruAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "MateriGuruAdapter";

    Context context;
    List<MateriGuru> list, filterd;
    ApiService service;
    TokenManager tokenManager;
    Call<ResponseBody> call;

    public MateriGuruAdapter(Context context, List<MateriGuru> list) {
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
            convertView = inflater.inflate(R.layout.tmp_materi_siswa, null);
        }

        MateriGuru materiGuru = filterd.get(position);
        TextView  nama_materi = convertView.findViewById(R.id.nama_materi);
        TextView kategori_pelajaran = convertView.findViewById(R.id.kategori_pelajaran);
        TextView nama_guru = convertView.findViewById(R.id.nama_guru);
        Button download = convertView.findViewById(R.id.download);

        nama_guru.setText("Guru : " + materiGuru.getNama());
        nama_materi.setText(materiGuru.getNama_materi());
        kategori_pelajaran.setText("Kategori : " + materiGuru.getMata_pelajaran());


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    tokenManager = TokenManager.getInstance(context.getSharedPreferences("pref", MODE_PRIVATE));
                    service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
                    call = service.download("http://192.168.43.206/bimbel/public/filemateri/" + materiGuru.getFile_materi());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try{
                                if (response.isSuccessful()) {
                                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), materiGuru.getNama_materi() + ".pdf");
                                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("File " + materiGuru.getNama_materi()  + ".pdf berhasil di download silahkan cek di folder download")
                                            .show();
                                } else {
                                    Toast.makeText(context, "server contact failed", Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }catch (Exception ex){
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        MateriGuruFilter filter = new MateriGuruFilter();
        return filter;
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String file) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), file);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private class MateriGuruFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MateriGuru> filterData = new ArrayList<MateriGuru>();
            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            for (MateriGuru bk: list){
                if(bk.getNama_materi().toString().toLowerCase().contains(filterString)){
                    filterData.add(bk);
                }
            }
            results.count = filterData.size();
            results.values = filterData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterd = (List<MateriGuru>) results.values;
            notifyDataSetChanged();
        }
    }
}
