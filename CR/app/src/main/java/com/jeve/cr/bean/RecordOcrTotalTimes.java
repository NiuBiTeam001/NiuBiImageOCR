package com.jeve.cr.bean;

import cn.bmob.v3.BmobObject;

/**
 * note:记录总次数
 * Created by zhangliang on 2018/1/5.
 */

public class RecordOcrTotalTimes extends BmobObject {
    private Integer totalTimes;

    public Integer getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(Integer totalTimes) {
        this.totalTimes = totalTimes;
    }
}
