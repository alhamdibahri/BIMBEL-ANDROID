package com.example.bimbel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.History.HistoryGuru;
import com.example.bimbel.response.TokenManager;
import com.example.bimbel.response.Utils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriGuru extends AppCompatActivity {

    private static final String TAG = "MateriGuru";
    private static final int FILE_REQUEST = 1;
    Call<List<HistoryGuru>> call;
    Call<ResponseBody> callupload;
    TokenManager tokenManager;
    ApiService service;
    Uri uri;
    String uriString;
    private static final int BUFFER_SIZE = 1024 * 2;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_gallery";

    @BindView(R.id.w)
    TextView w;
    @BindView(R.id.namasiswa)
    EditText namasiswa;
    @BindView(R.id.namamateri)
    EditText namamateri;
    @BindView(R.id.upload_materi)
    Button upload_materi;
    @BindView(R.id.btnupload)
    Button btnupload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materi_guru);
        getSupportActionBar().setTitle("Materi Guru");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        service = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);
        ButterKnife.bind(this);
        tampildata();
    }

    void tampildata(){
        call = service.materiguru();
        call.enqueue(new Callback<List<HistoryGuru>>() {
            @Override
            public void onResponse(Call<List<HistoryGuru>> call, Response<List<HistoryGuru>> response) {
                if(response.isSuccessful()){
                    if(response.body().size() == 0){
                        new SweetAlertDialog(MateriGuru.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Anda masih belum mempunyai siswa")
                                .show();
                        btnupload.setEnabled(false);
                    }
                    else{
                        w.setText(response.body().get(0).getKode_siswa());
                        namasiswa.setText(response.body().get(0).getNama());
                    }
                }
                else{
                    if(response.code() == 401){
                        startActivity(new Intent(MateriGuru.this, ActivityFormLoginRegister.class));
                        finish();
                        tokenManager.deleteToken();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HistoryGuru>> call, Throwable t) {
                new SweetAlertDialog(MateriGuru.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    @OnClick(R.id.upload_materi)
    void upload(){
        openFile();
    }

    @OnClick(R.id.btnupload)
    void uploadmateri(){
        String path = getFilePathFromURI(MateriGuru.this,uri);
        UploadPDF(path);
    }

    private void openFile(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent,FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            uri = data.getData();
            uriString = uri.toString();
            File myFile = new File(uriString);
            upload_materi.setText(getFileName(uri));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private  void UploadPDF(String path){
        try {
            File file = new File(path);
            RequestBody nama_materi = RequestBody.create(MediaType.parse("multipart/form-data"), namamateri.getText().toString());
            RequestBody kode_siswa = RequestBody.create(MediaType.parse("multipart/form-data"), w.getText().toString());
            RequestBody reqFile = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part file1 = MultipartBody.Part.createFormData("file_materi", file.getName(), reqFile);
            callupload = service.uploadmateri(nama_materi, file1, kode_siswa);
            callupload.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        new SweetAlertDialog(MateriGuru.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Materi berhasil di upload")
                                .show();
                        namamateri.setText("");
                        uri = null;
                        upload_materi.setText("Choose File...");
                    }
                    else{
                        if(response.code() == 422){
                            handleError(response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.w(TAG, "onfailure" + t.getMessage());
                }
            });
        }catch (Exception ex) {
            Toast.makeText(MateriGuru.this, "Harap upload materi", Toast.LENGTH_LONG).show();
        }
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        String fileName = getFileName(contentUri);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(wallpaperDirectory + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleError(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("nama_materi")){
                Toast.makeText(MateriGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }
            if(error.getKey().equals("file_materi")){
                Toast.makeText(MateriGuru.this,error.getValue().get(0), Toast.LENGTH_LONG).show();
            }

        }
    }
}
