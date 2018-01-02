package com.jeve.cr.bean;

import cn.bmob.v3.BmobObject;

/**
 * note:当我们ad《ocr时，中止
 * Created by zhangliang on 2018/1/2.
 */

public class SaveUs extends BmobObject {
    private Boolean stop;

    public Boolean getStop() {
        return stop;
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }
}
