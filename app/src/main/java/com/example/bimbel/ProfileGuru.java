package com.example.bimbel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.model.Guru;
import com.example.bimbel.model.Profile;
import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Kelas.DataKelas;
import com.example.bimbel.response.MataPelajaran.DataMataPelajaran;
import com.example.bimbel.response.TokenManager;
import com.example.bimbel.response.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileGuru extends AppCompatActivity {

    private static final String TAG = "ProfileGuru";

    ApiService service;
    Call<DataKelas> callKelas;
    Call<DataMataPelajaran> callMataPelajaran;
    Call<com.example.bimbel.response.Guru.ProfileGuru> call;
    Call<ResponseBody> callsimpan;
    TokenManager tokenManager;

    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap;
    Uri Path;

    @BindView(R.id.namaguru)
    EditText namaguru;
    @BindView(R.id.alamatguru)
    EditText alamatguru;
    @BindView(R.id.hpguru)
    EditText hpguru;
    @BindView(R.id.kelasguru)
    Spinner kelasguru;
    @BindView(R.id.pelajaranguru)
    Spinner pelajaranguru;
    @BindView(R.id.imagePGuru)
    ImageView imagePGuru;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.agama)
    Button btnagama;
    @BindView(R.id.jk)
    Button btnjk;
    @BindView(R.id.tglguru)
    Button btntglguru;


    List<String> listSpinner = null;
    List<String> listSpinnerPelajaran = null;
    ArrayAdapter<String> adapterpelajaran = null;
    ArrayAdapter<String> adapterKelas = null;
    List<String> idkelas;
    List<String> idmtapelajaran;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_guru);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        hasiltgl();

        imagePGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        namaguru.setText(getIntent().getStringExtra("data"));
        tv_name.setText(getIntent().getStringExtra("data"));

        tampildata();
        tampilKelas();
    }

    @OnClick(R.id.simpanpguru)
    void simpanpguru(){
        try {
            String kelas = idkelas.get(kelasguru.getSelectedItemPosition());
            String pelajaran = idmtapelajaran.get(pelajaranguru.getSelectedItemPosition());

            File file;
            RequestBody nama = RequestBody.create(MediaType.parse("multipart/form-data"), namaguru.getText().toString());
            RequestBody tgl_lahir = RequestBody.create(MediaType.parse("multipart/form-data"), btntglguru.getText().toString());
            RequestBody jk = RequestBody.create(MediaType.parse("multipart/form-data"), btnjk.getText().toString());
            RequestBody agama = RequestBody.create(MediaType.parse("multipart/form-data"), btnagama.getText().toString());
            RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"), alamatguru.getText().toString());
            RequestBody hp = RequestBody.create(MediaType.parse("multipart/form-data"), hpguru.getText().toString());
            RequestBody id_category_kelas = RequestBody.create(MediaType.parse("multipart/form-data"), kelas);
            RequestBody id_mata_pelajaran = RequestBody.create(MediaType.parse("multipart/form-data"), pelajaran);

            if (bitmap == null){
                file = null;
                callsimpan = service.ubahProfileguru(tgl_lahir, jk, agama,alamat,hp,null,id_category_kelas, id_mata_pelajaran,nama);
            }
            else {
                file = createTempFile(bitmap);
                RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("foto_guru", file.getName(), reqFile);
                callsimpan = service.ubahProfileguru(tgl_lahir, jk, agama,alamat,hp,body,id_category_kelas, id_mata_pelajaran,nama);
            }

            callsimpan.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        new SweetAlertDialog(ProfileGuru.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Profile berhasil di ubah")
                                .show();
                    }
                    else {
                        if(response.code() == 422){
                            handleError(response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }catch (Exception ex){
            Toast.makeText(ProfileGuru.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void tampildata(){
        call = service.profileguru();
        call.enqueue(new Callback<com.example.bimbel.response.Guru.ProfileGuru>() {
            @Override
            public void onResponse(Call<com.example.bimbel.response.Guru.ProfileGuru> call, Response<com.example.bimbel.response.Guru.ProfileGuru> response) {
                if(response.isSuccessful()){
                    try {
                        namaguru.setText(response.body().getData().get(0).getNama());
                        btntglguru.setText(response.body().getData().get(0).getTanggal_lahir_guru());
                        btnjk.setText(response.body().getData().get(0).getJenkel_guru());
                        btnagama.setText(response.body().getData().get(0).getAgama_guru());
                        alamatguru.setText(response.body().getData().get(0).getAlamat_guru());
                        hpguru.setText(response.body().getData().get(0).getNo_handphone_guru());
                        if(response.body().getData().get(0).getFoto_guru() != null && response.body().getData().get(0).getFoto_guru().length() > 0){
                            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + response.body().getData().get(0).getFoto_guru()).placeholder(R.drawable.placeholder).into(imagePGuru);
                        } else{
                            Picasso.get().load(R.drawable.placeholder).into(imagePGuru);
                        }
                        int kelas = listSpinner.indexOf(response.body().getData().get(0).getNama_category_kelas());
                        kelasguru.setSelection(kelas);
                        int pelajaran = listSpinnerPelajaran.indexOf(response.body().getData().get(0).getMata_pelajaran());
                        pelajaranguru.setSelection(pelajaran);
                    }catch (Exception ex){
                        //Toast.makeText(ProfileGuru.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<com.example.bimbel.response.Guru.ProfileGuru> call, Throwable t) {
                Toast.makeText(ProfileGuru.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void hasiltgl(){
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try {
                    if (month < 10 && dayOfMonth < 10) {
                        String fmonth = "0" + month;
                        month = Integer.parseInt(fmonth) + 1;
                        String fDate = "0" + dayOfMonth;
                        String paddedMonth = String.format("%02d", month);
                        String date =  year + "-" + paddedMonth + "-" + fDate;
                        btntglguru.setText(date);
                    } else {
                        if(dayOfMonth >= 10){
                            String fmonth = "0" + month;
                            month = Integer.parseInt(fmonth) + 1;
                            String paddedMonth = String.format("%02d", month);
                            String date = year + "-" + paddedMonth + "-" + dayOfMonth;
                            btntglguru.setText(date);
                        }
                        else{
                            String fmonth = "0" + month;
                            month = Integer.parseInt(fmonth) + 1;
                            String fDate = "0" + dayOfMonth;
                            String paddedMonth = String.format("%02d", month);
                            String date = year + "-" + paddedMonth + "-" + fDate;
                            btntglguru.setText(date);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @OnClick(R.id.tglguru)
    void tglguru(){
        int year, month,day;
        String hasil = btntglguru.getText().toString();
        Calendar calendar = Calendar.getInstance();
        if(hasil.equals("Atur Sekarang")){
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = calendar.MONTH;
            day = calendar.DAY_OF_MONTH;
        }
        else{
            year = Integer.parseInt(hasil.substring(0,4));
            month = Integer.parseInt(hasil.substring(5,7)) - 1;
            day = Integer.parseInt(hasil.substring(8,10));
        }

        DatePickerDialog dialog = new DatePickerDialog(
                ProfileGuru.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDataSetListener,
                year,month,day
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @OnClick(R.id.jk)
    void jk(){
        String[] singleChoiceItems = getResources().getStringArray(R.array.jenis_kelamin);
        int itemSelected = 0;
        if(btnjk.getText().toString().equals("Pria")){ itemSelected = 0; }
        else{ itemSelected = 1; }
        new AlertDialog.Builder(ProfileGuru.this)
                .setTitle("Pilih Jenis Kelamin")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnjk.setText(singleChoiceItems[which]);
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.agama)
    void agama(){
        String[] singleChoiceItems = getResources().getStringArray(R.array.agama);
        int itemSelected = 0;
        for (int x = 0; x < singleChoiceItems.length; x++){
            if(btnagama.getText().toString().equals(singleChoiceItems[x])){
                itemSelected = x;
            }
        }
        new AlertDialog.Builder(ProfileGuru.this)
                .setTitle("Pilih Agama")
                .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnagama.setText(singleChoiceItems[which]);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    void tampilKelas(){
        callKelas = service.kelas();
        callKelas.enqueue(new Callback<DataKelas>() {
            @Override
            public void onResponse(Call<DataKelas> call, Response<DataKelas> response) {
                listSpinner = new ArrayList<String>();
                idkelas = new ArrayList<String>();
                for(int i =0; i<response.body().getData().size(); i++){
                    idkelas.add(response.body().getData().get(i).getId_category_kelas());
                    listSpinner.add(response.body().getData().get(i).getNama_category_kelas());
                }
                adapterKelas = new ArrayAdapter<String>(ProfileGuru.this,
                        R.layout.spinner_item, listSpinner);
                kelasguru.setAdapter(adapterKelas);
                kelasguru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectKelas = parent.getItemAtPosition(position).toString();
                        callMataPelajaran = service.pelajaran(selectKelas);
                        callMataPelajaran.enqueue(new Callback<DataMataPelajaran>() {
                            @Override
                            public void onResponse(Call<DataMataPelajaran> call, Response<DataMataPelajaran> response) {
                                idmtapelajaran = new ArrayList<String>();
                                listSpinnerPelajaran = new ArrayList<String>();
                                for(int i =0; i<response.body().getData().size(); i++){
                                    idmtapelajaran.add(response.body().getData().get(i).getId_mata_pelajaran());
                                    listSpinnerPelajaran.add(response.body().getData().get(i).getMata_pelajaran());
                                }
                                adapterpelajaran = new ArrayAdapter<String>(ProfileGuru.this,
                                        R.layout.spinner_item, listSpinnerPelajaran);
                                pelajaranguru.setAdapter(adapterpelajaran);
                            }
                            @Override
                            public void onFailure(Call<DataMataPelajaran> call, Throwable t) {
                                Toast.makeText(ProfileGuru.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void onFailure(Call<DataKelas> call, Throwable t) {
                Toast.makeText(ProfileGuru.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        tampildata();
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Path = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Path);
                imagePGuru.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() +"_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.WEBP,0, bos);
        byte[] bitmapdata = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void handleError(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("nama")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("tanggal_lahir_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("jenkel_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("agama_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("alamat_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("foto_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("no_handphone_guru")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("id_category_kelas")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("id_mata_pelajaran")){
                Toast.makeText(ProfileGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
        }
    }

}
