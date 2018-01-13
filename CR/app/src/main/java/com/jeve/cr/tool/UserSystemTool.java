package com.jeve.cr.tool;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.jeve.cr.bean.UserRecord;
import com.jeve.cr.config.MainConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;

import cn.bmob.v3.Bmob;
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
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private UserSystemTool() {
        this.record = new UserRecord();
        deviceId = getPesudoUniqueID();
    }

    private String getPesudoUniqueID() {
        String deviceId = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return deviceId;
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

    public void initUser(int retryTime, final Handler handler) {
        final int[] retry = {retryTime};
        record.setUserId(deviceId);
        record.setUseTimes(4);
        record.setTodayGetTime(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URLConnection conn = new URL("https://www.baidu.com/").openConnection();
                    long time = conn.getDate();
                    record.setResetTime(time);
                    record.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            //表示成功
                            if (e == null) {
                                MainConfig.getInstance().setUserObjectId(s);
                                Message message = new Message();
                                message.obj = 4;
                                message.what = 2;
                                handler.sendMessage(message);
                            } else {//失败 s 为null
                                --retry[0];
                                if (retry[0] >= 1) {
                                    initUser(retry[0],handler);
                                }
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
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
                    record.setObjectId(object.getString("objectId"));
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
     * 查询用户
     */
    public void queryUser(final UserRecordQueryListener listener) {
        BmobQuery<UserRecord> query = new BmobQuery<>();
        query.getObject(MainConfig.getInstance().getUserObjectId(), new QueryListener<UserRecord>() {
            @Override
            public void done(UserRecord userRecord, BmobException e) {
                if (listener != null) {
                    listener.onUserRecordQueryLister(userRecord);
                }
            }
        });
    }

    /**
     * 修改剩余次数，Bmob原子操作
     *
     * @param times 正：表示递增times次   负：递减times次
     */
    public void updateUserTimes(Integer times, final UserRecordUpdateListener listener) {
        record.increment("useTimes", times);
        record.update(MainConfig.getInstance().getUserObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {//成功
                    if (listener != null) {
                        listener.onUserRecordUpdateListener(SUCCESS);
                    }
                } else {//失败
                    if (listener != null) {
                        listener.onUserRecordUpdateListener(FAILED);
                    }
                }
            }
        });
    }

    /**
     * 修改今天是否领取
     */
    public void updateUserIsGetTimes(Boolean isGet, final UserRecordUpdateListener listener) {
        record.setTodayGetTime(isGet);
        record.update(MainConfig.getInstance().getUserObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                //成功
                if (e == null) {
                    if (listener != null) {
                        listener.onUserRecordUpdateListener(SUCCESS);
                    }
                } else {//失败
                    if (listener != null) {
                        listener.onUserRecordUpdateListener(FAILED);
                    }
                }
            }
        });
    }

    /**
     * 删除一行
     */
    public void deleteUser() {
        record.delete(MainConfig.getInstance().getUserObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                //成功
                if (e == null) {
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

    public interface UserRecordQueryListener {
        void onUserRecordQueryLister(UserRecord record);
    }

    public interface UserRecordUpdateListener {
        void onUserRecordUpdateListener(int respondCode);
    }
}
