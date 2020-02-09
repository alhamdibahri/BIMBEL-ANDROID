package com.example.bimbel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.bimbel.response.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.ButterKnife;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HalamanUtamaSiswa extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_siswa);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));

        loadFragment(new HomeSiswaFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if(tokenManager.getToken().getAccessToken() == null){
            startActivity(new Intent(HalamanUtamaSiswa.this, MainActivity.class));
            finish();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.f1_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.home_menu:
                fragment = new HomeSiswaFragment();
                break;
            case R.id.chat_menu:
                try{
                    fragment = new ChatFragment();
                    TextView txt = (TextView)findViewById(R.id.txttampil);
                    Bundle bundle = new Bundle();
                    bundle.putString("nama", txt.getText().toString().substring(15));
                    fragment.setArguments(bundle);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
            case R.id.profile_menu:
                fragment = new ProfileFragment();
                try {
                    TextView txt = (TextView)findViewById(R.id.txttampil);
                    Bundle bundle = new Bundle();
                    bundle.putString("nama", txt.getText().toString().substring(15));
                    fragment.setArguments(bundle);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
        }
        return loadFragment(fragment);
    }

    public String getNama() {
        return getIntent().getStringExtra("nama");
    }
}
