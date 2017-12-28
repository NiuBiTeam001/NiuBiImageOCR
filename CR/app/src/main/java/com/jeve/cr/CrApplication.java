package com.jeve.cr;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.jeve.cr.bean.UserRecord;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.system.AppExceptionDeal;
import com.jeve.cr.tool.OCRTool;
import com.jeve.cr.tool.UMTool;
import com.jeve.cr.tool.UserSystemTool;

import net.youmi.android.AdManager;

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
    private static final String YOUMI_APP_ID = "475240dfd36784aa";
    private static final String YOUMI_SECRET = "728753070452caa8";

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
        //有米初始化
        AdManager.getInstance(context).init(YOUMI_APP_ID, YOUMI_SECRET, true );//TODO 有米审核时也需要设置为true 发布的时候设置为false
        initUser();
    }

    private void initUser(){
        //第一次进入应用，需要在后台建立用户设备id，和次数的数据库，后面处理只需要进行修改或其它处理
        if (MainConfig.getInstance().getFirstUseApp()) {
            MainConfig.getInstance().setFirstUseApp(false);
            UserSystemTool.getInstance().getUser(new UserSystemTool.UserRecordListener() {
                @Override
                public void onUserRecordLister(UserRecord record, int respondCode) {
                    if (record == null && respondCode != UserSystemTool.NET_UNENABLE_RESPOND_CODE) {
                        //表示数据库之前并没有保存唯一设备id的这条数据
                        UserSystemTool.getInstance().initUser(3);
                    }else if (record != null){
                        //避免用户清除数据后，将本地objectid清除
                        MainConfig.getInstance().setUserObjectId(record.getObjectId());
                    }
                }
            });
        }else {
            UserSystemTool.getInstance().queryUser(new UserSystemTool.UserRecordQueryListener() {
                @Override
                public void onUserRecordQueryLister(UserRecord record) {
                    if (record != null){
                        //// TODO: 2017/12/28

                    }
                }
            });
        }
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
