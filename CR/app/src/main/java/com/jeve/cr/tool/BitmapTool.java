package com.jeve.cr.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * 图片处理工具
 * lijiawei
 * 2017-12-13
 */
public class BitmapTool {

    /**
     * 加载图片
     *
     * @param path      图片路径
     * @param maxLength 图片缩放（不缩放填写 0 ）
     * @return 返回处理好的图片
     */
    public static Bitmap loadImage(String path, int maxLength) {
        return BitmapFactory.decodeFile(path, getBitmapOptionsPath(path, maxLength));
    }

    public static Bitmap loadImage(Context context, int id, int maxLength) {
        return BitmapFactory.decodeResource(context.getResources(), id, getBitmapOptionsResources(context, id,
                maxLength));
    }

    private static BitmapFactory.Options getBitmapOptionsPath(String path, int maxLength) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return getNewOptions(options, maxLength);
    }

    private static BitmapFactory.Options getBitmapOptionsResources(Context context, int id, int maxLength) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), id, options);
        return getNewOptions(options, maxLength);
    }

    private static BitmapFactory.Options getNewOptions(BitmapFactory.Options options, int maxLength) {
        //获取图片高宽
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        float sc = 1;
        float realMaxLength = bitmapWidth > bitmapHeight ? bitmapWidth : bitmapHeight;
        if (maxLength != 0 && realMaxLength > maxLength) {
            sc = realMaxLength / maxLength;
        }
        options.inSampleSize = (int) sc;
        options.inJustDecodeBounds = false;
        return options;
    }

    //缩放Bitmap
    public static Bitmap scBitmap(Bitmap bitmap, int maxlength) {
        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        float widthSc = maxlength / bitmapWidth;
        float heightSc = maxlength / bitmapHeight;
        float sc = widthSc < heightSc ? widthSc : heightSc;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(sc, sc);
        // 得到新的图片
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
        if (newBitmap != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return newBitmap;
    }

}
