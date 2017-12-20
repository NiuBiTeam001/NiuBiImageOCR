package com.jeve.cr.tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.jeve.cr.CrApplication;

/**
 * 设备工具
 * lijiawei
 * 2017-12-13
 */
public class DeviceTool {

    /**
     * 获取屏幕高宽
     */
    public static WH getWidthAndHeight(Activity activity) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        return new WH(width, height);
    }

    /**
     * 获取屏幕高宽
     */
    public static WH getWidthAndHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return new WH(width, height);
    }

    public static class WH {
        public WH(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int width;
        public int height;
    }

    /**
     * SD卡判断
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 是否有网
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 返回版本名字
     * 对应build.gradle中的versionName
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 返回版本号
     * 对应build.gradle中的versionCode
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 设备信息
     */
    public static String getDeviceInfo(){
        return android.os.Build.BRAND + " " + android.os.Build.MODEL + " " +"安卓版本:" + android.os.Build.VERSION.RELEASE;
    }

    public static int dip2px(float dp) {
        Resources res = CrApplication.getContext().getResources();
        float density = res.getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static Point getScreenSize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);

        Configuration config = context.getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏
            return new Point(Math.max(p.x, p.y), Math.min(p.x, p.y));
        } else /*if (config.orientation == Configuration.ORIENTATION_PORTRAIT)*/ {
            // 竖屏
            return new Point(Math.min(p.x, p.y), Math.max(p.x, p.y));
        }
    }
}
