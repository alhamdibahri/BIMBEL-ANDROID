package com.example.bimbel.response;

import android.content.SharedPreferences;

public class TokenManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static TokenManager INSTANCE = null;

    private TokenManager(SharedPreferences pref){
        this.pref = pref;
        this.editor = pref.edit();
    }

    public static synchronized TokenManager getInstance(SharedPreferences pref){
        if(INSTANCE == null){
            INSTANCE = new TokenManager(pref);
        }
        return INSTANCE;
    }

    public void saveToken(AccessToken token){
        editor.putString("ACCESS_TOKEN", token.getAccessToken());
        editor.putString("REFRESH_TOKEN", token.getRefreshToken());
        editor.commit();
    }

    public void deleteToken(){
        editor.remove("ACCESS_TOKEN");
        editor.remove("REFRESH_TOKEN");
        editor.commit();
    }

    public AccessToken getToken(){
        AccessToken token = new AccessToken();
        token.setAccessToken(pref.getString("ACCESS_TOKEN", null));
        token.setRefreshToken(pref.getString("REFRESH_TOKEN", null));
        return token;
    }

}
