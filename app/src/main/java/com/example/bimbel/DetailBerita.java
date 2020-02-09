package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.TokenManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailBerita extends AppCompatActivity {

    @BindView(R.id.txtjudulBerita)
    TextView txtjudulBerita;
    @BindView(R.id.imgBerita)
    ImageView imgBerita;
    @BindView(R.id.txtdeskripsiBerita)
    TextView txtdeskripsiBerita;

    ApiService apiService;
    TokenManager tokenManager;
    Call<Berita> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_berita);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        call = apiService.detailberita(getIntent().getStringExtra("data"));
        call.enqueue(new Callback<Berita>() {
            @Override
            public void onResponse(Call<Berita> call, Response<Berita> response) {
                if(response.isSuccessful()){
                    txtjudulBerita.setText(response.body().getNama_berita());
                    txtdeskripsiBerita.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        txtdeskripsiBerita.setText(Html.fromHtml(response.body().getDeskripsi(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        txtdeskripsiBerita.setText(Html.fromHtml(response.body().getDeskripsi()));
                    }

                    if(response.body().getFoto_berita() != null && response.body().getFoto_berita().length() > 0){
                        Picasso.get().load("http://192.168.43.206/bimbel/public/fotoberita/" + response.body().getFoto_berita()).placeholder(R.drawable.placeholder).into(imgBerita);
                    } else{
                        Picasso.get().load(R.drawable.placeholder).into(imgBerita);
                    }
                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(DetailBerita.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<Berita> call, Throwable t) {
                new SweetAlertDialog(DetailBerita.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }
}
