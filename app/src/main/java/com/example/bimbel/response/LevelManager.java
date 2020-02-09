package com.example.bimbel.response;

import android.content.SharedPreferences;

import com.example.bimbel.response.Siswa.Akun;

public class LevelManager {

    private SharedPreferences level;
    private SharedPreferences.Editor editor;

    private static LevelManager INSTANCE = null;

    private LevelManager(SharedPreferences level){
        this.level = level;
        this.editor = level.edit();
    }

    public static synchronized LevelManager getInstance(SharedPreferences level){
        if(INSTANCE == null){
            INSTANCE = new LevelManager(level);
        }
        return INSTANCE;
    }

    public void saveLevel(Akun token){
        editor.putString("LEVEL", token.getLevel());
        editor.commit();
    }

    public void deleteLevel(){
        editor.remove("LEVEL");
        editor.commit();
    }

    public Akun ambilLevel(){
        Akun level1 = new Akun();
        level1.setLevel(level.getString("LEVEL", null));
        return level1;
    }

}
