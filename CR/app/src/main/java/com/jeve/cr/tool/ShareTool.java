package com.jeve.cr.tool;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.widget.Toast;

import com.jeve.cr.CrApplication;

import java.io.File;

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

    /**
     * 分享文件
     * @param context
     * @param file
     */
    public static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            share.setType(getMimeType(file.getAbsolutePath()));//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, ""));
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param filePath
     * @return
     */
    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            } catch (IllegalStateException e) {
                return mime;
            } catch (IllegalArgumentException e) {
                return mime;
            } catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }
}
