package com.eswaraj.app.eswaraj.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesHelper {

    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;
    String defaultFile;

    public SharedPreferencesHelper() {
        this.context = null;
    }

    public SharedPreferencesHelper(Context context) {
        this.context = context;
        this.defaultFile = "eSwarajPrefs";
    }

    public Map<String, ?> getAll(String fileName){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getAll();
    }

    public Boolean getBoolean(String fileName, String key, Boolean defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getBoolean(key, defaultValue);
    }

    public float getFloat(String fileName, String key, float defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getFloat(key, defaultValue);
    }

    public int getInt(String fileName, String key, int defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getInt(key, defaultValue);
    }

    public long getLong(String fileName, String key, long defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getLong(key, defaultValue);
    }

    public String getString(String fileName, String key, String defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(String fileName, String key, Set<String> defaultValue){
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.getStringSet(key, defaultValue);
    }

    public Boolean contains(String fileName, String key) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        return prefs.contains(key);
    }

    public void registerOnChangeListener(String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(String fileName, String key, Boolean value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(String fileName, String key, float value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putInt(String fileName, String key, int value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(String fileName, String key, long value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putString(String fileName, String key, String value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(String fileName, String key, Set<String> value) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void remove(String fileName, String key) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(String fileName) {
        prefs = ((Activity) this.context).getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
