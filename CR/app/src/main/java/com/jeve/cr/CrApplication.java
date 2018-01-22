package com.jeve.cr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jeve.cr.bean.SaveUs;
import com.jeve.cr.bean.UserRecord;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.system.AppExceptionDeal;
import com.jeve.cr.tool.DeviceTool;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.UMTool;
import com.jeve.cr.tool.UserSystemTool;
import com.jeve.cr.youmi.UmiManager;

import net.youmi.android.AdManager;
import net.youmi.android.os.OffersManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.waps.AppConnect;

import static android.R.id.list;

/**
 * Application
 * lijiawei
 * 2017-12-6
 */
public class CrApplication extends Application {

    private static Context context;
    ArrayList<Activity> list = new ArrayList<>();
    private static final String BMOBAPPLICATION_ID = "57e5fc30ae14024bb7f93f303f142e25";
    private static final String WAPAPP_ID = "843cdffea9ade4d7a44827fc5aa4df77";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //Bmob初始化
        Bmob.initialize(this, BMOBAPPLICATION_ID);
        //百度SDK初始化
        OCRTool.getInstence().init(this);
        //全局报错处理初始化
        //设置该CrashHandler为程序的默认处理器
//        AppExceptionDeal catchExcep = new AppExceptionDeal(this);
//        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
        //友盟
        UMTool.getInstence().init();
        //有米初始化
//        UmiManager.initUmi();
        //万普广告初始化
        AppConnect.getInstance(WAPAPP_ID,"waps",this);
        //万普插屏广告初始化
        AppConnect.getInstance(this).initPopAd(this);
//        //有米插屏初始化
//        UmiManager.initSpotAd();
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
