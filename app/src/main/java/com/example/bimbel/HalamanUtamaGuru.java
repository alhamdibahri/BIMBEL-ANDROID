package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Guru.GuruResponse;
import com.example.bimbel.response.LevelManager;
import com.example.bimbel.response.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.logging.Level;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalamanUtamaGuru extends AppCompatActivity {

    private static final String TAG = "HalamanUtamaGuru";

    Button btnmenu,home,msg,so, settings,pr,btnMateri;
    RelativeLayout maincontent;
    LinearLayout mainmenu;
    Animation fromtop,frombottom;
    ImageView userbg;
    TextView user;
    TokenManager tokenManager;
    LevelManager levelManager;
    ApiService apiService;
    Call<GuruResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_guru);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        btnmenu =(Button) findViewById(R.id.btnmenu);

        home =(Button) findViewById(R.id.history);
        msg =(Button) findViewById(R.id.msg);
        so =(Button) findViewById(R.id.so);
        pr =(Button) findViewById(R.id.pr);
        settings =(Button) findViewById(R.id.settings);
        user =(TextView) findViewById(R.id.namaa);
        userbg =(ImageView) findViewById(R.id.userbg);

        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);


        maincontent = (RelativeLayout) findViewById(R.id.maincontent);
        mainmenu = (LinearLayout) findViewById(R.id.mainmenu);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));

        if(tokenManager.getToken().getAccessToken() == null){
            startActivity(new Intent(HalamanUtamaGuru.this, MainActivity.class));
            finish();
        }

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        tampildata();

    }

    void tampildata(){
        call = apiService.dataguru();
        call.enqueue(new Callback<GuruResponse>() {
            @Override
            public void onResponse(Call<GuruResponse> call, Response<GuruResponse> response) {
                if(response.isSuccessful()){
                    try {
                        user.setText(response.body().getDatauser().get(0).getNama());
                        if(response.body().getDataguru().get(0).getFoto_guru() != null && response.body().getDataguru().get(0).getFoto_guru().length() > 0){
                            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + response.body().getDataguru().get(0).getFoto_guru()).placeholder(R.drawable.placeholder).into(userbg);
                        } else{
                            Picasso.get().load(R.drawable.placeholder).into(userbg);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(HalamanUtamaGuru.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<GuruResponse> call, Throwable t) {
                new SweetAlertDialog(HalamanUtamaGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    @OnClick(R.id.maincontent)
    void content(){
        mainmenu.animate().translationX(-800);
        maincontent.animate().translationX(-800);
    }

    @OnClick(R.id.btnmenu)
    void Menu(){
        mainmenu.animate().translationX(0);
        maincontent.animate().translationX(0);

        home.startAnimation(frombottom);
        msg.startAnimation(frombottom);
        so.startAnimation(frombottom);
        pr.startAnimation(frombottom);
        settings.startAnimation(frombottom);

        user.startAnimation(fromtop);
        userbg.startAnimation(fromtop);
    }

    @OnClick(R.id.so)
    void SignOut(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Apakah anda yakin ingin logout?")
                .setConfirmText("Ya")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        tokenManager = TokenManager.getInstance(getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE));
                        tokenManager.deleteToken();
                        levelManager = LevelManager.getInstance(getApplicationContext().getSharedPreferences("level", MODE_PRIVATE));
                        levelManager.deleteLevel();
                        Intent intent = new Intent(HalamanUtamaGuru.this, ActivityFormLoginRegister.class);
                        finish();
                        startActivity(intent);
                    }
                })
                .setCancelButton("Tidak", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @OnClick(R.id.settings)
    void settings(){
        startActivity(new Intent(HalamanUtamaGuru.this, SettingsAkun.class));
    }
    @OnClick(R.id.btn_dGuru)
    void Guru(){
        startActivity(new Intent(HalamanUtamaGuru.this, DataGuru.class));
    }
    @OnClick(R.id.btn_dSiswa)
    void Siswa(){
        startActivity(new Intent(HalamanUtamaGuru.this, DataSiswa.class));
    }
    @OnClick(R.id.btn_dBerita)
    void Berita(){
        startActivity(new Intent(HalamanUtamaGuru.this, RuangBaca.class));
    }
    @OnClick(R.id.pr)
    void profile(){
        Intent intent = new Intent(HalamanUtamaGuru.this, ProfileGuru.class);
        intent.putExtra("data", user.getText().toString());
        startActivity(intent);
    }
    @OnClick(R.id.history)
    void history(){
        startActivity(new Intent(HalamanUtamaGuru.this, HistoryGuru.class));
    }

    @OnClick(R.id.btnMateri)
    void materi(){
        startActivity(new Intent(HalamanUtamaGuru.this, MateriGuru.class));
    }

    @OnClick(R.id.msg)
    void chat(){
        startActivity(new Intent(HalamanUtamaGuru.this, ChatGuru.class));
    }

}
