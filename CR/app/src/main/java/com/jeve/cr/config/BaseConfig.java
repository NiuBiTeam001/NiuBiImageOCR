package com.jeve.cr.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jeve.cr.CrApplication;


/**
 * <pre>
 * Note: Config基类
 * created by zhangliang 2017/12/13
 */
public abstract class BaseConfig {

    protected SharedPreferences config = null;
    protected Context mContext;

    public BaseConfig(String name) {
        mContext = CrApplication.getContext();
        config = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    protected boolean getBoolean(String key) {
        return config.getBoolean(key, false);
    }

    protected boolean getBoolean(String key, boolean defValue) {
        return config.getBoolean(key, defValue);
    }

    protected String getString(String key) {
        return config.getString(key, "");
    }

    protected String getString(String key, String defValue) {
        return config.getString(key, defValue);
    }

    protected long getLong(String key) {
        return config.getLong(key, -1);
    }

    protected long getLong(String key, long defValue) {
        return config.getLong(key, defValue);
    }

    protected int getInt(String key) {
        return config.getInt(key, 0);
    }

    protected int getInt(String key, int defValue) {
        return config.getInt(key, defValue);
    }

    protected float getFloat(String key) {
        return config.getFloat(key, 0.0F);
    }

    protected float getFloat(String key, float defValue) {
        return config.getFloat(key, defValue);
    }

    protected void setBoolean(String key, boolean value) {
        config.edit().putBoolean(key, value).apply();
    }

    protected void setString(String key, String value) {
        config.edit().putString(key, value).apply();
    }

    protected void setLong(String key, long value) {
        config.edit().putLong(key, value).apply();
    }

    protected void setInt(String key, int value) {
        config.edit().putInt(key, value).apply();
    }

    protected void setFloat(String key, float value) {
        config.edit().putFloat(key, value).apply();
    }

    public void clear() {
        config.edit().clear().apply();
    }

    public int size() {
        return config.getAll().size();
    }

    public Editor editor() {
        return config.edit();
    }
}

