package com.jeve.cr.tool;

import android.content.Context;

import com.jeve.cr.CrApplication;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * 友盟
 * lijiawei
 * 2017-12-7
 */
public class UMTool {
    private static UMTool umTool;
    private static final String APPKEY = "5a290d6ef43e485a93000030";

    String app_start = "app_start";

    private UMTool() {
        //初始化友盟
//        UMConfigure.init(CrApplication.getContext(), UMConfigure.DEVICE_TYPE_PHONE, APPKEY);
        MobclickAgent.setScenarioType(CrApplication.getContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public static UMTool getInstence() {
        if (umTool == null) {
            synchronized (UMTool.class) {
                if (umTool == null) {
                    umTool = new UMTool();
                }
            }
        }
        return umTool;
    }

    public void setResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public void setPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public void appStart() {
        MobclickAgent.onEvent(CrApplication.getContext(), app_start);
    }

    public void openDebug(){
        MobclickAgent.setDebugMode(true);
    }

}
