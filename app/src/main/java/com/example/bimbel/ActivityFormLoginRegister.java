package com.example.bimbel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;

import com.example.bimbel.network.ApiError;
import com.example.bimbel.network.ApiService;
import com.example.bimbel.network.RetrofitClient;
import com.example.bimbel.response.AccessToken;
import com.example.bimbel.response.Siswa.Akun;
import com.example.bimbel.response.LevelManager;
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

public class ActivityFormLoginRegister extends AppCompatActivity {

    private TabHost tabHost;
    private static final String TAG = "ActivityFormLoginRegister";


    //formlogin
    @BindView(R.id.lUsername)
    EditText lUsername;
    @BindView(R.id.lPassword)
    EditText lPassword;

    @BindView(R.id.LmaterialEmail)
    TextInputLayout LmaterialEmail;
    @BindView(R.id.LmaterialPassword)
    TextInputLayout LmaterialPassowrd;

    //form register
    @BindView(R.id.nama)
    EditText name;
    @BindView(R.id.rUsername)
    EditText rUsername;
    @BindView(R.id.rEmail)
    EditText rEmail;
    @BindView(R.id.rPassword)
    EditText rPassowrd;

    @BindView(R.id.materialPass)
    TextInputLayout materialPass;
    @BindView(R.id.materialNama)
    TextInputLayout materialNama;
    @BindView(R.id.materialEmail)
    TextInputLayout materialEmail;
    @BindView(R.id.materialUsername)
    TextInputLayout materialUsername;
    @BindView(R.id.materialLevel)
    TextInputLayout materialLevel;


    ApiService service;
    Call<AccessToken> call;
    Call<Akun> call1;
    TokenManager tokenManager;

    LevelManager levelManager;

    Spinner spinner ;

    String[] SPINNER_DATA = {"--Daftar Sebagai--","Guru","Siswa"};


    String Level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login_register);
        getSupportActionBar().hide();

        tabHost = (TabHost)findViewById(R.id.tabHost);

        tabHost.setup();
        addTab(tabHost, "LOG IN", "LOG IN", R.id.tab1);
        addTab(tabHost, "SIGN UP", "SIGN UP", R.id.tab2);

        ButterKnife.bind(this);
        service = RetrofitClient.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        levelManager = LevelManager.getInstance(getSharedPreferences("level", MODE_PRIVATE));

        if(tokenManager.getToken().getAccessToken() != null){
            if(levelManager.ambilLevel().getLevel().equals("Guru")){
                startActivity(new Intent(ActivityFormLoginRegister.this, HalamanUtamaGuru.class));
            }
            else{
                startActivity(new Intent(ActivityFormLoginRegister.this, HalamanUtamaSiswa.class));
            }
            finish();
        }

        getSpinner();

    }

    private void getSpinner(){
        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, SPINNER_DATA);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spinner.getSelectedItem().toString().equals("Guru")){
                    Level = "Guru";
                }
                else if(spinner.getSelectedItem().toString().equals("Siswa")){
                    Level = "Siswa";
                }
                else{
                    Level = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btnLogin)
    void login(){
        String username = lUsername.getText().toString();
        String password = lPassword.getText().toString();


        LmaterialEmail.setError(null);
        LmaterialPassowrd.setError(null);

        call = service.login(username, password);

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()){
                    String username = lUsername.getText().toString();
                    call1 = service.logindata(username);
                    call1.enqueue(new Callback<Akun>() {
                        @Override
                        public void onResponse(Call<Akun> call, Response<Akun> response) {
                            if(response.isSuccessful()){
                                String level = response.body().getLevel();
                                levelManager.saveLevel(response.body());
                                if(level.equals("Siswa")){
                                    Intent i = new Intent(ActivityFormLoginRegister.this, HalamanUtamaSiswa.class);
                                    startActivity(i);
                                    MainActivity.fa.finish();
                                    finish();
                                }
                                else{
                                    Intent i = new Intent(ActivityFormLoginRegister.this, HalamanUtamaGuru.class);
                                    startActivity(i);
                                    MainActivity.fa.finish();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Akun> call, Throwable t) {
                            new SweetAlertDialog(ActivityFormLoginRegister.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(t.getMessage())
                                    .show();
                        }
                    });
                    tokenManager.saveToken(response.body());
                }else{
                    if(response.code() == 422){
                        handleErrorLogin(response.errorBody());
                    }
                    if(response.code() == 400){
                        new SweetAlertDialog(ActivityFormLoginRegister.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("username/email dan password anda salah")
                                .show();
                    }

                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                new SweetAlertDialog(ActivityFormLoginRegister.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });

    }

    @OnClick (R.id.btnSignUp)
    void register(){
        String nama = name.getText().toString();
        String username = rUsername.getText().toString();
        String email = rEmail.getText().toString();
        String password = rPassowrd.getText().toString();

        materialNama.setError(null);
        materialPass.setError(null);
        materialUsername.setError(null);
        materialEmail.setError(null);
        materialLevel.setError(null);

        call = service.register(username, email, password, nama, Level);

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if(response.isSuccessful()){
                    tokenManager.saveToken(response.body());
                    bersihkan();
                    new SweetAlertDialog(ActivityFormLoginRegister.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Registrasi berhasil")
                            .show();
                }
                else{
                    handleErrorRegister(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                new SweetAlertDialog(ActivityFormLoginRegister.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(t.getMessage())
                        .show();
            }
        });

    }
    private void addTab(TabHost tabHost, String nama, String Indicator, int content){
        TabHost.TabSpec spec;
        spec =tabHost.newTabSpec(nama);
        spec.setContent(content);
        spec.setIndicator(Indicator);
        tabHost.addTab(spec);
    }

    @OnTextChanged(R.id.nama)
    void changeNama(){
        materialNama.setError(null);
    }

    @OnTextChanged(R.id.rUsername)
    void changeUsername(){
        materialUsername.setError(null);
    }

    @OnTextChanged(R.id.rEmail)
    void changeEmail(){
        materialEmail.setError(null);
    }

    @OnTextChanged(R.id.rPassword)
    void changePassword(){
        materialPass.setError(null);
    }

    @OnTextChanged(R.id.lUsername)
    void changeLUsername(){
        LmaterialEmail.setError(null);
    }

    @OnTextChanged(R.id.lPassword)
    void changeLPassword(){
        LmaterialPassowrd.setError(null);
    }

    private void bersihkan(){
        materialNama.setError(null);
        materialPass.setError(null);
        materialUsername.setError(null);
        materialEmail.setError(null);
        materialLevel.setError(null);
        name.setText("");
        rUsername.setText("");
        rEmail.setText("");
        rPassowrd.setText("");
    }

    private void handleErrorRegister(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("nama")){
                materialNama.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("username")){
                materialUsername.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("email")){
                materialEmail.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("password")){
                materialPass.setError(error.getValue().get(0));
            }

            if(error.getKey().equals("level")){
                materialLevel.setError(error.getValue().get(0));
            }
        }
    }

    private void handleErrorLogin(ResponseBody response){
        ApiError apiError = Utils.converErrors(response);

        for(Map.Entry<String, List<String>> error : apiError.getErrors().entrySet()){
            if(error.getKey().equals("username")){
                LmaterialEmail.setError(error.getValue().get(0));
            }
            if(error.getKey().equals("password")){
                LmaterialPassowrd.setError(error.getValue().get(0));
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }


}
