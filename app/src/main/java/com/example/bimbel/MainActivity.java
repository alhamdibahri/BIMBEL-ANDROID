package com.example.bimbel;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.bimbel.response.LevelManager;
import com.example.bimbel.response.TokenManager;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MainActivity extends AppIntro {

    TokenManager tokenManager;
    LevelManager levelManager;
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        tokenManager = TokenManager.getInstance(getSharedPreferences("pref", MODE_PRIVATE));
        levelManager = LevelManager.getInstance(getSharedPreferences("level", MODE_PRIVATE));
        if(tokenManager.getToken().getAccessToken() != null){
            if(levelManager.ambilLevel().getLevel().equals("Guru")){
                startActivity(new Intent(MainActivity.this, HalamanUtamaGuru.class));
            }
            else{
                startActivity(new Intent(MainActivity.this, HalamanUtamaSiswa.class));
            }
            finish();
        }

        addSlide(AppIntroFragment.newInstance("",
                "Aplikasi ini memudahkan anda untuk siswa mencari guru private dan untuk guru mendapatkan siswa",
                R.drawable.abjay, getResources().getColor(R.color.cyan)));
        addSlide(AppIntroFragment.newInstance("Berita Pendidikan",
                "Aplikasi ini menyediakan berita pendidikan yang update setiap harinya",
                R.drawable.img2, getResources().getColor(R.color.cyan)));
        addSlide(AppIntroFragment.newInstance("Rekomendasi Belajar",
                "Rekomendasi Belajar yang langsung diberikan oleh guru di dalam aplikasi",
                R.drawable.img1, getResources().getColor(R.color.cyan)));
        addSlide(AppIntroFragment.newInstance("Tanya Jawab",
                "Aplikasi ini disediakan live chat untuk memudahkan guru dan siswa dalam melakukan obrolan",
                R.drawable.img3, getResources().getColor(R.color.cyan)));
        setBarColor(getResources().getColor(R.color.cyan));
        setSeparatorColor(getResources().getColor(R.color.white));
        showSkipButton(true);

        fa = this;
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        startActivity(new Intent(MainActivity.this, ActivityFormLoginRegister.class));
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(MainActivity.this, ActivityFormLoginRegister.class));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
