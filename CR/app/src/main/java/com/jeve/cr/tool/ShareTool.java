package com.jeve.cr.tool;

import android.content.Intent;
import android.net.Uri;

import com.jeve.cr.CrApplication;

/**
 * note:分享或者到应用市场评分
 * Created by zhangliang on 2017/12/22.
 */

public class ShareTool {
    public static void goToMarket(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("market://details?id=" + CrApplication.getContext().getPackageName()));
        try{
            CrApplication.getContext().startActivity(intent);
        }catch (Exception e){
            openLinkBySystem("");
        }
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private static void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        CrApplication.getContext().startActivity(intent);
    }
}
