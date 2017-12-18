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
    private static final String TAKE_PHOTO_TIME = "take_photo_time";

    /**
     * 添加图片提示
     */
    private static final String ADD_IMAGE_TIP = "add_image_tip";
    /**
     * 功能按键提示
     */
    private static final String FUNCTION_TIP = "function_tip";

    /**
     * 设置拍照的时间
     *
     * @param time
     */
    public void setTakePhotoTime(Long time) {
        setLong(TAKE_PHOTO_TIME, time);
    }

    public Long getTakePhotoTime() {
        return getLong(TAKE_PHOTO_TIME);
    }

    /**
     * 设置是否图片提示
     */
    public void setAddImageTip(Boolean imageTip) {
        setBoolean(ADD_IMAGE_TIP, imageTip);
    }

    /**
     * 获取是否图片提示
     */
    public Boolean getAddImageTip() {
        return getBoolean(ADD_IMAGE_TIP, false);
    }

    /**
     * 设置是否功能提示
     */
    public void setFunctionTip(Boolean imageTip) {
        setBoolean(FUNCTION_TIP, imageTip);
    }

    /**
     * 获取是否功能提示
     */
    public Boolean getFunctionTip() {
        return getBoolean(FUNCTION_TIP, false);
    }
}
