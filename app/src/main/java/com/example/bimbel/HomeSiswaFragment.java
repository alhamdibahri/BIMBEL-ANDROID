package com.example.bimbel;


import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Siswa.AkunResponse;
import com.example.bimbel.response.TokenManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeSiswaFragment extends Fragment {


    @BindView(R.id.btnSettings)
    CardView btnSettings;
    @BindView(R.id.btnSiswa)
    CardView btnSiswa;
    @BindView(R.id.btn_Guru)
    CardView btn_Guru;
    @BindView(R.id.btnRuangBaca)
    CardView btnRuangBaca;
    @BindView(R.id.imgMenuGuru)
    ImageView imgMenuGuru;
    @BindView(R.id.txttampil)
    TextView txttampil;
    @BindView(R.id.txtCari)
    EditText txtCari;
    @BindView(R.id.historySiswa)
    CardView historySiswa;
    @BindView(R.id.btn_materi_siswa)
    CardView btn_materi_siswa;


    ApiService apiService;
    TokenManager tokenManager;
    Call<AkunResponse> call;
    SwipeRefreshLayout swipeRefreshLayout;

    public HomeSiswaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_siswa, container, false);
        ButterKnife.bind(this, view);


        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                tampildata();
            }
        });

        tampildata();

        txtCari.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        HalamanUtamaSiswa m1 = (HalamanUtamaSiswa) getActivity();
                        Intent intent = new Intent(m1, DataGuru.class);
                        intent.putExtra("data", txtCari.getText().toString());
                        startActivity(intent);
                        txtCari.setText("");
                        return true;
                    }catch (Exception ex){
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsAkun.class));
            }
        });

        btnSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DataSiswa.class));
            }
        });

        btn_Guru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DataGuru.class));
            }
        });

        btnRuangBaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RuangBaca.class));
            }
        });

        historySiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistorySiswa.class));
            }
        });

        btn_materi_siswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MateriSiswa.class));
            }
        });

        return view;
    }

    void tampildata(){
        call = apiService.setting();
        call.enqueue(new Callback<AkunResponse>() {
            @Override
            public void onResponse(Call<AkunResponse> call, Response<AkunResponse> response) {
                if(response.isSuccessful()){
                    txttampil.setText("Selamat Datang " + response.body().getData().get(0).getNama());
                }
                else{
                    startActivity(new Intent(getContext(), ActivityFormLoginRegister.class));
                    getActivity().finish();
                    tokenManager.deleteToken();
                }
            }

            @Override
            public void onFailure(Call<AkunResponse> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

}
