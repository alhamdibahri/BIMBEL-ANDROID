package com.example.bimbel;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.bimbel.adapter.GuruAdapter;
import com.example.bimbel.model.Guru;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Kelas.DataKelas;
import com.example.bimbel.response.MataPelajaran.DataMataPelajaran;
import com.example.bimbel.response.TokenManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataGuru extends AppCompatActivity {

    ApiService service;
    Call<List<Guru>> call;
    Call<DataKelas> callKelas;
    Call<DataMataPelajaran> callMataPelajaran;
    TokenManager tokenManager;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Toolbar t1;
    DrawerLayout mDrawerLayout;
    Button btn_filter, btnCari;
    ActionBarDrawerToggle mDrawerToggle;
    LinearLayout drawer_right;
    EditText cariguru1;
    Spinner spinnermatkul;
    Spinner spinnerpelajaran;

    ArrayAdapter<String> adapterKelas;
    List<String> kelasSpinner;
    ArrayAdapter<String> adapterPelajaran;
    List<String> pelajaranSpinner;
    String kelas;
    String pelajaran;

    private static final String TAG = "DataGuru";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_guru);
        getSupportActionBar().hide();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyleView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_data_guru);
        btn_filter = (Button)findViewById(R.id.btnfilter);
        btnCari = (Button)findViewById(R.id.btnCari);
        drawer_right = (LinearLayout)findViewById(R.id.drawer_right);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        spinnermatkul = (Spinner)findViewById(R.id.spinnermatkul);
        spinnerpelajaran = (Spinner)findViewById(R.id.spinnerpelajaran);
        cariguru1 = (EditText) findViewById(R.id.cariguru);

        cariguru1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    carigurukelas();
                    return true;
                }
                return false;
            }
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerOpen(drawer_right)){
                    mDrawerLayout.closeDrawer(drawer_right);
                }else if(!mDrawerLayout.isDrawerOpen(drawer_right)){
                    mDrawerLayout.openDrawer(drawer_right);
                }
            }
        });

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerOpen(drawer_right)) {
                    mDrawerLayout.closeDrawer(drawer_right);
                }
                carigurukelas();
            }
        });
        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);

        progressBar = findViewById(R.id.myProgressbar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        tampilKelas();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                tampilKelas();
                if(getIntent().getStringExtra("data") == null){
                    refresh();
                }else{
                    carigurukelas();
                }

            }
        });

        cariguru1.setText(getIntent().getStringExtra("data"));

        if(getIntent().getStringExtra("data") != null){
            carigurukelas();
        }
        else{
            refresh();
        }

    }

    void tampilKelas(){
        callKelas = service.kelas();
        callKelas.enqueue(new Callback<DataKelas>() {
            @Override
            public void onResponse(Call<DataKelas> call, Response<DataKelas> response) {
                kelasSpinner = new ArrayList<String>();
                for(int i =0; i<response.body().getData().size(); i++){
                    kelasSpinner.add(response.body().getData().get(i).getNama_category_kelas());
                }

                adapterKelas = new ArrayAdapter<String>(DataGuru.this,
                        R.layout.spinner_item, kelasSpinner);
                spinnermatkul.setAdapter(adapterKelas);
                kelas = kelasSpinner.get(spinnermatkul.getSelectedItemPosition());
                spinnermatkul.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectKelas = parent.getItemAtPosition(position).toString();
                        callMataPelajaran = service.pelajaran(selectKelas);
                        callMataPelajaran.enqueue(new Callback<DataMataPelajaran>() {
                            @Override
                            public void onResponse(Call<DataMataPelajaran> call, Response<DataMataPelajaran> response) {
                                pelajaranSpinner = new ArrayList<String>();
                                for(int i =0; i<response.body().getData().size(); i++){
                                    pelajaranSpinner.add(response.body().getData().get(i).getMata_pelajaran());
                                }
                                adapterPelajaran = new ArrayAdapter<String>(DataGuru.this,
                                        R.layout.spinner_item, pelajaranSpinner);
                                spinnerpelajaran.setAdapter(adapterPelajaran);
                                pelajaran = pelajaranSpinner.get(spinnerpelajaran.getSelectedItemPosition());
                            }

                            @Override
                            public void onFailure(Call<DataMataPelajaran> call, Throwable t) {
                                Toast.makeText(DataGuru.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DataGuru.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh() {
        call = service.guru();

        call.enqueue(new Callback<List<Guru>>() {
            @Override
            public void onResponse(Call<List<Guru>> call, Response<List<Guru>> response) {
                progressBar.setVisibility(View.GONE);
                mAdapter = new GuruAdapter(DataGuru.this,response.body());
                mRecyclerView.setAdapter(mAdapter);
                check();
            }

            @Override
            public void onFailure(Call<List<Guru>> call, Throwable t) {
                new SweetAlertDialog(DataGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    void carigurukelas(){
        call = service.carigurukelas(cariguru1.getText().toString(), kelas, pelajaran);
        call.enqueue(new Callback<List<Guru>>() {
            @Override
            public void onResponse(Call<List<Guru>> call, Response<List<Guru>> response) {
                progressBar.setVisibility(View.GONE);
                mAdapter = new GuruAdapter(DataGuru.this,response.body());
                mRecyclerView.setAdapter(mAdapter);
                check();
            }

            @Override
            public void onFailure(Call<List<Guru>> call, Throwable t) {
                new SweetAlertDialog(DataGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    void check(){
        if(mAdapter.getItemCount() == 0){
            Toast.makeText(DataGuru.this, "Data tidak ditemukan", Toast.LENGTH_LONG).show();
        }
    }

}
