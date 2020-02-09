package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.History.HistoryGuru;
import com.example.bimbel.response.History.HistorySiswa;
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

public class DetailHistoryGuru extends AppCompatActivity {

    Funct funct;

    @BindView(R.id.HistoryDetailFotoSiswa)
    ImageView HistoryDetailFotoSiswa;
    @BindView(R.id.HistoryDetailNamaSiswa)
    TextView HistoryDetailNamaSiswa;
    @BindView(R.id.HistoryDetailJenkelSiswa)
    TextView HistoryDetailJenkelSiswa;
    @BindView(R.id.HistoryDetailTglSiswa)
    TextView HistoryDetailTglSiswa;
    @BindView(R.id.HistoryDetailHpSiswa)
    TextView HistoryDetailHpSiswa;
    @BindView(R.id.HistoryDetailAlamatSiswa)
    TextView HistoryDetailAlamatSiswa;
    @BindView(R.id.idHistoryGuru)
    TextView idHistoryGuru;
    @BindView(R.id.BtnHistoryTerimaGuru)
    Button BtnHistoryTerimaGuru;
    @BindView(R.id.BtnHistoryTolakGuru)
    Button BtnHistoryTolakGuru;

    ApiService apiService;
    TokenManager tokenManager;
    Call<List<HistoryGuru>> call;
    Call<ResponseBody> callterima;
    Call<ResponseBody> calltolak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_guru);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);

        funct = new Funct();

        try{
            call = apiService.detailhistoryguru(getIntent().getStringExtra("data"), getIntent().getStringExtra("data1"));
            call.enqueue(new Callback<List<HistoryGuru>>() {
                @Override
                public void onResponse(Call<List<HistoryGuru>> call, Response<List<HistoryGuru>> response) {
                    if(response.isSuccessful()){
                        try {
                            HistoryDetailNamaSiswa.setText(response.body().get(0).getNama());
                            HistoryDetailJenkelSiswa.setText(response.body().get(0).getJenkel_siswa());
                            HistoryDetailTglSiswa.setText(funct.TanggalIndo(response.body().get(0).getTanggal_lahir_siswa()));
                            HistoryDetailHpSiswa.setText(response.body().get(0).getNo_handphone_siswa());
                            HistoryDetailAlamatSiswa.setText(response.body().get(0).getAlamat_siswa());
                            idHistoryGuru.setText(response.body().get(0).getId_history());

                            if(response.body().get(0).getFoto_siswa() != null && response.body().get(0).getFoto_siswa().length() > 0){
                                Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" +response.body().get(0).getFoto_siswa()).placeholder(R.drawable.placeholder).into(HistoryDetailFotoSiswa);
                            } else{
                                Picasso.get().load(R.drawable.placeholder).into(HistoryDetailFotoSiswa);
                            }

                            if(response.body().get(0).getStatus_history().equals("0")){
                                BtnHistoryTerimaGuru.setVisibility(View.VISIBLE);
                                BtnHistoryTolakGuru.setVisibility(View.VISIBLE);
                            }

                            if(response.body().get(0).getStatus_history().equals("2") || response.body().get(0).getStatus_history().equals("1")){
                                BtnHistoryTerimaGuru.setVisibility(View.GONE);
                                BtnHistoryTolakGuru.setVisibility(View.GONE);
                            }
                        }catch (Exception ex){
                            new SweetAlertDialog(DetailHistoryGuru.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(ex.getMessage())
                                    .show();
                        }

                    }
                    else{
                        if(response.code() == 401){
                            startActivity(new Intent(DetailHistoryGuru.this, ActivityFormLoginRegister.class));
                            finish();
                            tokenManager.deleteToken();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<HistoryGuru>> call, Throwable t) {
                    new SweetAlertDialog(DetailHistoryGuru.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(t.getMessage())
                            .show();
                }
            });
        }catch (Exception ex){
            new SweetAlertDialog(DetailHistoryGuru.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(ex.getMessage())
                    .show();
        }

    }

    @OnClick(R.id.BtnHistoryTerimaGuru)
    void terima(){
        callterima =  apiService.terimaBooking(idHistoryGuru.getText().toString());
        callterima.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(DetailHistoryGuru.this, "berhasil menerima booking", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DetailHistoryGuru.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.BtnHistoryTolakGuru)
    void tolak(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                DetailHistoryGuru.this);

        alertDialog2.setTitle("Tolak Booking");

        alertDialog2.setMessage("Anda yakin ingin menolak booking?");

        alertDialog2.setIcon(R.drawable.tolak);

        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            calltolak = apiService.tolakBooking(idHistoryGuru.getText().toString());
                            calltolak.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(DetailHistoryGuru.this, "berhasil tolak booking", Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(DetailHistoryGuru.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }catch (Exception ex){
                            Toast.makeText(DetailHistoryGuru.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

        alertDialog2.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog2.show();
    }
}
