package com.jeve.cr.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.UMTool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

public class MainActivity extends BaseActivity {
    TextView textView;
    private String string11= "11111";
    private String string22 = "2222";
    private String string33 = "3333";
    private String wocao = "xingbuxing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UMTool.getInstence().openDebug();
        textView = (TextView) findViewById(R.id.tv);
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            OCRTool.getInstence().OCRTest(Environment.getExternalStorageDirectory().getAbsolutePath() + "/img001.png",
                    new OCRTool.OcrCallBack() {
                        @Override
                        public void success(String str) {
                            textView.setText(str);
                        }

                        @Override
                        public void error(String error) {

                        }
                    });
        } else {
            requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void requestSuccess(String permission) {
        super.requestSuccess(permission);
        OCRTool.getInstence().OCRTest(Environment.getExternalStorageDirectory().getAbsolutePath() + "/img001.png",
                new OCRTool.OcrCallBack() {
                    @Override
                    public void success(String str) {
                        textView.setText(str);
                    }

                    @Override
                    public void error(String error) {

                    }
                });
    }


    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMTool.getInstence().appStart();
    }
}
