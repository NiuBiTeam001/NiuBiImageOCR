package com.jeve.cr.update;

import cn.bmob.v3.BmobObject;

/**
 * 必须继承于BmobObject
 * Created by zhangliang on 2018/1/4.
 */

public class UpdateInfo extends BmobObject{
    //版本号
    private Integer versionCode;
    //版本名字
    private String versionName;
    //更新内容
    private String content;
    //更新形式 半强制更新：0  强制更新：1
    private Integer updateType;

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUpdateType() {
        return updateType;
    }

    public void setUpdateType(Integer updateType) {
        this.updateType = updateType;
    }
}
