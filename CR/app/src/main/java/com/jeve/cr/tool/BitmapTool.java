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
    public final static String DEALPATH = CrApplication.getContext().getFilesDir() + File.separator + "CR" + File.separator + "Deal Image" + File.separator + "dealImage.jpg";
    //处理图片保存文件夹位置
    private final static String DEALPATHFLODER = CrApplication.getContext().getFilesDir() + File.separator + "CR" + File.separator + "Deal Image" + File.separator;

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

    public static final String PRIMITIVE_SAVEPATH = CrApplication.getContext().getFilesDir() + File.separator + "primitive" +
            File.separator;

    //未经过任何处理的图片存放位置（退出就删除）
    public static final String PRIMITIVE_PATH = CrApplication.getContext().getFilesDir() + File.separator + "primitive" +
            File.separator + "primitiveImg.jpg";

    /**
     * 根据资源ID获取Bitmap
     */
    public static Bitmap getBitmap(int resId) {
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 获取图片的缩略图
     */
    public static Bitmap getBitmap(String filePath, int width, int height) {
        return getThumbnail(decodeBitmap(filePath, width, height), width, height);
    }

    /**
     * 按照一定比例获取图片的缩略图
     */
    public static Bitmap decodeBitmap(String path, int w, int h) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int be = Math.min(options.outWidth / w, options.outHeight / h);
        options.inSampleSize = be <= 0 ? 1 : be;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 缩放指定图片，处理宽高比不一致导致拉伸的情况
     */
    public static Bitmap getThumbnail(Bitmap source, int width, int height) {
        return extractThumbnail(source, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    private static final int OPTIONS_SCALE_UP = 0x1;

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source  original bitmap source
     * @param width   targeted width
     * @param height  targeted height
     * @param options options used during thumbnail extraction
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height, int options) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return transform(matrix, source, width, height, OPTIONS_SCALE_UP | options);
    }

    /**
     * Transform source Bitmap to targeted width and height.
     */
    private static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & ThumbnailUtils.OPTIONS_RECYCLE_INPUT) != 0;

        final int sourceW = source.getWidth();
        final int sourceH = source.getHeight();

        int deltaX = sourceW - targetWidth;
        int deltaY = sourceH - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
            * In this case the bitmap is smaller, at least in one dimension,
            * than the target.  Transform it by placing as much of the image
            * as possible into the target and leaving the top/bottom or
            * left/right (or both) black.
            */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int left = Math.max(0, deltaX / 2);
            int top = Math.max(0, deltaY / 2);
            int right = left + Math.min(targetWidth, sourceW);
            int bottom = top + Math.min(targetHeight, sourceH);
            Rect src = new Rect(left, top, right, bottom);

            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);

            c.drawBitmap(source, src, dst, null);
            if (recycle && b2 != source) {
                source.recycle();
            }
            c.setBitmap(null);

            return b2;
        }

        float bitmapAspect = (float) sourceW / sourceH;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = (float) targetHeight / sourceH;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = (float) targetWidth / sourceW;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler == null) {
            b1 = source;
        } else {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, sourceW, sourceH, scaler, true);
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);
        if (b2 != b1) {
            if (recycle && b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }

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

    public static Bitmap rotateBitmap(int resId, float degree) {
        Bitmap b = BitmapFactory.decodeResource(getResources(), resId);
        return rotateBitmap(b, degree);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Bitmap.Config config = drawable.getOpacity() == PixelFormat.OPAQUE ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private static Resources getResources() {
        return CrApplication.getContext().getResources();
    }

    /**
     * 图片圆角处理
     */
    public static Bitmap roundedCornerBitmap(Bitmap source, float round) {
        Bitmap.Config config = source.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, rect, rect, paint);
        return output;
    }

    /**
     * 图片左上角和右上角的圆角处理
     */
    public static Bitmap roundedCornerBitmapTop(Bitmap source, float round, boolean recycleInputBitmap) {
        Bitmap.Config config = source.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int w = source.getWidth();
        int h = source.getHeight();
        Bitmap output = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // 画圆角矩形，四个角都是圆形
        canvas.drawRoundRect(rectF, round, round, paint);

        // 画一个矩形，将底部的两个圆角挡住
        canvas.translate(0, h - round);
        canvas.drawRect(new RectF(0, 0, w, round), paint);
        canvas.translate(0, round - h);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));// 前后的两张图取交叉部分
        canvas.drawBitmap(source, rect, rect, paint);
        if (recycleInputBitmap && source != output) {
            source.recycle();
        }
        return output;
    }

    /**
     * 获取相册图片
     */
    public static Bitmap getBigBitmap(String path) {
        Bitmap selectBitmap;
        try {
            selectBitmap = BitmapFactory.decodeFile(path, getBitmapOptions(path));
            return selectBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
            try {
                selectBitmap = BitmapFactory.decodeFile(path, getBitmapOptions(path));
                return selectBitmap;
            } catch (Exception e2) {
                e2.printStackTrace();
                Toast.makeText(CrApplication.getContext(), CrApplication.getContext().getResources().getString(R.string
                        .getphoto_too_big), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
    }

    /**
     * 获取bitmap缩放的sc
     */
    private static int getOptionsSc(int width, int height) {
        float text = width > height ? width : height;
//        int imageLimit = 720;
//        if (MainConfig.getInstance().getCfgOpenHd()) {
        int imageLimit = 1920;
//        }

        if (text > imageLimit) {
            float temp = text % imageLimit;
            float sc = text / imageLimit;
            if (temp == 0) {
                return (int) sc;
            }
            return (int) sc + 1;
        } else {
            return 1;
        }
    }

    /**
     * 获取bitmap缩放的options
     */
    private static BitmapFactory.Options getBitmapOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        options.inSampleSize = getOptionsSc(bitmapWidth, bitmapHeight);
        options.inJustDecodeBounds = false;
        return options;
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
