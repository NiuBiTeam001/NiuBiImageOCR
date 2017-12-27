package com.jeve.cr.bean;

import cn.bmob.v3.BmobObject;

/**
 * note:主要用于保存用户的使用次数，及设备唯一识别码
 * Created by zhangliang on 2017/12/27.
 */

public class UserRecord extends BmobObject {
    private String userId;//设备唯一表示
    private Integer useTimes;//使用次数

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUseTimes(Integer useTimes) {
        this.useTimes = useTimes;
    }
}
