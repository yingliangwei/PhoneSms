package com.xframe.widget.entity;

import androidx.annotation.IdRes;

public class RecyclerEntity {
    public String name;
    @IdRes
    public int drawable;
    public String src_url;
    public String text;
    public String key;
    public boolean isArray = false;
    @IdRes
    public int icon;
    public boolean isSwitch;
    public boolean SwitchBox;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public RecyclerEntity(String name, int drawable, String text, String src_url) {
        this.name = name;
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
    }

    public RecyclerEntity(String name, int drawable, String text, String src_url, String key) {
        this.name = name;
        setKey(key);
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
    }

    public RecyclerEntity(int icon, String name, int drawable, String text, String src_url, String key) {
        this.name = name;
        this.icon = icon;
        setKey(key);
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
    }

    public RecyclerEntity(int icon, String name, int drawable, String text, String src_url, String key, boolean isArray) {
        this.name = name;
        this.icon = icon;
        setKey(key);
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
        this.isArray = isArray;
    }

    public RecyclerEntity(int icon, String name, String text, String src_url, String key, boolean isArray) {
        this.name = name;
        this.icon = icon;
        setKey(key);
        this.text = text;
        this.src_url = src_url;
        this.isArray = isArray;
    }

    public RecyclerEntity(int icon, String name, String key) {
        this.name = name;
        this.icon = icon;
        setKey(key);
    }

    public RecyclerEntity(int icon, String name, String key, boolean isSwitch, boolean SwitchBox) {
        this.name = name;
        this.icon = icon;
        this.isSwitch = isSwitch;
        this.SwitchBox = SwitchBox;
        setKey(key);
    }

    public RecyclerEntity(int icon, String name, String text, String key) {
        this.name = name;
        this.icon = icon;
        this.text = text;
        setKey(key);
    }

    public RecyclerEntity(int icon, String name, String text, String key, boolean isArray) {
        this.name = name;
        this.icon = icon;
        this.text = text;
        this.isArray = isArray;
        setKey(key);
    }

    public RecyclerEntity(String name, int drawable, String text, String src_url, String key, boolean isArray) {
        this.name = name;
        setKey(key);
        this.drawable = drawable;
        this.text = text;
        this.src_url = src_url;
        this.isArray = isArray;
    }
}
