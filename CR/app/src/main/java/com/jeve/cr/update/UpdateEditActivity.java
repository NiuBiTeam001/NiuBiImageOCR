package com.jeve.cr.update;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jeve.cr.BaseActivity;
import com.jeve.cr.R;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.DeviceTool;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UpdateEditActivity extends BaseActivity implements View.OnClickListener {

    private EditText versionCode;
    private EditText versionName;
    private EditText update_type;
    private EditText update_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_edit);
        initView();
    }

    private void initView() {
        RelativeLayout back_re = (RelativeLayout) findViewById(R.id.update_back_re);
        RelativeLayout send_re = (RelativeLayout) findViewById(R.id.update_send_re);
        versionCode = (EditText) findViewById(R.id.update_version_code);
        versionName = (EditText) findViewById(R.id.update_version_name);
        update_type = (EditText) findViewById(R.id.update_type);
        update_content = (EditText) findViewById(R.id.update_content);
        back_re.setOnClickListener(this);
        send_re.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_back_re:
                finish();
                break;
            case R.id.update_send_re:
//                send();
                new UpdateManager().uploadApkFile();
                break;
            default:
                break;
        }
    }

    private void send() {
        if (!DeviceTool.isNetworkConnected(this)) {
            Toast.makeText(this, getString(R.string.main_net_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(versionCode.getText().toString())) {
            Toast.makeText(this, getString(R.string.update_version_code_tip), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(versionName.getText().toString())) {
            Toast.makeText(this, getString(R.string.update_version_name_tip), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(update_type.getText().toString())) {
            Toast.makeText(this, getString(R.string.update_type_tip), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(update_content.getText().toString())) {
            Toast.makeText(this, getString(R.string.update_content_tip), Toast.LENGTH_SHORT).show();
            return;
        }
        String OBJECT_ID = "d3e304cf51";
        UpdateInfo info = new UpdateInfo();
        info.setVersionCode(Integer.valueOf(versionCode.getText().toString()));
        info.setVersionName(versionName.getText().toString());
        info.setUpdateType(Integer.valueOf(update_type.getText().toString()));
        info.setContent(update_content.getText().toString());
        info.update(OBJECT_ID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(UpdateEditActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    if (Integer.valueOf(update_type.getText().toString()) == 0) {
                        MainConfig.getInstance().setHalfForceUpdate(false);
                    }
                    finish();
                } else {
                    Toast.makeText(UpdateEditActivity.this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
