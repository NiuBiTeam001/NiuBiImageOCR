package com.jeve.cr.activity.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jeve.cr.R;
import com.jeve.cr.tool.BitmapTool;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by zhangliang on 2017/12/15.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private Context context;
    private ArrayList<File> fileList;
    private LruCache<String, Bitmap> lruCache;

    public AlbumAdapter(Context context, ArrayList<File> fileList) {
        this.context = context;
        this.fileList = fileList;

        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        lruCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String path, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);

            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(context).inflate(R.layout.album_item_layout, parent, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bitmap bitmap = lruCache.get(fileList.get(position).getAbsolutePath());
        if (bitmap == null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String path = fileList.get(position).getAbsolutePath();
                    Bitmap loadImage = BitmapTool.loadImage(path, 0);
                    int degree = BitmapTool.getPictureDegree(path);
                    Bitmap rotateBitmap = BitmapTool.rotateBitmap(loadImage, degree);
                    lruCache.put(path,rotateBitmap);
                }
            }).start();
        }else {
            holder.iv.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        ViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.album_item_iv);
        }
    }
}
