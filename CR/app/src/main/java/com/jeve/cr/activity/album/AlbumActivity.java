package com.jeve.cr.activity.album;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;

public class AlbumActivity extends BaseActivity implements View.OnClickListener{
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initViews();
    }

    private void initViews() {
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.album_back_re);
        recyclerView = (RecyclerView) findViewById(R.id.album_recycler);
        back_re.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        finish();
    }
}
