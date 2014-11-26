package com.eswaraj.app.eswaraj.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesHelper {

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    String defaultFile;

    public SharedPreferencesHelper() {
        this.defaultFile = "eSwarajPrefs";
    }

    public Map<String, ?> getAll(Context context, String fileName){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getAll();
    }

    public Boolean getBoolean(Context context, String fileName, String key, Boolean defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getBoolean(key, defaultValue);
    }

    public float getFloat(Context context, String fileName, String key, float defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getFloat(key, defaultValue);
    }

    public int getInt(Context context, String fileName, String key, int defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getInt(key, defaultValue);
    }

    public long getLong(Context context, String fileName, String key, long defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getLong(key, defaultValue);
    }

    public String getString(Context context, String fileName, String key, String defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(Context context, String fileName, String key, Set<String> defaultValue){
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.getStringSet(key, defaultValue);
    }

    public Boolean contains(Context context, String fileName, String key) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        return prefs.contains(key);
    }

    public void registerOnChangeListener(Context context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(Context context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(Context context, String fileName, String key, Boolean value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(Context context, String fileName, String key, float value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putInt(Context context, String fileName, String key, int value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(Context context, String fileName, String key, long value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putString(Context context, String fileName, String key, String value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(Context context, String fileName, String key, Set<String> value) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void remove(Context context, String fileName, String key) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(Context context, String fileName) {
        prefs = ((Activity) context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
