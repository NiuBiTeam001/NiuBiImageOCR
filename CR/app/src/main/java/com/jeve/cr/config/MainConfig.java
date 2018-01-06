package com.jeve.cr.config;

/**
 * note:主配置类
 * Created by zhangliang on 2017/12/13.
 */

public class MainConfig extends BaseConfig {
    private static final String MAIN_CONFIG = "main_config";
    private static MainConfig mainConfig;

    public static MainConfig getInstance() {
        if (mainConfig == null) {
            synchronized (MainConfig.class) {
                if (mainConfig == null) {
                    mainConfig = new MainConfig();
                }
            }
        }
        return mainConfig;
    }

    private MainConfig() {
        super(MAIN_CONFIG);
    }

    /**
     * 拍照的时间
     */
    private final String TAKE_PHOTO_TIME = "take_photo_time";
    /**
     * 复制提示
     */
    private final String COPY_TIP = "function_tip";
    /**
     * 切换模式提示
     */
    private final String CHANGE_MODE_TIP = "change_mode_tip";

    /**
     * 第一次使用本应用
     */
    private final String FIRST_USE_APP = "first_use_app";

    /**
     * 用户的objectID
     */
    private final String USER_OBJECT_ID = "user_object_id";

    /**
     * 保存用户的剩余使用次数
     */
    private final String USER_LEAVE_OCR_TIMES = "user_leave_ocr_times";

    /**
     * 更新查询时间
     */
    private final String UPDATE_QUERY_TIME = "update_query_time";

    /**
     * 半强制更新
     */
    private final String HALF_FORCE_UPDATE = "half_force_update";

    /**
     * 显示照片选取提示
     */
    private final String PHOTO_SELECT_TIP = "photo_select_tip";

    /**
     * 设置拍照的时间
     */
    public void setTakePhotoTime(Long time) {
        setLong(TAKE_PHOTO_TIME, time);
    }

    public Long getTakePhotoTime() {
        return getLong(TAKE_PHOTO_TIME);
    }

    /**
     * 设置是否复制提示
     */
    public void setCopyTip(Boolean imageTip) {
        setBoolean(COPY_TIP, imageTip);
    }

    /**
     * 获取是否复制提示
     */
    public Boolean getCopyTip() {
        return getBoolean(COPY_TIP, true);
    }

    /**
     * 设置是否切换模式提示
     */
    public void setChangeModeTip(Boolean imageTip) {
        setBoolean(CHANGE_MODE_TIP, imageTip);
    }

    /**
     * 获取是否切换模式提示
     */
    public Boolean getChangeModeTip() {
        return getBoolean(CHANGE_MODE_TIP, true);
    }

    /**
     * 设置第一次使用本应用
     *
     * @param first
     */
    public void setFirstUseApp(boolean first) {
        setBoolean(FIRST_USE_APP, first);
    }

    public Boolean getFirstUseApp() {
        return getBoolean(FIRST_USE_APP, true);
    }

    /**
     * 设置用户objectid
     */
    public void setUserObjectId(String objectId) {
        setString(USER_OBJECT_ID, objectId);
    }

    public String getUserObjectId() {
        return getString(USER_OBJECT_ID, "");
    }

    /**
     * 本地保存用户剩余次数
     */
    public void setUserLeaveOcrTimes(int times) {
        setInt(USER_LEAVE_OCR_TIMES, times);
    }

    public int getUserLeaveOcrTimes() {
        return getInt(USER_LEAVE_OCR_TIMES, 0);
    }

    /**
     * 设置更新查询时间
     */
    public void setUpdateQueryTime(long time) {
        setLong(UPDATE_QUERY_TIME, time);
    }

    public long getUpdateQueryTime() {
        return getLong(UPDATE_QUERY_TIME, 0);
    }

    /**
     * 半强制更新
     */
    public void setHalfForceUpdate(boolean show) {
        setBoolean(HALF_FORCE_UPDATE, show);
    }

    public boolean getHalfForceUpdate() {
        return getBoolean(HALF_FORCE_UPDATE, false);
    }

    /**
     * 设置照片选择提示
     */
    public void setPhotoSelectTip(Boolean tip) {
        setBoolean(PHOTO_SELECT_TIP, tip);
    }

    /**
     * 获取照片选择提示
     */
    public Boolean getPhotoSelectTip() {
        return getBoolean(PHOTO_SELECT_TIP, false);
    }
}
