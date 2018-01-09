package com.jeve.cr.activity.result;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.UMTool;

/**
 * 结果展示页面
 * lijiawei
 * 2017-12-27
 */
public class ResultActivity extends BaseActivity implements View.OnClickListener {
    private EditText result_tv;
    private RelativeLayout copy_tip_re;
    private TextView copy_tip_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        init();
    }

    public void init() {
        Intent intent = getIntent();
        result_tv = (EditText) findViewById(R.id.result_tv);
        RelativeLayout copy_re = (RelativeLayout) findViewById(R.id.copy_re);
        RelativeLayout share_re = (RelativeLayout) findViewById(R.id.share_re);
        copy_tip_tv = (TextView) findViewById(R.id.copy_tip_tv);
        copy_tip_re = (RelativeLayout) findViewById(R.id.copy_tip_re);
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.back_re);

        result_tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                UMTool.getInstence().sendEvent(UMTool.Action.CR_FREE_COPY);
                return false;
            }
        });
        result_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result_tv.setFocusable(true);
                result_tv.setFocusableInTouchMode(true);
                result_tv.requestFocus();
                result_tv.requestFocusFromTouch();
                //弹出键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });

        copy_re.setOnClickListener(this);
        copy_tip_re.setOnClickListener(this);
        back_re.setOnClickListener(this);

        result_tv.setText(intent.getStringExtra("result"));
        result_tv.clearFocus();
        result_tv.setFocusable(false);

        showTextRsultTip();
    }

    /**
     * 复制功能
     */
    private void copyTest(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("copy", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(this, getString(R.string.main_coyp_success), Toast.LENGTH_SHORT).show();
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_re:
                finish();
            case R.id.copy_re:
                UMTool.getInstence().sendEvent(UMTool.Action.CR_ALLCOPY);
                copyTest(result_tv.getText().toString());
                break;
            case R.id.copy_tip_re:
                copy_tip_re.setVisibility(View.GONE);
                break;
            case R.id.share_re:

                break;
        }
    }

    private void showTextRsultTip() {
        if (MainConfig.getInstance().getCopyTip()) {
            MainConfig.getInstance().setCopyTip(false);
            //提示用户可以自由复制
            copy_tip_tv.setText(getString(R.string.main_coyp_tip));
            copy_tip_re.setVisibility(View.VISIBLE);
        }
    }
}
