package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.bimbel.adapter.RuangBacaAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.TokenManager;

import java.util.List;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RuangBaca extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ApiService service;
    Call<List<Berita>> call;
    TokenManager tokenManager;
    ProgressBar progressBar;
    private RuangBacaAdapter adapter;
    private ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruang_baca);
        getSupportActionBar().setTitle("Ruang Baca");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        listView = (ListView)findViewById(R.id.listBaca);
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
                Intent i = new Intent(RuangBaca.this, DetailBerita.class);
                String idberita = ((TextView) view.findViewById(R.id.txtid)).getText().toString();
                i.putExtra("data", idberita);
                startActivity(i);
            }
        });
    }

    private void isiData(List<Berita> berita){
        adapter = new RuangBacaAdapter(this, berita);
        listView.setAdapter(adapter);
    }

    void tampildata(){
        call = service.berita();
        call.enqueue(new Callback<List<Berita>>() {
            @Override
            public void onResponse(Call<List<Berita>> call, Response<List<Berita>> response) {
                if(response.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    isiData(response.body());
                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(RuangBaca.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Berita>> call, Throwable t) {
                new SweetAlertDialog(RuangBaca.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
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
        searchView.setQueryHint("Cari Berita");

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
