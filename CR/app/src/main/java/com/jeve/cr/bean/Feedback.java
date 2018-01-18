package com.jeve.cr.bean;

import cn.bmob.v3.BmobObject;

/**
 * note:feedback需要提交到Bmob服务器的数据，必须继承与BmobObject
 * Created by zhangliang on 2017/12/13.
 */

public class Feedback extends BmobObject {
    private String tag;//其它，bug，文案，界面
    private String content;
    private String email;
    private String deviceInfo;

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }


    public void setContent(String content) {
        this.content = content;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
