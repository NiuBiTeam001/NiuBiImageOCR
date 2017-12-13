package com.jeve.cr.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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

}
