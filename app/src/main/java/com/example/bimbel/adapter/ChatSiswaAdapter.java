package com.example.bimbel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bimbel.Funct;
import com.example.bimbel.R;
import com.example.bimbel.response.Chat.ChatSiswa;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatSiswaAdapter extends BaseAdapter {

    Funct funct;
    private List<ChatSiswa> list;
    private Context context;

    public ChatSiswaAdapter(Context context, List<ChatSiswa> list){
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.tmp_chat, parent, false);
        }

        funct = new Funct();

        TextView nama_chat = convertView.findViewById(R.id.nama_chat);
        ImageView imgChat = convertView.findViewById(R.id.imgChat);
        TextView tgl_chat = convertView.findViewById(R.id.tgl_chat);
        TextView idguru = convertView.findViewById(R.id.idguru);
        TextView idsiswa = convertView.findViewById(R.id.idsiswa);
        TextView idchat = convertView.findViewById(R.id.idchat);

        ChatSiswa chat = list.get(position);

        idguru.setText(chat.getKode_guru());
        idsiswa.setText(chat.getKode_siswa());
        idchat.setText(chat.getId_chat());

        tgl_chat.setText(funct.TanggalIndo(chat.getTgl_chat()));
        nama_chat.setText(chat.getNama());

        if(chat.getFoto_guru() != null && chat.getFoto_guru().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + chat.getFoto_guru()).placeholder(R.drawable.placeholder).into(imgChat);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(imgChat);
        }

        return convertView;
    }
}
