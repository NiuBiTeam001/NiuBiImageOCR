package com.jeve.cr.activity.imageEdit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.tool.BitmapTool;
import com.jeve.cr.view.FlexImageView;

public class ImageEditActivity extends BaseActivity implements View.OnClickListener {

    private FlexImageView flex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Bitmap bitmap = BitmapTool.loadImage(BitmapTool.PRIMITIVE_PATH, 0);

        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.back_re);
        RelativeLayout finish_re = (RelativeLayout) findViewById(R.id.finish_re);
        flex = (FlexImageView) findViewById(R.id.flex);
        flex.setImageBitmap(bitmap);

        back_re.setOnClickListener(this);
        finish_re.setOnClickListener(this);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_re:
                finish();
                break;
            case R.id.finish_re:
                dealImage();
                break;
        }
    }

    private void dealImage() {
        //得到图片
        Bitmap resultBitmap = flex.cutImage();
        //保存图片
        BitmapTool.saveBitmapToSdcard(resultBitmap);
        //告诉MainActivity更新
        Intent intent = new Intent();
        intent.putExtra("success", "success");
        setResult(3, intent);
        //finish
        finish();
    }
}
