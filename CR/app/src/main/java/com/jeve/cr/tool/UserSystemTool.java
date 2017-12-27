package com.jeve.cr.tool;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jeve.cr.CrApplication;
import com.jeve.cr.bean.UserRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void initUser( int retryTime) {
        final int[] retry = {retryTime};
        //将设备唯一id设置为objectID，方便做其它操作
        record.setUserId(deviceId);
        record.setUseTimes(0);
        record.setTodayGetTime(false);
        record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {//表示成功
                    //s 为objectId
                } else {//失败 s 为null
                    --retry[0];
                    if (retry[0] >= 1) {
                        initUser(retry[0]);
                    }
                }
            }
        });
    }

    /**
     * 获取一行,查询数据
     */
    public void getUser(final UserRecordListener listener) {
        BmobQuery query = new BmobQuery("UserRecord");
        query.addWhereEqualTo("userId", deviceId);
        //v3.5.0版本提供`findObjectsByTable`方法查询自定义表名的数据
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e != null) {
                    //应该是网络出现异常造成
                    if (listener != null) {
                        listener.onUserRecordLister(null, e.getErrorCode());
                    }
                    return;
                }
                try {
                    JSONObject object = (JSONObject) ary.get(0);
                    UserRecord record = new UserRecord();
                    record.setUserId(object.getString("userId"));
                    record.setUseTimes(object.getInt("useTimes"));
                    if (listener != null) {
                        listener.onUserRecordLister(record, 0);
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    if (listener != null) {
                        listener.onUserRecordLister(null, 0);
                    }
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
