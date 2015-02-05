package com.next.eswaraj.helpers;

import android.app.Activity;
import android.app.Service;
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

    public Map<String, ?> getAll(Activity context, String fileName){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getAll();
    }

    public Boolean getBoolean(Activity context, String fileName, String key, Boolean defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getBoolean(key, defaultValue);
    }

    public float getFloat(Activity context, String fileName, String key, float defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getFloat(key, defaultValue);
    }

    public int getInt(Activity context, String fileName, String key, int defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getInt(key, defaultValue);
    }

    public long getLong(Activity context, String fileName, String key, long defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getLong(key, defaultValue);
    }

    public String getString(Activity context, String fileName, String key, String defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(Activity context, String fileName, String key, Set<String> defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getStringSet(key, defaultValue);
    }

    public Boolean contains(Activity context, String fileName, String key) {
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.contains(key);
    }

    public void registerOnChangeListener(Activity context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = context.getSharedPreferences(fileName, 0);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(Activity context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = context.getSharedPreferences(fileName, 0);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(Activity context, String fileName, String key, Boolean value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(Activity context, String fileName, String key, float value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putInt(Activity context, String fileName, String key, int value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(Activity context, String fileName, String key, long value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putString(Activity context, String fileName, String key, String value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(Activity context, String fileName, String key, Set<String> value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void remove(Activity context, String fileName, String key) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(Activity context, String fileName) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public Map<String, ?> getAll(Service context, String fileName){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getAll();
    }

    public Boolean getBoolean(Service context, String fileName, String key, Boolean defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getBoolean(key, defaultValue);
    }

    public float getFloat(Service context, String fileName, String key, float defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getFloat(key, defaultValue);
    }

    public int getInt(Service context, String fileName, String key, int defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getInt(key, defaultValue);
    }

    public long getLong(Service context, String fileName, String key, long defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getLong(key, defaultValue);
    }

    public String getString(Service context, String fileName, String key, String defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(Service context, String fileName, String key, Set<String> defaultValue){
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.getStringSet(key, defaultValue);
    }

    public Boolean contains(Service context, String fileName, String key) {
        prefs = context.getSharedPreferences(fileName, 0);
        return prefs.contains(key);
    }

    public void registerOnChangeListener(Service context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = context.getSharedPreferences(fileName, 0);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(Service context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = context.getSharedPreferences(fileName, 0);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(Service context, String fileName, String key, Boolean value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(Service context, String fileName, String key, float value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putInt(Service context, String fileName, String key, int value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(Service context, String fileName, String key, long value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putString(Service context, String fileName, String key, String value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(Service context, String fileName, String key, Set<String> value) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void remove(Service context, String fileName, String key) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(Service context, String fileName) {
        prefs = context.getSharedPreferences(fileName, 0);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    public Map<String, ?> getAll(Context context, String fileName){
        prefs = getPrefs(context, fileName);
        return prefs.getAll();
    }

    public Boolean getBoolean(Context context, String fileName, String key, Boolean defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getBoolean(key, defaultValue);
    }

    public float getFloat(Context context, String fileName, String key, float defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getFloat(key, defaultValue);
    }

    public int getInt(Context context, String fileName, String key, int defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getInt(key, defaultValue);
    }

    public long getLong(Context context, String fileName, String key, long defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getLong(key, defaultValue);
    }

    public String getString(Context context, String fileName, String key, String defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getString(key, defaultValue);
    }

    public Set<String> getStringSet(Context context, String fileName, String key, Set<String> defaultValue){
        prefs = getPrefs(context, fileName);
        return prefs.getStringSet(key, defaultValue);
    }

    public Boolean contains(Context context, String fileName, String key) {
        prefs = getPrefs(context, fileName);
        return prefs.contains(key);
    }

    public void registerOnChangeListener(Context context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = getPrefs(context, fileName);
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnChangeListener(Context context, String fileName, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs = getPrefs(context, fileName);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void putBoolean(Context context, String fileName, String key, Boolean value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putFloat(Context context, String fileName, String key, float value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putInt(Context context, String fileName, String key, int value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(Context context, String fileName, String key, long value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putString(Context context, String fileName, String key, String value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(Context context, String fileName, String key, Set<String> value) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    public void remove(Context context, String fileName, String key) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clear(Context context, String fileName) {
        prefs = getPrefs(context, fileName);
        editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    private SharedPreferences getPrefs(Context context, String fileName) {
        if(context instanceof Activity) {
            return ((Activity) context).getSharedPreferences(fileName, 0);
        }
        else {
            return ((Service) context).getSharedPreferences(fileName, 0);
        }
    }
}
