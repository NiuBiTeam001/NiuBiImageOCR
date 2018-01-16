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

    private UMTool() {
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

    public void init() {
        //初始化友盟
        UMConfigure.init(CrApplication.getContext(), UMConfigure.DEVICE_TYPE_PHONE, null);
        //设置组件化的Log开关
        UMConfigure.setLogEnabled(true);
        //设置日志加密
        UMConfigure.setEncryptEnabled(true);

        MobclickAgent.setScenarioType(CrApplication.getContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public void setResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public void setPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public void sendEvent(String action) {
        MobclickAgent.onEvent(CrApplication.getContext(), action);
    }

    public static class Action {
        public static String CR_APP_START = "cr_app_start";
        public static String CR_CAMERA_CLICK = "cr_camera_click";
        public static String CR_CAMERA_SUCCESS = "cr_camera_success";
        public static String CR_CAMERA_ERROR = "cr_camera_error";
        public static String CR_PHOTO_CLICK = "cr_photo_click";
        public static String CR_PHOTO_SUCCESS = "cr_photo_success";
        public static String CR_SELECT_AGAIN = "cr_select_again";
        public static String CR_DEAL_CLICK = "cr_deal_click";
        public static String CR_DEAL_SUCCESS = "cr_deal_success";
        public static String CR_CR_CLICK = "cr_cr_click";
        public static String CR_CR_SUCCESS = "cr_cr_success";
        public static String CR_CR_ERROR = "cr_cr_error";
        public static String CR_TEXT_CR = "cr_text_cr";
        public static String CR_BANK_CR = "cr_bank_cr";
        public static String CR_FEEDBACK_CLICK = "cr_feedback_click";
        public static String CR_FEEDBACK_SUCCESS = "cr_feedback_success";
        public static String CR_ALLCOPY = "cr_allcopy";
        public static String CR_FREE_COPY = "cr_free_copy";
        public static String CR_AD_SHOW = "cr_ad_show";
        public static String CR_AD_CLICK = "cr_ad_click";
    }
}
