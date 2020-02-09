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
import com.example.bimbel.response.Berita.Berita;
import com.example.bimbel.response.Chat.DataChat;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatHistoryAdapter  extends BaseAdapter {

    Context context;
    List<DataChat> list;

    public ChatHistoryAdapter(Context context, List<DataChat> list) {
        this.context = context;
        this.list = list;
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
        if (convertView == null){
            LayoutInflater inflater =LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.temp_message_list, null);
        }

        TextView sentMessage = convertView.findViewById(R.id.sentmessage);
        TextView receivedMessage = convertView.findViewById(R.id.receivedmessage);

        DataChat chat = list.get(position);

        if(position % 2 == 1){
            sentMessage.setVisibility(View.VISIBLE);
            sentMessage.setText(chat.getIsi_chat());
            receivedMessage.setVisibility(View.INVISIBLE);
        }
        else{
            receivedMessage.setVisibility(View.VISIBLE);
            receivedMessage.setText(chat.getIsi_chat());
            sentMessage.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }
}
