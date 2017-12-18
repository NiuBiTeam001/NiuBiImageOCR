package com.jeve.cr.activity.album;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.tool.BitmapTool;
import com.jeve.cr.tool.DeviceTool;

import java.io.File;
import java.util.ArrayList;

public class AlbumActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GridView gridView;
    private AlbumAdapter adapter;
    private ArrayList<File> fileList = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initViews();
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<File> photoList = PhotoUtils.getPhotoList();
                fileList.addAll(photoList);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.album_back_re);
        gridView = (GridView) findViewById(R.id.album_gv);
        adapter = new AlbumAdapter(this, fileList);
        gridView.setAdapter(adapter);
        back_re.setOnClickListener(this);
        gridView.setOnItemClickListener(this);
    }


    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (!fileList.isEmpty()) {
            String path = fileList.get(position).getAbsolutePath();
            Bitmap originalBitmap = BitmapTool.loadImage(path, DeviceTool.getWidthAndHeight(this).width);
            int degree = BitmapTool.getPictureDegree(path);
            if (degree != 0) {
                originalBitmap = BitmapTool.rotateBitmap(originalBitmap, degree);
            }
            originalBitmap = BitmapTool.scBitmap(originalBitmap, DeviceTool.getWidthAndHeight(this).width);
            BitmapTool.savePrimitiveImag(originalBitmap);
            setResult(0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fileList.size() > 0) {
            fileList.clear();
        }
        Log.d("LJW", "相册 onDestroy");
    }

}
