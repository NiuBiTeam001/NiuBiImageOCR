package com.jeve.cr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.jeve.cr.system.AppExceptionDeal;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.UMTool;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;

import static android.R.id.list;

/**
 * Application
 * lijiawei
 * 2017-12-6
 */
public class CrApplication extends Application {

    private static Context context;
    ArrayList<Activity> list = new ArrayList<>();
    private static final String APPLICATION_ID = "57e5fc30ae14024bb7f93f303f142e25";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //Bmob初始化
        Bmob.initialize(this, APPLICATION_ID);
        //百度SDK初始化
        OCRTool.getInstence().init(this);
        //全局报错处理初始化
        //设置该CrashHandler为程序的默认处理器
//        AppExceptionDeal catchExcep = new AppExceptionDeal(this);
//        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
        //友盟
        UMTool.getInstence().init();
    }

    public static Context getContext() {
        return context;
    }


    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
