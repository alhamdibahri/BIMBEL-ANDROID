package com.example.bimbel;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bimbel.adapter.ProfileAdapter;
import com.example.bimbel.model.ProfileModel;
import com.example.bimbel.model.ProfileResponse;
import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.TokenManager;
import com.example.bimbel.response.Utils;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap;
    Uri Path;

    TokenManager tokenManager;
    ApiService service;
    Call<ProfileResponse> call;
    Call<ResponseBody> call1;

    @BindView(R.id.circleRectImage5)
    ImageView imageView;
    @BindView(R.id.btnUpdateProfile)
    ImageButton btnUpdateProfile;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<ProfileModel> profileModels;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        tokenManager = TokenManager.getInstance(getActivity().getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        recyclerView = view.findViewById(R.id.recyleView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                tampil();
            }
        });

        tampil();

        try {
            String nama = getArguments().getString("nama");
            tampildata(nama, "Atur Sekarang", "Atur Sekarang", "Atur Sekarang", "Atur Sekarang", "Atur Sekarang");
        }catch (Exception ex){
            ex.printStackTrace();
        }

        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    TextView textnama = recyclerView.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.tmp_prof_hasil);
                    TextView texttgl = recyclerView.findViewHolderForAdapterPosition(1).itemView.findViewById(R.id.tmp_prof_hasil);
                    TextView textjk = recyclerView.findViewHolderForAdapterPosition(2).itemView.findViewById(R.id.tmp_prof_hasil);
                    TextView textagama = recyclerView.findViewHolderForAdapterPosition(3).itemView.findViewById(R.id.tmp_prof_hasil);
                    TextView textalamat = recyclerView.findViewHolderForAdapterPosition(4).itemView.findViewById(R.id.tmp_prof_hasil);
                    TextView texthp = recyclerView.findViewHolderForAdapterPosition(5).itemView.findViewById(R.id.tmp_prof_hasil);

                    File file;
                    RequestBody nama = RequestBody.create(MediaType.parse("multipart/form-data"), textnama.getText().toString());
                    RequestBody tgl_lahir = RequestBody.create(MediaType.parse("multipart/form-data"), texttgl.getText().toString());
                    RequestBody jk = RequestBody.create(MediaType.parse("multipart/form-data"), textjk.getText().toString());
                    RequestBody agama = RequestBody.create(MediaType.parse("multipart/form-data"), textagama.getText().toString());
                    RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"), textalamat.getText().toString());
                    RequestBody hp = RequestBody.create(MediaType.parse("multipart/form-data"), texthp.getText().toString());
                    if (bitmap == null){
                        file = null;
                        call1 = service.ubahProfile(tgl_lahir, jk, agama,alamat,hp,null,nama);
                    }
                    else {
                        file = createTempFile(bitmap);
                        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("foto_siswa", file.getName(), reqFile);
                        call1 = service.ubahProfile(tgl_lahir, jk, agama,alamat,hp,body,nama);
                    }

                    call1.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(getActivity(), "Profile di ubah" , Toast.LENGTH_LONG).show();
                            }
                            else {
                                if(response.code() == 422){
                                    handleError(response.errorBody());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });

        return view;
    }

    void tampil(){
        call = service.profile_siswa();
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {
                        tampildata(
                                response.body().getData().get(0).getNama(),
                                response.body().getData().get(0).getTanggal_lahir_siswa(),
                                response.body().getData().get(0).getJenkel_siswa(),
                                response.body().getData().get(0).getAgama_siswa(),
                                response.body().getData().get(0).getAlamat_siswa(),
                                response.body().getData().get(0).getNo_handphone_siswa()
                        );
                        recyclerView.removeAllViewsInLayout();
                        if(response.body().getData().get(0).getFoto_siswa() != null && response.body().getData().get(0).getFoto_siswa().length() > 0){
                            Picasso.get().load("http://192.168.43.206/bimbel/public/fotosiswa/" + response.body().getData().get(0).getFoto_siswa()).placeholder(R.drawable.placeholder).into(imageView);
                        } else{
                            Picasso.get().load(R.drawable.placeholder).into(imageView);
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }


    private void tampildata(String nama, String tgl_lahir, String jk, String agama, String alamat, String hp){
        try {
            profileModels = new ArrayList<>();
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp,"Nama", nama));
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp, "Tanggal Lahir", tgl_lahir));
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp,"Jenis Kelamin", jk));
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp,"Agama", agama));
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp,"Alamat", alamat));
            profileModels.add(new ProfileModel(R.drawable.ic_chevron_right_black_24dp, "Handphone", hp));
            profileModels.add(new ProfileModel(R.drawable.logout,"Logout", ""));
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getActivity());
            mAdapter = new ProfileAdapter(profileModels);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mAdapter);
        }catch (Exception e){
           Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Path = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Path);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private File createTempFile(Bitmap bitmap) {
        File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , System.currentTimeMillis() +"_image.webp");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.WEBP,0, bos);
        byte[] bitmapdata = bos.toByteArray();
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void handleError(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("nama")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("tanggal_lahir_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("jenkel_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("agama_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("alamat_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("foto_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("no_handphone_siswa")){
                Toast.makeText(getActivity(),error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
        }
    }

}
