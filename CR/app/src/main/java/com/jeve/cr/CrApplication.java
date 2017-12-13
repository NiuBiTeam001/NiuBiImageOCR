package com.jeve.cr;

import android.app.Application;
import android.content.Context;

import com.jeve.cr.tool.UMTool;

/**
 * Application
 * lijiawei
 * 2017-12-6
 */
public class CrApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
