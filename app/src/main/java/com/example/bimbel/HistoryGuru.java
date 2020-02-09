package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.adapter.HistoryGuruAdapter;
import com.example.bimbel.adapter.RuangBacaAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.TokenManager;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryGuru extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = "HistoryGuru";
    
    ApiService service;
    Call<List<com.example.bimbel.response.History.HistoryGuru>> call;
    TokenManager tokenManager;
    ProgressBar progressBar;
    private HistoryGuruAdapter adapter;
    private ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_guru);

        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listHistoryGuru);
        progressBar = findViewById(R.id.myProgressbar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simpleSwipeRefreshLayout);

        tampildata();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                tampildata();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HistoryGuru.this, DetailHistoryGuru.class);
                String kode_siswa = ((TextView) view.findViewById(R.id.txtHistoryKodeSiswa)).getText().toString();
                String id_history = ((TextView) view.findViewById(R.id.txtidHistorySiswa)).getText().toString();
                i.putExtra("data", kode_siswa);
                i.putExtra("data1", id_history);
                startActivity(i);
            }
        });
    }


    @Override
    protected void onResume() {
        tampildata();
        super.onResume();
    }

    private void isiData(List<com.example.bimbel.response.History.HistoryGuru> historyGuru){
        adapter = new HistoryGuruAdapter(this, historyGuru);
        listView.setAdapter(adapter);
    }

    void tampildata(){
        call = service.historyguru();
        call.enqueue(new Callback<List<com.example.bimbel.response.History.HistoryGuru>>() {
            @Override
            public void onResponse(Call<List<com.example.bimbel.response.History.HistoryGuru>> call, Response<List<com.example.bimbel.response.History.HistoryGuru>> response) {
                if(response.isSuccessful()){
                    try {
                        progressBar.setVisibility(View.GONE);
                        isiData(response.body());
                    }catch (Exception ex){
                        Toast.makeText(HistoryGuru.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(HistoryGuru.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.example.bimbel.response.History.HistoryGuru>> call, Throwable t) {

            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_baca, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.option_menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Cari History");

        return true;
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
