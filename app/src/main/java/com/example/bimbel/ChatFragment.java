package com.example.bimbel;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bimbel.adapter.ChatSiswaAdapter;
import com.example.bimbel.adapter.SiswaAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Chat.ChatSiswa;
import com.example.bimbel.response.Siswa.Siswa;
import com.example.bimbel.response.TokenManager;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    ApiService service;
    Call<List<ChatSiswa>> call;
    TokenManager tokenManager;

    private ChatSiswaAdapter adapter;
    private ListView listView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        listView = view.findViewById(R.id.listChatSiswa);
        tampildata();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), Chat.class);
                String idsiswa = ((TextView) view.findViewById(R.id.idsiswa)).getText().toString();
                String idguru = ((TextView) view.findViewById(R.id.idguru)).getText().toString();
                String idchat = ((TextView) view.findViewById(R.id.idchat)).getText().toString();
                i.putExtra("idsiswa", idsiswa);
                i.putExtra("idguru", idguru);
                i.putExtra("idchat", idchat);
                startActivity(i);
            }
        });

        return view;
    }

    private void isiData(List<ChatSiswa> siswa){
        adapter = new ChatSiswaAdapter(getActivity(), siswa);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setScrollbarFadingEnabled(false);
    }

    void tampildata(){
        call = service.chatsiswa();
        call.enqueue(new Callback<List<ChatSiswa>>() {
            @Override
            public void onResponse(Call<List<ChatSiswa>> call, Response<List<ChatSiswa>> response) {
                isiData(response.body());
            }

            @Override
            public void onFailure(Call<List<ChatSiswa>> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }
}
