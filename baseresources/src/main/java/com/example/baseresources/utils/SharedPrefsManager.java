package com.example.baseresources.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.baseresources.callbacks.TearDownManager;

public final class SharedPrefsManager implements TearDownManager {

    private static final String API_KEY = "API_KEY";
    private static final String SEC_KEY = "SEC_KEY";
    private static final String TOGGLE_STATE = "TOGGLE_STATE";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences("com.example.baseresources.utils_Shared_Prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void storeKeys(String apiKey, String secKey, boolean toogleState) {
        editor.putString(API_KEY, apiKey);
        editor.putString(SEC_KEY, secKey);
        editor.putBoolean(TOGGLE_STATE, toogleState);
        editor.apply();
    }

    public void clearPrefs() {
        editor.clear().apply();
    }

    public boolean isPrefsEmpty() {
        return !(sharedPreferences.contains(API_KEY) && sharedPreferences.contains(SEC_KEY));
    }

    public boolean getToggleState() {
        return sharedPreferences.getBoolean(TOGGLE_STATE, false);
    }

    public String getSign1() {
        return sharedPreferences.getString(API_KEY, "Error");
    }

    public String getSign2() {
        return sharedPreferences.getString(SEC_KEY, "Error");
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    @Override
    public void tearDown() {
        sharedPreferences = null;
    }
}
