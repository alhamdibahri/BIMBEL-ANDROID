package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.adapter.ChatHistoryAdapter;
import com.example.bimbel.adapter.RuangBacaAdapter;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.Chat.DataChat;
import com.example.bimbel.response.Guru.ProfileGuruResponse;
import com.example.bimbel.response.LevelManager;
import com.example.bimbel.response.Siswa.Siswa;
import com.example.bimbel.response.TokenManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;

public class Chat extends AppCompatActivity {

    private WebSocket webSocket;
    MessageAdapter messageAdapter;
    ChatHistoryAdapter chatAdapter;
    ApiService service;
    Call<List<Siswa>> call;
    Call<List<ProfileGuruResponse>> call1;
    Call<List<DataChat>> coba;
    Call<ResponseBody> callChat;
    TokenManager tokenManager;
    LevelManager levelManager;
    TextView nm;
    ImageView v;
    ListView messagelist;
    private static final String TAG = "Chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagelist = (ListView) findViewById(R.id.messagelist);
        EditText messagebox = findViewById(R.id.messagebox);
        TextView send = findViewById(R.id.kirim);
        getSupportActionBar().hide();

        instantianteWebSocket();

        v = (ImageView)findViewById(R.id.v);
        nm = (TextView)findViewById(R.id.nm);

        messageAdapter = new MessageAdapter();
        messagelist.setAdapter(messageAdapter);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        levelManager = LevelManager.getInstance(getSharedPreferences("level", MODE_PRIVATE));

        coba = service.coba(getIntent().getStringExtra("idchat"));
        coba.enqueue(new Callback<List<DataChat>>() {
            @Override
            public void onResponse(Call<List<DataChat>> call, retrofit2.Response<List<DataChat>> response) {
               isiData(response.body());
                messageAdapter = new MessageAdapter();
                messagelist.setAdapter(messageAdapter);
            }

            @Override
            public void onFailure(Call<List<DataChat>> call, Throwable t) {
            }
        });



        if(levelManager.ambilLevel().getLevel().equals("Guru")){
            call = service.guruchat(getIntent().getStringExtra("idsiswa"));
            call.enqueue(new Callback<List<Siswa>>() {
                @Override
                public void onResponse(Call<List<Siswa>> call, retrofit2.Response<List<Siswa>> response) {
                    nm.setText(response.body().get(0).getNama());
                    if(response.body().get(0).getFoto_siswa() != null && response.body().get(0).getFoto_siswa().length() > 0){
                        Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" + response.body().get(0).getFoto_siswa()).placeholder(R.drawable.placeholder).into(v);
                    } else{
                        Picasso.get().load(R.drawable.placeholder).into(v);
                    }
                }

                @Override
                public void onFailure(Call<List<Siswa>> call, Throwable t) {
                    new SweetAlertDialog(Chat.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(t.getMessage())
                            .show();
                }
            });
        }
        else{
            call1 = service.siswachat(getIntent().getStringExtra("idguru"));
            call1.enqueue(new Callback<List<ProfileGuruResponse>>() {
                @Override
                public void onResponse(Call<List<ProfileGuruResponse>> call, retrofit2.Response<List<ProfileGuruResponse>> response) {
                    nm.setText(response.body().get(0).getNama());
                    if(response.body().get(0).getFoto_guru() != null && response.body().get(0).getFoto_guru().length() > 0){
                        Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + response.body().get(0).getFoto_guru()).placeholder(R.drawable.placeholder).into(v);
                    } else{
                        Picasso.get().load(R.drawable.placeholder).into(v);
                    }
                }

                @Override
                public void onFailure(Call<List<ProfileGuruResponse>> call, Throwable t) {
                    new SweetAlertDialog(Chat.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(t.getMessage())
                            .show();
                }
            });
        }



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messagebox.getText().toString();
                if(!message.isEmpty()){
                    webSocket.send(message);
                    messagebox.setText("");
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", message);
                        jsonObject.put("byServer", false);
                        messageAdapter.addItem(jsonObject);
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }


                    try {
                        callChat = service.chat(getIntent().getStringExtra("idguru"), message, getIntent().getStringExtra("idsiswa"));
                        callChat.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(Chat.this, t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void isiData(List<DataChat> chat){
        chatAdapter = new ChatHistoryAdapter(this, chat);
        messagelist.setAdapter(chatAdapter);
    }


    private void instantianteWebSocket() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.43.206:8090").build();
        SocketListener socketListener = new SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);
    }

    public class SocketListener extends WebSocketListener{
        public Chat chat;
        public SocketListener(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            chat.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(chat, "Koneksi Berhasil", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);

            chat.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", text);
                        jsonObject.put("byServer", true);
                        messageAdapter.addItem(jsonObject);
                    }catch (JSONException ex){
                        ex.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
        }
    }

    public class MessageAdapter extends BaseAdapter{

        List<JSONObject> messagelist = new ArrayList<>();

        @Override
        public int getCount() {
            return messagelist.size();
        }

        @Override
        public Object getItem(int position) {
            return messagelist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.temp_message_list, parent, false);
            }

            TextView sentMessage = convertView.findViewById(R.id.sentmessage);
            TextView receivedMessage = convertView.findViewById(R.id.receivedmessage);

            JSONObject item = messagelist.get(position);

            try {
                if(item.getBoolean("byServer")){
                    receivedMessage.setVisibility(View.VISIBLE);
                    receivedMessage.setText(item.getString("message"));
                    sentMessage.setVisibility(View.INVISIBLE);
                }else{
                    sentMessage.setVisibility(View.VISIBLE);
                    sentMessage.setText(item.getString("message"));
                    receivedMessage.setVisibility(View.INVISIBLE);
                }
            }catch (JSONException ex){
                ex.printStackTrace();
            }


            return convertView;

        }

        void addItem(JSONObject item){
            messagelist.add(item);
            notifyDataSetChanged();
        }
    }


}
