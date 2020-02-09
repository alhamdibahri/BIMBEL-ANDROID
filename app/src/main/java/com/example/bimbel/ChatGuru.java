package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bimbel.adapter.ChatGuruAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.TokenManager;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatGuru extends AppCompatActivity {

    ApiService service;
    Call<List<com.example.bimbel.response.Chat.ChatGuru>> call;
    TokenManager tokenManager;

    private ChatGuruAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_guru);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        listView = findViewById(R.id.listChatGuru);
        tampildata();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ChatGuru.this, Chat.class);
                String idsiswa = ((TextView) view.findViewById(R.id.idsiswa)).getText().toString();
                String idguru = ((TextView) view.findViewById(R.id.idguru)).getText().toString();
                String idchat = ((TextView) view.findViewById(R.id.idchat)).getText().toString();
                i.putExtra("idsiswa", idsiswa);
                i.putExtra("idguru", idguru);
                i.putExtra("idchat", idchat);
                startActivity(i);
            }
        });
    }

    private void isiData(List<com.example.bimbel.response.Chat.ChatGuru> guru){
        adapter = new ChatGuruAdapter(this, guru);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setScrollbarFadingEnabled(false);
    }

    void tampildata(){
        call = service.chatguru();
        call.enqueue(new Callback<List<com.example.bimbel.response.Chat.ChatGuru>>() {
            @Override
            public void onResponse(Call<List<com.example.bimbel.response.Chat.ChatGuru>> call, Response<List<com.example.bimbel.response.Chat.ChatGuru>> response) {
                isiData(response.body());
            }

            @Override
            public void onFailure(Call<List<com.example.bimbel.response.Chat.ChatGuru>> call, Throwable t) {
                new SweetAlertDialog(ChatGuru.this, SweetAlertDialog.ERROR_TYPE)
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
}
