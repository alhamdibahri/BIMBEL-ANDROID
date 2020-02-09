package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
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

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class DetailHistorySiswa extends AppCompatActivity {

    private static final String TAG = "DetailHistorySiswa";

    @BindView(R.id.HistoryDetailFotoGuru)
    ImageView HistoryDetailFotoGuru;
    @BindView(R.id.HistoryDetailNamaGuru)
    TextView HistoryDetailNamaGuru;
    @BindView(R.id.HistoryDetailKelasGuru)
    TextView HistoryDetailKelasGuru;
    @BindView(R.id.HistoryDetailPelajaranGuru)
    TextView HistoryDetailPelajaranGuru;
    @BindView(R.id.HistoryDetailHpGuru)
    TextView HistoryDetailHpGuru;
    @BindView(R.id.HistoryDetailAlamatGuru)
    TextView HistoryDetailAlamatGuru;
    @BindView(R.id.idHistorySiswa)
    TextView idHistorySiswa;
    @BindView(R.id.BtnHistoryDetailGuru)
    Button BtnHistoryDetailGuru;
    @BindView(R.id.BtnHistorySelesaiGuru)
    Button BtnHistorySelesaiGuru;


    ApiService apiService;
    TokenManager tokenManager;
    Call<List<HistorySiswa>> call;
    Call<ResponseBody> callBatalkan;
    Call<ResponseBody> callSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history_siswa);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        call = apiService.detailhistorysiswa(getIntent().getStringExtra("data"), getIntent().getStringExtra("data1"));

        call.enqueue(new Callback<List<HistorySiswa>>() {
            @Override
            public void onResponse(Call<List<HistorySiswa>> call, Response<List<HistorySiswa>> response) {
                if(response.isSuccessful()){
                    HistoryDetailNamaGuru.setText(response.body().get(0).getNama());
                    HistoryDetailKelasGuru.setText(response.body().get(0).getNama_category_kelas());
                    HistoryDetailPelajaranGuru.setText(response.body().get(0).getMata_pelajaran());
                    HistoryDetailHpGuru.setText(response.body().get(0).getNo_handphone_guru());
                    HistoryDetailAlamatGuru.setText(response.body().get(0).getAlamat_guru());
                    idHistorySiswa.setText(response.body().get(0).getId_history());
                    if(response.body().get(0).getFoto_guru() != null && response.body().get(0).getFoto_guru().length() > 0){
                        Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" +response.body().get(0).getFoto_guru()).placeholder(R.drawable.placeholder).into(HistoryDetailFotoGuru);
                    } else{
                        Picasso.get().load(R.drawable.placeholder).into(HistoryDetailFotoGuru);
                    }

                    if(response.body().get(0).getStatus_history().equals("0")){
                        BtnHistoryDetailGuru.setVisibility(View.VISIBLE);
                        BtnHistorySelesaiGuru.setVisibility(View.GONE);
                    }

                    if(response.body().get(0).getStatus_history().equals("2")){
                        BtnHistoryDetailGuru.setVisibility(View.GONE);
                        BtnHistorySelesaiGuru.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(DetailHistorySiswa.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HistorySiswa>> call, Throwable t) {
                new SweetAlertDialog(DetailHistorySiswa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    @OnClick(R.id.BtnHistoryDetailGuru)
    void batalkanBooking(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                DetailHistorySiswa.this);

        alertDialog2.setTitle("Batalkan Booking");

        alertDialog2.setMessage("Anda yakin ingin membatalkan booking?");

        alertDialog2.setIcon(R.drawable.delete);

        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callBatalkan = apiService.batalkanBooking(idHistorySiswa.getText().toString());
                        callBatalkan.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Toast.makeText(DetailHistorySiswa.this, "Berhasil batal booking", Toast.LENGTH_LONG).show();
                                BtnHistoryDetailGuru.setEnabled(false);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(DetailHistorySiswa.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
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

    @OnClick(R.id.BtnHistorySelesaiGuru)
    void selesai(){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                DetailHistorySiswa.this);

        alertDialog2.setTitle("Selesai Booking");

        alertDialog2.setMessage("Anda yakin ingin selesai?");

        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        callSelesai = apiService.selesaiBooking(idHistorySiswa.getText().toString(), getIntent().getStringExtra("data"));
                        callSelesai.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Toast.makeText(DetailHistorySiswa.this, "Berhasil menyelesaikan booking", Toast.LENGTH_LONG).show();
                                BtnHistorySelesaiGuru.setEnabled(false);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(DetailHistorySiswa.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
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
