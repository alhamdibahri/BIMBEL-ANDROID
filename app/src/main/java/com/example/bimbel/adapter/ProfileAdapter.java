package com.example.bimbel.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bimbel.ActivityFormLoginRegister;
import com.example.bimbel.HalamanUtamaSiswa;
import com.example.bimbel.MainActivity;
import com.example.bimbel.R;
import com.example.bimbel.model.ProfileModel;
import com.example.bimbel.response.LevelManager;
import com.example.bimbel.response.TokenManager;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private ArrayList<ProfileModel> profileModels;

    TokenManager tokenManager;
    LevelManager levelManager;

    private DatePickerDialog.OnDateSetListener mDataSetListener;

    public static class ProfileViewHolder extends RecyclerView.ViewHolder{

        public TextView txt_judul, txt_hasil;
        public ImageView imageView;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_judul = itemView.findViewById(R.id.tmp_prof_judul);
            txt_hasil = itemView.findViewById(R.id.tmp_prof_hasil);
            imageView = itemView.findViewById(R.id.tmp_icon_prof);
        }
    }

    public ProfileAdapter(ArrayList<ProfileModel> profileModels){
        this.profileModels = profileModels;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        ProfileViewHolder profileViewHolder = new ProfileViewHolder(v);
        return  profileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {

        ProfileModel currentItem = profileModels.get(position);
        holder.txt_judul.setText(currentItem.getJudul());
        holder.txt_hasil.setText(currentItem.getHasil());
        holder.imageView.setImageResource(currentItem.getImage());

        if(currentItem.getJudul().equals("Tanggal Lahir")){
            mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    try {
                        if (month < 10 && dayOfMonth < 10) {
                            String fmonth = "0" + month;
                            month = Integer.parseInt(fmonth) + 1;
                            String fDate = "0" + dayOfMonth;
                            String paddedMonth = String.format("%02d", month);
                            String date =  year + "-" + paddedMonth + "-" + fDate;
                            holder.txt_hasil.setText(date);
                        } else {
                            if(dayOfMonth >= 10){
                                String fmonth = "0" + month;
                                month = Integer.parseInt(fmonth) + 1;
                                String paddedMonth = String.format("%02d", month);
                                String date = year + "-" + paddedMonth + "-" + dayOfMonth;
                                holder.txt_hasil.setText(date);
                            }
                            else{
                                String fmonth = "0" + month;
                                month = Integer.parseInt(fmonth) + 1;
                                String fDate = "0" + dayOfMonth;
                                String paddedMonth = String.format("%02d", month);
                                String date = year + "-" + paddedMonth + "-" + fDate;
                                holder.txt_hasil.setText(date);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int year, month,day;
                    String hasil = holder.txt_hasil.getText().toString();
                    Calendar calendar = Calendar.getInstance();
                    if(hasil.equals("Atur Sekarang")){
                        year = Calendar.getInstance().get(Calendar.YEAR);
                        month = calendar.MONTH;
                        day = calendar.DAY_OF_MONTH;
                    }
                    else{
                        year = Integer.parseInt(hasil.substring(0,4));
                        month = Integer.parseInt(hasil.substring(5,7)) - 1;
                        day = Integer.parseInt(hasil.substring(8,10));
                    }
                    DatePickerDialog dialog = new DatePickerDialog(
                            v.getContext(),
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDataSetListener,
                            year,month,day
                    );

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        }


        if(currentItem.getJudul().equals("Logout")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(v.getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Apakah anda yakin ingin logout?")
                            .setConfirmText("Ya")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    tokenManager = TokenManager.getInstance(v.getContext().getSharedPreferences("pref", MODE_PRIVATE));
                                    tokenManager.deleteToken();
                                    levelManager = LevelManager.getInstance(v.getContext().getSharedPreferences("level", MODE_PRIVATE));
                                    levelManager.deleteLevel();
                                    Intent intent = new Intent(v.getContext(), ActivityFormLoginRegister.class);
                                    ((Activity)v.getContext()).finish();
                                    v.getContext().startActivity(intent);
                                }
                            })
                            .setCancelButton("Tidak", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            });
        }

        if(currentItem.getJudul().equals("Jenis Kelamin")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] singleChoiceItems = v.getContext().getResources().getStringArray(R.array.jenis_kelamin);
                    int itemSelected = 0;

                    if(holder.txt_hasil.getText().toString().equals("Pria")){ itemSelected = 0; }
                    else{ itemSelected = 1; }
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Pilih Jenis Kelamin")
                            .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.txt_hasil.setText(singleChoiceItems[which]);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }

        if(currentItem.getJudul().equals("Agama")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] singleChoiceItems = v.getContext().getResources().getStringArray(R.array.agama);
                    int itemSelected = 0;

                    for (int x = 0; x < singleChoiceItems.length; x++){
                        if(holder.txt_hasil.getText().toString().equals(singleChoiceItems[x])){
                            itemSelected = x;
                        }
                    }

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Pilih Agama")
                            .setSingleChoiceItems(singleChoiceItems, itemSelected, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.txt_hasil.setText(singleChoiceItems[which]);
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        }

        if(currentItem.getJudul().equals("Nama")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Nama");
                    LinearLayout layout = new LinearLayout(v.getContext());
                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    parms.setMargins(20,20,20,20);

                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setLayoutParams(parms);
                    layout.setGravity(Gravity.CLIP_VERTICAL);
                    layout.setPadding(10, 10, 10, 10);

                    EditText edittext = new EditText(v.getContext());
                    edittext.setText(holder.txt_hasil.getText());
                    edittext.setPadding(0, 40, 100, 40);
                    edittext.setSingleLine(true);
                    edittext.setGravity(Gravity.LEFT);
                    edittext.setTextSize(20);

                    LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv1Params.bottomMargin = 5;
                    tv1Params.leftMargin = 50;
                    tv1Params.rightMargin = 50;
                    layout.addView(edittext,tv1Params);


                    alertDialog.setView(layout);

                    alertDialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.txt_hasil.setText(edittext.getText().toString());
                        }
                    }).show();

                }
            });
        }


        if(currentItem.getJudul().equals("Handphone")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Handphone");
                    LinearLayout layout = new LinearLayout(v.getContext());
                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    parms.setMargins(20,20,20,20);

                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setLayoutParams(parms);
                    layout.setGravity(Gravity.CLIP_VERTICAL);
                    layout.setPadding(10, 10, 10, 10);

                    EditText edittext = new EditText(v.getContext());
                    edittext.setText(holder.txt_hasil.getText());
                    edittext.setPadding(0, 40, 100, 40);
                    edittext.setSingleLine(true);
                    edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edittext.setGravity(Gravity.LEFT);
                    edittext.setTextSize(20);

                    LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv1Params.bottomMargin = 5;
                    tv1Params.leftMargin = 50;
                    tv1Params.rightMargin = 50;
                    layout.addView(edittext,tv1Params);


                    alertDialog.setView(layout);

                    alertDialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            holder.txt_hasil.setText(edittext.getText().toString());
                        }
                    }).show();

                }
            });
        }

        if(currentItem.getJudul().equals("Alamat")){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Alamat");
                    LinearLayout layout = new LinearLayout(v.getContext());
                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    parms.setMargins(20,20,20,20);

                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setLayoutParams(parms);
                    layout.setGravity(Gravity.CLIP_VERTICAL);
                    layout.setPadding(10, 10, 10, 10);

                    EditText edittext = new EditText(v.getContext());
                    edittext.setText(currentItem.getHasil());
                    edittext.setPadding(0, 40, 100, 40);
                    edittext.setGravity(Gravity.LEFT);
                    edittext.setTextSize(20);

                    LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv1Params.bottomMargin = 5;
                    tv1Params.leftMargin = 50;
                    tv1Params.rightMargin = 50;
                    layout.addView(edittext,tv1Params);

                    edittext.setText(holder.txt_hasil.getText().toString());

                    alertDialog.setView(layout);

                    alertDialog.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                holder.txt_hasil.setText(edittext.getText().toString());
                        }
                    }).show();

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return profileModels.size();
    }
}
