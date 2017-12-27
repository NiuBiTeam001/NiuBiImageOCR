package com.jeve.cr.tool;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jeve.cr.CrApplication;
import com.jeve.cr.bean.UserRecord;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zhangliang on 2017/12/27.
 */

public class UserSystemTool {
    private static final String TAG = "zl---UserSystemTool---";
    private UserRecord record;
    private static UserSystemTool tool;
    private String deviceId = "";
    public static final int DATA_NULL_RESPOND_CODE = 101;//数据为null
    public static final int NET_UNENABLE_RESPOND_CODE = 9016;//网络不可用
    private UserSystemTool() {
        this.record = new UserRecord();
        TelephonyManager manager = (TelephonyManager) (CrApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE));
        deviceId = manager.getDeviceId();
    }

    public static UserSystemTool getInstance() {
        if (tool == null) {
            synchronized (UserSystemTool.class) {
                if (tool == null) {
                    tool = new UserSystemTool();
                }
            }
        }
        return tool;
    }

    public void initUser() {
        //将设备唯一id设置为objectID，方便做其它操作
        record.setUserId(deviceId);
        record.setUseTimes(0);
        record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {//表示成功
                    //s 为objectId
                } else {//失败 s 为null
                    initUser();
                }
            }
        });
    }

    /**
     * 获取一行
     */
    public void getUser(final UserRecordListener listener) {
        BmobQuery<UserRecord> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(deviceId, new QueryListener<UserRecord>() {
            @Override
            public void done(UserRecord object, BmobException e) {
                //object可能为空，为null的时候表示获取失败 101：数据不存在  9016：网络错误
                if (listener != null) {
                    listener.onUserRecordLister(object, e.getErrorCode());
                }
            }
        });
    }

    /**
     * 修改一行数据
     *
     * @param times
     */
    public void updateUser(Integer times) {
        record.setUseTimes(times);
        record.update(deviceId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {//成功

                } else {//失败

                }
            }
        });
    }

    /**
     * 删除一行
     */
    public void deleteUser() {
        record.delete(deviceId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {//成功
                    Log.d(TAG, "删除成功:" + record.getUpdatedAt());
                } else {//失败
                    Log.d(TAG, "删除失败：" + e.getMessage());
                }
            }
        });
    }

    public interface UserRecordListener {
        void onUserRecordLister(UserRecord record, int respondCode);
    }
}
