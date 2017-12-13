package com.jeve.cr.activity.feedback;

import cn.bmob.v3.BmobObject;

/**
 * note:feedback需要提交到Bmob服务器的数据，必须继承与BmobObject
 * Created by zhangliang on 2017/12/13.
 */

public class Feedback extends BmobObject {
    private String suggestion;
    private String bug;
    private String deviceInfo;

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }


    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }


    public void setBug(String bug) {
        this.bug = bug;
    }
}
