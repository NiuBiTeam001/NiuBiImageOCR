package com.jeve.cr.activity.album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.jeve.cr.tool.BitmapTool;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 图片加载类
 * lijiawei
 * 2016-8-5
 */
public class ImageLoaderTool {

    private static ImageLoaderTool imageLoaderTool;

    private LruCache<String, Bitmap> lruCache;

    private LinkedList<Runnable> linkedList;

    private ExecutorService mThreadPool;
    private static final int THREAD_NUM = 3;

    private Thread mLunThread;
    private Handler mLunHandler;

    private Handler mUIHandler;

    private Semaphore mLunSemaphore = new Semaphore(0);

    private Semaphore threadPoolSemaphore;

    private ImageLoaderTool() {
        init();
    }

    private void init() {
        linkedList = new LinkedList<>();

        mLunThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mLunHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {

                        try {
                            //通知任务队列
                            mThreadPool.execute(linkedList.removeLast());
                            threadPoolSemaphore.acquire();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mLunSemaphore.release();
                Looper.loop();
            }
        };
        mLunThread.start();

        int MaxMemory = (int) Runtime.getRuntime().maxMemory();
        int lruMemory = MaxMemory / 10;
        lruCache = new LruCache<String, Bitmap>(lruMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

        mThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
        threadPoolSemaphore = new Semaphore(THREAD_NUM);
    }

    public static ImageLoaderTool getInstance() {
        if (imageLoaderTool == null) {
            synchronized (ImageLoaderTool.class) {
                if (imageLoaderTool == null) {
                    imageLoaderTool = new ImageLoaderTool();
                }
            }
        }
        return imageLoaderTool;
    }

    /**
     * 加载图片
     */
    public void loadImage(String path, ImageView imageView) {
        imageView.setTag(path);

        mUIHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ImgBind imgBind = (ImgBind) msg.obj;
                if (imgBind.imageView.getTag().toString().equals(imgBind.path) && imgBind.bitmap != null) {
                    imgBind.imageView.setImageBitmap(imgBind.bitmap);
                }
            }
        };

        loadImageFromLruCache(path, imageView);
    }

    private void loadImageFromLruCache(final String key, final ImageView imageView) {
        Bitmap bitmap = lruCache.get(key);
        if (bitmap != null) {
            refreshImageview(key, bitmap, imageView);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取图片   剪裁图片
                        Bitmap getBitmap = BitmapFactory.decodeFile(key, getBitmapOptions(key, imageView));

                        int degree = BitmapTool.getPictureDegree(key);
                        Bitmap rotateBitmap = BitmapTool.rotateBitmap(getBitmap, degree);
                        //存入缓存
                        if (lruCache.get(key) == null && rotateBitmap != null) {
                            lruCache.put(key, rotateBitmap);

                            refreshImageview(key, rotateBitmap, imageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            System.gc();
                            //获取图片   剪裁图片
                            Bitmap getBitmap = BitmapFactory.decodeFile(key, getBitmapOptions(key, imageView));
                            int degree = BitmapTool.getPictureDegree(key);
                            Bitmap rotateBitmap = BitmapTool.rotateBitmap(getBitmap, degree);
                            //存入缓存
                            if (lruCache.get(key) == null && rotateBitmap != null) {
                                lruCache.put(key, rotateBitmap);

                                refreshImageview(key, rotateBitmap, imageView);
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    threadPoolSemaphore.release();
                }
            });
        }
    }

    /**
     * 获取bitmap缩放的sc
     */
    private int getOptionsSc(ImageView imageView, int width, int height) {
        int imgX = imageView.getWidth();
        int text = width > height ? width : height;
        if (text > imgX) {
            if (imgX != 0) {
                float sc = text / imgX;
                return (int) sc;
            } else {
                float sc = text / 240;
                return (int) sc;
            }
        } else {
            return 2;
        }
    }

    /**
     * 获取bitmap缩放的options
     */
    private BitmapFactory.Options getBitmapOptions(String key, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(key, options);
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        options.inSampleSize = getOptionsSc(imageView, bitmapWidth, bitmapHeight);
        options.inJustDecodeBounds = false;
        return options;
    }

    /**
     * 加入任务
     */
    private synchronized void addTask(Runnable loadImageThread) {
        try {
            if (linkedList == null) {
                linkedList = new LinkedList<>();
            }

            if (loadImageThread != null) {
                linkedList.add(loadImageThread);
                // if (mLunHandler != null)

                if (mLunHandler == null)
                    mLunSemaphore.acquire();


                mLunHandler.sendEmptyMessage(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通知handler设置图片
     */
    private void refreshImageview(String key, Bitmap bitmap, ImageView imageView) {
        Message message = new Message();
        ImgBind imgBind = new ImgBind();
        imgBind.path = key;
        imgBind.bitmap = bitmap;
        imgBind.imageView = imageView;
        message.obj = imgBind;
        mUIHandler.sendMessage(message);
    }

    private class ImgBind {
        Bitmap bitmap;
        String path;
        ImageView imageView;
    }
}
