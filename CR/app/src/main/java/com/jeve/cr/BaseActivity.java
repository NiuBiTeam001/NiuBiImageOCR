package com.jeve.cr;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.jeve.cr.CrApplication;
import com.jeve.cr.R;
import com.jeve.cr.tool.UMTool;

/**
 * Activity基类
 * 主要实现权限访问
 * lijiawei
 * 2017-12-6
 * <p>
 * 需要申请的权限
 * group:android.permission-group.CONTACTS
 * permission:android.permission.WRITE_CONTACTS
 * permission:android.permission.GET_ACCOUNTS
 * permission:android.permission.READ_CONTACTS
 * <p>
 * group:android.permission-group.PHONE
 * permission:android.permission.READ_CALL_LOG
 * permission:android.permission.READ_PHONE_STATE
 * permission:android.permission.CALL_PHONE
 * permission:android.permission.WRITE_CALL_LOG
 * permission:android.permission.USE_SIP
 * permission:android.permission.PROCESS_OUTGOING_CALLS
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 * <p>
 * group:android.permission-group.CALENDAR
 * permission:android.permission.READ_CALENDAR
 * permission:android.permission.WRITE_CALENDAR
 * <p>
 * group:android.permission-group.CAMERA
 * permission:android.permission.CAMERA
 * <p>
 * group:android.permission-group.SENSORS
 * permission:android.permission.BODY_SENSORS
 * <p>
 * group:android.permission-group.LOCATION
 * permission:android.permission.ACCESS_FINE_LOCATION
 * permission:android.permission.ACCESS_COARSE_LOCATION
 * <p>
 * group:android.permission-group.STORAGE
 * permission:android.permission.READ_EXTERNAL_STORAGE
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 * <p>
 * group:android.permission-group.MICROPHONE
 * permission:android.permission.RECORD_AUDIO
 * <p>
 * group:android.permission-group.SMS
 * permission:android.permission.READ_SMS
 * permission:android.permission.RECEIVE_WAP_PUSH
 * permission:android.permission.RECEIVE_MMS
 * permission:android.permission.RECEIVE_SMS
 * permission:android.permission.SEND_SMS
 * permission:android.permission.READ_CELL_BROADCASTS
 */
public class BaseActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
    }

    /**
     * 检测权限
     *
     * @param permission 权限
     * @return true为有权限   false为没有权限
     */
    public Boolean checkPermission(String permission) {
        int selfPermission = ContextCompat.checkSelfPermission(CrApplication.getContext(), permission);
        return selfPermission != PackageManager.PERMISSION_DENIED;
    }

    /**
     * 申请权限
     *
     * @param activity    activity
     * @param permissions 权限（支持多个）
     */
    public void requestPermission(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST);
    }

    /**
     * 申请权限
     *
     * @param activity activity
     */
    public void requestPermission(Activity activity, String permissions) {
        ActivityCompat.requestPermissions(activity, new String[]{permissions}, PERMISSION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        requestError(permissions[i]);
                    } else {
                        requestSuccess(permissions[i]);
                    }
                }
                break;
        }
    }

    public void requestSuccess(String permission) {

    }

    public void requestError(String permission) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //友盟
        UMTool.getInstence().setResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟
        UMTool.getInstence().setPause(this);
    }
}
