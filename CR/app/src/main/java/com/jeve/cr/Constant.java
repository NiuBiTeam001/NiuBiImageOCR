package com.jeve.cr;

import android.os.Environment;

import java.io.File;

/**
 * note：保存应用一些常用的常量
 * Created by zhangliang on 2017/12/13.
 */

public class Constant {
    public static String PACKAGE_NAME = "com.jeve.cr";

    //原图本地保存地址
    public static final String ORIGINAL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File
            .separator + CrApplication.getContext().getString(R.string.app_name) + File.separator;
}
