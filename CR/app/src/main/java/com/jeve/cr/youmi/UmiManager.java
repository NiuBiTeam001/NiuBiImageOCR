package com.jeve.cr.youmi;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.jeve.cr.CrApplication;
import com.jeve.cr.R;
import com.jeve.cr.activity.MainActivity;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.UserSystemTool;

import net.youmi.android.AdManager;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;

import java.util.Random;

/**
 * umi管理类
 * Created by zhangliang on 2017/12/29.
 */

public class UmiManager {
    private static final String TAG = "zl---UmiManager---";
    private static final String YOUMI_APP_ID = "475240dfd36784aa";
    private static final String YOUMI_SECRET = "728753070452caa8";

    /**
     * U mi 初始化
     */
    public static void initUmi() {
        //TODO 有米需要加小米应用渠道
        AdManager.getInstance(CrApplication.getContext()).init(YOUMI_APP_ID, YOUMI_SECRET, true);//TODO 有米审核时也需要设置为true 发布的时候设置为false

        // 设置是否在通知栏显示下载相关提示。默认为true，标识开启；设置为false则关闭。
        AdManager.getInstance(CrApplication.getContext()).setIsDownloadTipsDisplayOnNotification(true);

        // 获取是否在通知栏显示下载进度的值。
        AdManager.getInstance(CrApplication.getContext()).isDownloadTipsDisplayOnNotification();


        // 设置安装完成后是否在通知栏显示已安装成功的通知。默认为true，标识开启；设置为false则关闭。
        AdManager.getInstance(CrApplication.getContext()).setIsInstallationSuccessTipsDisplayOnNotification(
                true);

        // 获取安装完成后是否在通知栏显示已安装成功的通知的值。
        AdManager.getInstance(CrApplication.getContext()).isInstallationSuccessTipsDisplayOnNotification();
    }

    /**
     * 插屏广告，只需在启动应用的时候调用
     */
    public static void initSpotAd() {
        SpotManager manager = SpotManager.getInstance(CrApplication.getContext());
        //设置横屏模式（当广告有横屏时，会使用横屏模式，默认为竖屏模式）
        manager.setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);
        //设置动画效果为高级模式
        manager.setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
        manager.requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG, "initSpotAd()----success");
            }

            @Override
            public void onRequestFailed(int i) {
                Log.d(TAG, "initSpotAd()----fail");
            }
        });
    }

    /**
     * 展示插屏广告
     */
    public static void showSpot(final Activity activity, final Handler handler) {
        SpotManager.getInstance(activity).showSpot(activity, new SpotListener() {
            @Override
            public void onShowSuccess() {
                Log.d(TAG, "onShowSuccess()");
            }

            @Override
            public void onShowFailed(int i) {
                Log.d(TAG, "onShowFailed()");
            }

            @Override
            public void onSpotClosed() {
                Log.d(TAG, "onSpotClosed()");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                Log.d(TAG, "onSpotClicked()");
                final int random = getRandom();
                UserSystemTool.getInstance().updateUserTimes(random, new UserSystemTool.UserRecordUpdateListener() {
                    @Override
                    public void onUserRecordUpdateListener(int respondCode) {
                        if (respondCode == UserSystemTool.SUCCESS){
                            Message msg = Message.obtain();
                            msg.what = 6;
                            msg.obj = random + MainConfig.getInstance().getUserLeaveOcrTimes();
                            handler.sendMessage(msg);
                            Toast.makeText(activity, String.format(activity.getString(R.string.get_ocr_time_by_ad), random+""), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取随机一到三的数字
     *
     * @return
     */
    private static int getRandom() {
        int max = 3;
        int min = 1;
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 隐藏插屏广告
     */
    public static void hideSpot(Activity activity) {
        SpotManager.getInstance(activity).hideSpot();
    }

    ////////////////////////////生命周期方法///////////////////////////////////////////////

    /**
     * ONPAUSE
     */
    public static void spotOnPause(Activity activity) {
        SpotManager.getInstance(activity).onPause();
    }

    /**
     * onStop
     */
    public static void spotOnStop(Activity activity) {
        SpotManager.getInstance(activity).onStop();
    }

    /**
     * onDestroy
     */
    public static void spotOnDestroy(Activity activity) {
        SpotManager.getInstance(activity).onDestroy();
    }

    /**
     * exit
     */
    public static void spotExit(Activity activity) {
        SpotManager.getInstance(activity).onAppExit();
    }
}
