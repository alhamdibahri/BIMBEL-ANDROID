package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.model.Guru;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.LevelManager;
import com.example.bimbel.response.TokenManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailGuru extends AppCompatActivity {

    private static final String TAG = "DetailGuru";
    
    @BindView(R.id.imageViewguru)
    ImageView imageViewguru;
    @BindView(R.id.textViewNamaGuru)
    TextView textViewNamaGuru;
    @BindView(R.id.textViewAlamatGuru)
    TextView textViewAlamatGuru;
    @BindView(R.id.textViewKelas)
    TextView textViewKelas;
    @BindView(R.id.TextViewPelajaran)
    TextView TextViewPelajaran;
    @BindView(R.id.TextViewHandphone)
    TextView TextViewHandphone;
    @BindView(R.id.buttonBooking)
    Button buttonBooking;

    ApiService service;
    Call<List<Guru>> call;
    TokenManager tokenManager;
    Call<ResponseBody> callSimpan;
    LevelManager levelManager;
    String kode_guru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_guru);
        getSupportActionBar().hide();
        levelManager = LevelManager.getInstance(getSharedPreferences("LEVEL", MODE_PRIVATE));
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        tampildata();
        if(levelManager.ambilLevel().getLevel().equals("Guru")){
            buttonBooking.setVisibility(View.GONE);
        }

        kode_guru = getIntent().getStringExtra("data");
    }

    @OnClick(R.id.buttonBooking)
    void booking(){
        callSimpan = service.simpanHistory(kode_guru);
        callSimpan.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    buttonBooking.setEnabled(false);
                    new SweetAlertDialog(DetailGuru.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Booking berhasil silahkan cek menu history")
                            .show();
                }else{
                    if(response.code() == 401){
                        startActivity(new Intent(DetailGuru.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                    if(response.code() == 400){
                        new SweetAlertDialog(DetailGuru.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("anda sudah melakukan booking silahkan tunggu konfirmasi!")
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new SweetAlertDialog(DetailGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    void tampildata(){
        call = service.detailguru(getIntent().getStringExtra("data"));
        call.enqueue(new Callback<List<Guru>>() {
            @Override
            public void onResponse(Call<List<Guru>> call, Response<List<Guru>> response) {
                if(response.isSuccessful()){
                    if(response.body().get(0).getFoto_guru() != null && response.body().get(0).getFoto_guru().length() > 0){
                        Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + response.body().get(0).getFoto_guru()).placeholder(R.drawable.placeholder).into(imageViewguru);
                    } else{
                        Picasso.get().load(R.drawable.placeholder).into(imageViewguru);
                    }
                    textViewNamaGuru.setText(response.body().get(0).getNama());
                    textViewAlamatGuru.setText(response.body().get(0).getAlamat_guru());
                    textViewKelas.setText(response.body().get(0).getNama_category_kelas());
                    TextViewPelajaran.setText(response.body().get(0).getMata_pelajaran());
                    TextViewHandphone.setText(response.body().get(0).getNo_handphone_guru());
                }else{
                    if(response.code() == 401){
                        startActivity(new Intent(DetailGuru.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Guru>> call, Throwable t) {
                new SweetAlertDialog(DetailGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }
}
