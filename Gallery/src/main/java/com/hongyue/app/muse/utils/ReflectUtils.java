package com.hongyue.app.muse.utils;

import com.hongyue.app.muse.process.Processor;

/**
 * For more information, you can visit https://github.com/guoxiaoxing or contact me by
 * guoxiaoxingse@163.com.
 *
 * @author guoxiaoxing
 * @since 2017/8/3 下午4:08
 */
public class ReflectUtils {

    /**
     * 图片压缩
     */
    public static final String PictureCompressProcessor = "com.guoxiaoxing.phoenix.compress.picture.PictureCompressProcessor";

    /**
     * 图片编辑
     */
    public static final String PictureEditProcessor = "com.guoxiaoxing.phoenix.picture.edit.PictureEditProcessor";

    /**
     * 视频压缩
     */
    public static final String VideoCompressProcessor = "com.guoxiaoxing.phoenix.compress.video.soft.VideoCompressProcessor";

    /**
     * 图片/视频选择
     */
    public static final String Phoenix = "com.guoxiaoxing.phoenix.picker.Phoenix";

    public static Processor loadProcessor(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (Processor) clazz.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}
