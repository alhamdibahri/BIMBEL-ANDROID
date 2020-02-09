package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.Siswa.AkunResponse;
import com.example.bimbel.response.TokenManager;
import com.example.bimbel.response.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsAkun extends AppCompatActivity {

    private static final String TAG = "SettingsAkun";
    
    @BindView(R.id.sUsername)
    EditText sUsername;
    @BindView(R.id.sEmail)
    EditText sEmail;
    @BindView(R.id.sPasswordBaru)
    EditText sPasswordBaru;


    @BindView(R.id.materialUsernameSetting)
    TextInputLayout materialUsernameSetting;
    @BindView(R.id.materialPasswordSetting)
    TextInputLayout materialPasswordSetting;
    @BindView(R.id.materialEmailSetting)
    TextInputLayout materialEmailSetting;

    ApiService apiService;
    TokenManager tokenManager;
    Call<AkunResponse> call;
    Call<ResponseBody> call1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_akun);
        getSupportActionBar().setTitle("Setting Akun");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        apiService = RetrofitClient.createServiceWithAuth(ApiService.class, tokenManager);

        ambildata();
    }

    void ambildata(){
        call = apiService.setting();
        call.enqueue(new Callback<AkunResponse>() {
            @Override
            public void onResponse(Call<AkunResponse> call, Response<AkunResponse> response) {
                Log.w(TAG, "Onresponse : " + response);
                if(response.isSuccessful()){
                    sUsername.setText(response.body().getData().get(0).getUsername());
                    sEmail.setText(response.body().getData().get(0).getEmail());
                }
                else{
                    startActivity(new Intent(SettingsAkun.this, ActivityFormLoginRegister.class));
                    finish();
                    tokenManager.deleteToken();
                }
            }

            @Override
            public void onFailure(Call<AkunResponse> call, Throwable t) {
                new SweetAlertDialog(SettingsAkun.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    @OnTextChanged(R.id.sPasswordBaru)
    void textChangePassword(){
        materialPasswordSetting.setError(null);
    }

    @OnClick(R.id.btnUbahSetting)
    void ubahData(){
        String username = sUsername.getText().toString();
        String email = sEmail.getText().toString();
        String password = sPasswordBaru.getText().toString();

        materialUsernameSetting.setError(null);
        materialEmailSetting.setError(null);
        materialPasswordSetting.setError(null);

        call1 = apiService.ubahakun(username,email,password);

        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
               if(response.isSuccessful()){
                   sPasswordBaru.setText("");
                   new SweetAlertDialog(SettingsAkun.this, SweetAlertDialog.SUCCESS_TYPE)
                           .setTitleText("Data berhasil di ubah")
                           .show();
               }
               else{
                   if(response.code() == 422){
                       handleError(response.errorBody());
                   }
                   if(response.code() == 401){
                       startActivity(new Intent(SettingsAkun.this, ActivityFormLoginRegister.class));
                       finish();
                       tokenManager.deleteToken();
                   }
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                new SweetAlertDialog(SettingsAkun.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });
    }

    private void handleError(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);
        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("username")){
                materialUsernameSetting.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("email")){
                materialEmailSetting.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                materialPasswordSetting.setError(error.getValue().get(0));
            }
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }
}
