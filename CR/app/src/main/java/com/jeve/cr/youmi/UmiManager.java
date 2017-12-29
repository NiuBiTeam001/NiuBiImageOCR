package com.jeve.cr.youmi;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jeve.cr.CrApplication;

import net.youmi.android.AdManager;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;

/**
 * umi管理类
 * Created by zhangliang on 2017/12/29.
 */

public class UmiManager {
    private static final String TAG = "zl---UmiManager---";
    private static final String YOUMI_APP_ID = "475240dfd36784aa";
    private static final String YOUMI_SECRET = "728753070452caa8";

    /**
     *U mi 初始化
     */
    public static void initUmi(){
        //TODO 有米需要加小米应用渠道
        AdManager.getInstance(CrApplication.getContext()).init(YOUMI_APP_ID, YOUMI_SECRET, true );//TODO 有米审核时也需要设置为true 发布的时候设置为false
    }

    /**
     * 插屏广告，只需在启动应用的时候调用
     */
    public static void initSpotAd(){
        SpotManager manager = SpotManager.getInstance(CrApplication.getContext());
        //设置横屏模式（当广告有横屏时，会使用横屏模式，默认为竖屏模式）
        manager.setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);
        //设置动画效果为高级模式
        manager.setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
        manager.requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG,"initSpotAd()----success");
            }

            @Override
            public void onRequestFailed(int i) {
                Log.d(TAG,"initSpotAd()----fail");
            }
        });
    }

    /**
     * 展示插屏广告
     */
    public static void showSpot(Activity activity){
        SpotManager.getInstance(activity).showSpot(activity, new SpotListener() {
            @Override
            public void onShowSuccess() {
                Log.d(TAG,"onShowSuccess()");
            }

            @Override
            public void onShowFailed(int i) {
                Log.d(TAG,"onShowFailed()");
            }

            @Override
            public void onSpotClosed() {
                Log.d(TAG,"onSpotClosed()");
            }

            @Override
            public void onSpotClicked(boolean b) {
                Log.d(TAG,"onSpotClicked()");
            }
        });
    }

    /**
     * 隐藏插屏广告
     */
    public static void hideSpot(Activity activity){
        SpotManager.getInstance(activity).hideSpot();
    }

    ////////////////////////////生命周期方法///////////////////////////////////////////////
    /**
     * ONPAUSE
     */
    public static void spotOnPause(Activity activity){
        SpotManager.getInstance(activity).onPause();
    }

    /**
     * onStop
     */
    public static void spotOnStop(Activity activity){
        SpotManager.getInstance(activity).onStop();
    }

    /**
     * onDestroy
     */
    public static void spotOnDestroy(Activity activity){
        SpotManager.getInstance(activity).onDestroy();
    }

    /**
     * exit
     */
    public static void spotExit(Activity activity){
        SpotManager.getInstance(activity).onAppExit();
    }
}
