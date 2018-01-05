package com.jeve.cr.tool;

import com.jeve.cr.bean.RecordOcrTotalTimes;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by zhangliang on 2018/1/5.
 */

public class RecordOcrTotalTimesTool {
    private static final String objectId = "ff885a5698";

    /**
     * 更改总的次数
     * @param times +：是递增times次   负：是递减times次
     */
    public static void updateTotalTimes(Integer times){
        RecordOcrTotalTimes totalTimes = new RecordOcrTotalTimes();
        totalTimes.increment("totalTimes",times);
        totalTimes.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        });
    }

    public static void getRecordOcrTotalTimes(final RecordOcrTotalTimesListener listener){
        BmobQuery<RecordOcrTotalTimes> query = new BmobQuery<>();
        query.getObject(objectId, new QueryListener<RecordOcrTotalTimes>() {
            @Override
            public void done(RecordOcrTotalTimes recordOcrTotalTimes, BmobException e) {
                if (listener != null){
                    listener.onRecordOcrTotalTimesListener(recordOcrTotalTimes);
                }
            }
        });
    }

    interface RecordOcrTotalTimesListener{
        void onRecordOcrTotalTimesListener(RecordOcrTotalTimes times);
    }
}
