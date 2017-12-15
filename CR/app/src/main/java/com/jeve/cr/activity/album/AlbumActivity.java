package com.jeve.cr.activity.album;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;

import java.io.File;
import java.util.ArrayList;

public class AlbumActivity extends BaseActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private ArrayList<File> fileList = new ArrayList<>();
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
                fileList = photoList;
                adapter.notifyDataSetChanged();
            }
        }).start();
    }

    private void initViews() {
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.album_back_re);
        recyclerView = (RecyclerView) findViewById(R.id.album_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new AlbumAdapter(this,fileList);
        recyclerView.setAdapter(adapter);
        back_re.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fileList.size() > 0){
            fileList.clear();
        }
    }
}
