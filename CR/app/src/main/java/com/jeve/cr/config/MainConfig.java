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
}
