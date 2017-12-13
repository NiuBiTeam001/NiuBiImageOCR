package com.jeve.cr;

import android.app.Application;
import android.content.Context;

import com.jeve.cr.tool.UMTool;

import cn.bmob.v3.Bmob;

/**
 * Application
 * lijiawei
 * 2017-12-6
 */
public class CrApplication extends Application {

    private static Context context;
    private static final String APPLICATION_ID = "57e5fc30ae14024bb7f93f303f142e25";
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //Bmob初始化
        Bmob.initialize(this,APPLICATION_ID);
    }

    public static Context getContext() {
        return context;
    }
}
