package com.example.bimbel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bimbel.Chat;
import com.example.bimbel.DetailGuru;
import com.example.bimbel.R;
import com.example.bimbel.model.Guru;
import com.example.bimbel.response.LevelManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GuruAdapter extends RecyclerView.Adapter<GuruAdapter.MyViewHolder> {

    List<Guru> guru;
    LevelManager levelManager;
    Context context;

    public GuruAdapter(Context context, List<Guru> guru) {
        this.context = context;
        this.guru = guru;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtnama, txtpelajaran, txtkelas;
        public ImageView imageView;
        public Button btnDetailGuru, btnchat;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtnama = (TextView) itemView.findViewById(R.id.textNamaGuru);
            txtpelajaran = (TextView) itemView.findViewById(R.id.txtpelajaran);
            txtkelas = (TextView) itemView.findViewById(R.id.txtkelas);
            imageView = (ImageView) itemView.findViewById(R.id.imageGuru);
            btnDetailGuru = (Button)itemView.findViewById(R.id.btnDetailGuru);
            btnchat = (Button)itemView.findViewById(R.id.btnchat);
        }
    }

    @NonNull
    @Override
    public GuruAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.tmp_data_guru, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mview);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GuruAdapter.MyViewHolder holder, int position) {
        holder.txtnama.setText(guru.get(position).getNama());
        holder.txtpelajaran.setText(guru.get(position).getMata_pelajaran());
        holder.txtkelas.setText(guru.get(position).getNama_category_kelas());

        levelManager = LevelManager.getInstance(context.getSharedPreferences("LEVEL", MODE_PRIVATE));


        if(guru.get(position).getFoto_guru() != null && guru.get(position).getFoto_guru().length() > 0){
            Picasso.get().load("http://192.168.43.206/bimbel/public/fotoguru/" + guru.get(position).getFoto_guru()).placeholder(R.drawable.placeholder).into(holder.imageView);
        } else{
            Picasso.get().load(R.drawable.placeholder).into(holder.imageView);
        }

        if(levelManager.ambilLevel().getLevel().equals("Guru")){
            holder.btnchat.setVisibility(View.GONE);
        }

        holder.btnDetailGuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailGuru.class);
                intent.putExtra("data", guru.get(position).getKode_guru());
                v.getContext().startActivity(intent);

            }
        });

        holder.btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Chat.class);
                intent.putExtra("idguru", guru.get(position).getKode_guru());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return guru.size();
    }
}
