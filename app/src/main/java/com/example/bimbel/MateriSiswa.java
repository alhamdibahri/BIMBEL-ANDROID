package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.bimbel.adapter.MateriGuruAdapter;
import com.example.bimbel.adapter.RuangBacaAdapter;
import com.example.bimbel.adapter.SiswaAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.MateriGuru.MateriGuru;
import com.example.bimbel.response.TokenManager;

import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriSiswa extends AppCompatActivity implements SearchView.OnQueryTextListener{

    TokenManager tokenManager;
    ApiService service;
    Call<List<MateriGuru>> call;
    private MateriGuruAdapter adapter;
    private ListView listView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int MY_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi_siswa);
        getSupportActionBar().setTitle("Materi Guru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        ButterKnife.bind(this);
        listView = (ListView)findViewById(R.id.listMateri);
        progressBar = findViewById(R.id.myProgressbar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);
        tampildata();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                tampildata();
            }
        });

        if(ContextCompat.checkSelfPermission(MateriSiswa.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MateriSiswa.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSION_REQUEST);
        }
    }



    private void isiData(List<MateriGuru> materiGuru){
        adapter = new MateriGuruAdapter(this, materiGuru);
        listView.setAdapter(adapter);
    }

    void tampildata(){
        call = service.materisiswa();
        call.enqueue(new Callback<List<MateriGuru>>() {
            @Override
            public void onResponse(Call<List<MateriGuru>> call, Response<List<MateriGuru>> response) {
                progressBar.setVisibility(View.GONE);
                isiData(response.body());
            }

            @Override
            public void onFailure(Call<List<MateriGuru>> call, Throwable t) {
                new SweetAlertDialog(MateriSiswa.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_baca, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.option_menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Cari Materi");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }
}
