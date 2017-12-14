package com.jeve.cr.tool;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.widget.Toast;

import com.jeve.cr.CrApplication;
import com.jeve.cr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图片处理工具
 * lijiawei
 * 2017-12-13
 */
public class BitmapTool {

    //处理图片保存位置
    public final static String DEALPATH = CrApplication.getContext().getFilesDir() + File.separator + "CR" + File
            .separator + "Deal Image" + File.separator + "dealImage.jpg";
    //处理图片保存文件夹位置
    private final static String DEALPATHFLODER = CrApplication.getContext().getFilesDir() + File.separator + "CR" +
            File.separator + "Deal Image" + File.separator;

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


    //创建图片文件
    private static File getOutputMediaFile() {
        // Create a media file name
        File mediaFile;
        File imgFile;

        mediaFile = new File(DEALPATHFLODER);
        if (!mediaFile.exists()) {
            mediaFile.mkdirs();
        }
        imgFile = new File(DEALPATH);
        if (imgFile.exists())
            imgFile.delete();

        return imgFile;
    }

    //图片保存到本地
    public static boolean saveBitmapToSdcard(Bitmap selectBitmap) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
            return selectBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据资源ID获取Bitmap
     */
    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }


    private static final String PRIMITIVE_SAVEPATH = CrApplication.getContext().getFilesDir() + File.separator + "CR" +
            File.separator + "photo" + File.separator;

    //未经过任何处理的图片存放位置（退出就删除）
    public static final String PRIMITIVE_PATH = CrApplication.getContext().getFilesDir() + File.separator + "CR" +
            File.separator + "photo" + File.separator + "primitiveImg.jpg";

    /**
     * 获取图片的旋转角度
     */
    public static int getPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface
                    .ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    /**
     * 图片旋转
     *
     * @param source 要旋转的图片
     * @param degree 图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     */
    public static Bitmap rotateBitmap(Bitmap source, float degree) {
        return rotateBitmap(source, degree, true);
    }

    /**
     * 图片旋转
     *
     * @param source       要旋转的图片
     * @param degree       图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     * @param recycleInput 是否释放source
     */
    public static Bitmap rotateBitmap(Bitmap source, float degree, boolean recycleInput) {
        if (source == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap b = Bitmap.createBitmap(source, 0, 0, w, h, matrix, true);
        if (recycleInput && b != source) {
            source.recycle();
        }
        return b;
    }

    private static Resources getResources() {
        return CrApplication.getContext().getResources();
    }

    /**
     * 保存最原始图片
     */
    public static boolean savePrimitiveImag(Bitmap primitiveBitmap) {
        File pictureFile = getOutputPrimitiveMediaFile();
        if (pictureFile == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
            boolean success = primitiveBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建最原始图片
     */
    public static File getOutputPrimitiveMediaFile() {
        // Create a media file name
        File mediaFile;
        File imgFile;

        mediaFile = new File(PRIMITIVE_SAVEPATH);
        if (!mediaFile.exists()) {
            mediaFile.mkdirs();
        }
        imgFile = new File(PRIMITIVE_PATH);

        return imgFile;
    }

}
