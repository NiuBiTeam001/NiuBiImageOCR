package com.jeve.cr.update;

import android.app.Activity;
import android.app.Dialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeve.cr.CrApplication;
import com.jeve.cr.R;
import com.jeve.cr.config.MainConfig;
import com.jeve.cr.tool.DeviceTool;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * note:更新管理类
 * Created by zhangliang on 2018/1/5.
 */

public class UpdateManager implements View.OnClickListener {

    private Dialog dialog;
    private int updateType;//0 半强制  1： 强制

    public void checkUpdate(final Activity activity) {
        //网络判断
        if (!DeviceTool.isNetworkConnected(CrApplication.getContext())) {
            return;
        }
//        //查询时间是否过期
//        if (getQueryTimeInterval() < 12) {
//            return;
//        }
        MainConfig.getInstance().setUpdateQueryTime(System.currentTimeMillis());

        BmobQuery<UpdateInfo> query = new BmobQuery<>();
        query.getObject("d3e304cf51", new QueryListener<UpdateInfo>() {
            @Override
            public void done(UpdateInfo updateInfo, BmobException e) {
                if (updateInfo != null) {
                    if (updateInfo.getVersionCode() > Integer.valueOf(DeviceTool.getVersionCode(activity))) {
                        //type  0:半强制更新  1：强制更新
                        if ((updateInfo.getUpdateType() == 0 && !MainConfig.getInstance().getHalfForceUpdate())
                                || updateInfo.getUpdateType() == 1) {
                            showUpdateDialog(updateInfo, activity);
                        }
                    }
                }
            }
        });

    }

    /**
     * 获取时间间隔
     *
     * @return
     */
    private long getQueryTimeInterval() {
        long queryTime = MainConfig.getInstance().getUpdateQueryTime();
        return (System.currentTimeMillis() - queryTime) / (3600 * 1000);
    }

    private void showUpdateDialog(UpdateInfo info, Activity activity) {
        updateType = info.getUpdateType();
        View layout = LayoutInflater.from(activity).inflate(R.layout.dialog_update_layout, null);
        TextView versionName = layout.findViewById(R.id.update_version_name);
        versionName.setText(info.getVersionName());
        TextView content = layout.findViewById(R.id.update_content);
        content.setText(getUpdateContent(info.getContent()));
        Button sure = layout.findViewById(R.id.update_sure);
        dialog = new Dialog(activity);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        switch (info.getUpdateType()) {
            //半强制
            case 0:
                sure.setOnClickListener(this);
                MainConfig.getInstance().setHalfForceUpdate(true);
                dialog.show();
                break;
            //强制
            case 1:
                sure.setOnClickListener(this);
                dialog.setCancelable(false);
                dialog.show();
                break;
            default:
                break;
        }
        setDialogView(activity);
    }

    private String getUpdateContent(String content) {
        String[] strings = content.split(",");
        if (strings.length > 0) {
            String string = "";
            for (int i = 0; i < strings.length; ++i) {
                if (i == strings.length - 1) {
                    string += strings[i];
                } else {
                    string += strings[i] + "\n";
                }
            }
            return string;
        } else {
            return content;
        }
    }

    private void setDialogView(Activity activity) {
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();//获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.91);    //宽度设置为屏幕的0.55
        dialog.getWindow().setAttributes(p); //设置生效
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_sure:
                if (updateType == 0){
                    dialog.dismiss();
                }else if (updateType == 1){
                    android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                    System.exit(0);
                }
                break;
            default:
                break;
        }
    }

}
