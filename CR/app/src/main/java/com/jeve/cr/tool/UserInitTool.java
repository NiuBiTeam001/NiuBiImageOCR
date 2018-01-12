package com.jeve.cr.tool;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.TextureView;

import com.jeve.cr.bean.UserRecord;
import com.jeve.cr.config.MainConfig;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户初始化流程
 * lijiawei
 * 2018-1-6
 */

public class UserInitTool {

    //初始化用户
    public static void initUser(Context context, final Handler handler) {
        if (DeviceTool.isNetworkConnected(context)) {
            //第一次进入应用，需要在后台建立用户设备id，和次数的数据库，后面处理只需要进行修改或其它处理
            if (TextUtils.isEmpty(MainConfig.getInstance().getUserObjectId())) {
                UserSystemTool.getInstance().getUser(new UserSystemTool.UserRecordListener() {
                    @Override
                    public void onUserRecordLister(UserRecord record, int respondCode) {
                        if (record == null && respondCode != UserSystemTool.NET_UNENABLE_RESPOND_CODE) {
                            //表示数据库之前并没有保存唯一设备id的这条数据
                            UserSystemTool.getInstance().initUser(3,handler);
                        } else if (record != null) {
                            //避免用户清除数据后，将本地objectid清除
                            MainConfig.getInstance().setUserObjectId(record.getObjectId());
                            //获取识别次数
                            getUseCount(handler);
                        }
                    }
                });
            } else {
                getUseCount(handler);
            }
        } else {
            //没有网络情况下
            handler.sendEmptyMessage(3);
        }

    }

    /**
     * 获取免费次数
     */
    private static void getUseCount(final Handler handler) {
        //获取识别次数
        UserSystemTool.getInstance().queryUser(new UserSystemTool.UserRecordQueryListener() {
            @Override
            public void onUserRecordQueryLister(UserRecord record) {
                if (record != null) {
                    int count = record.getUseTimes();
                    MainConfig.getInstance().setUserLeaveOcrTimes(count);
                    Message message = new Message();
                    message.obj = count;
                    message.what = 2;
                    handler.sendMessage(message);
                }
                //设置免费领取
                initUserFreeGet();
            }
        });
    }

    /**
     * 初始化用户免费获取条件
     */
    private static void initUserFreeGet() {
        String objectID = MainConfig.getInstance().getUserObjectId();
        if (TextUtils.isEmpty(objectID)) {
            return;
        }
        BmobQuery<UserRecord> query = new BmobQuery<>();
        query.getObject(objectID, new QueryListener<UserRecord>() {
            @Override
            public void done(final UserRecord userRecord, BmobException e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //如果大于了一天需要将置为可以免费获取
                        try {
                            URLConnection conn = new URL("https://www.baidu.com/").openConnection();
                            long time = conn.getDate();
                            SimpleDateFormat format = new SimpleDateFormat("dd");
                            int format1 = Integer.parseInt(format.format(new Date(time)));
                            int format2 = Integer.parseInt(format.format(new Date(userRecord.getResetTime())));
                            if (Math.abs(format1 - format2) >= 1) {
                                userRecord.setResetTime(time);
                                userRecord.setTodayGetTime(false);
                                userRecord.update(MainConfig.getInstance().getUserObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

}
