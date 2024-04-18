package com.baolian.network.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedPreferencesManager {
    private String PREFERENCES_NAME = "my_preferences";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPreferencesManager(Context context, @Mode String PREFERENCES_NAME) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void savaBool(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBool(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public int getString(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.remove(key);
        editor1.apply();
    }

    // 添加其他类型的数据保存和读取方法，例如 saveInt(), getInt() 等
    public void clearAllData() {
        editor.clear();
        editor.apply();
    }

    public void saveStringList(String key, List<String> stringList) {
        Set<String> stringSet = new HashSet<>(stringList);
        editor.putStringSet(key, stringSet);
        editor.apply();
    }

    public List<String> getStringList(String key) {
        Set<String> stringSet = sharedPreferences.getStringSet(key, new HashSet<>());
        return new ArrayList<>(stringSet);
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    @StringDef({proxy, white_list, api, user, economize, mixed,set})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final String proxy = "proxy";
    public static final String white_list = "white_list";

    public static final String api = "api";
    public static final String user = "user";
    public static final String economize = "economize";
    public static final String mixed = "mixed";
    public static final String set = "set";

}
