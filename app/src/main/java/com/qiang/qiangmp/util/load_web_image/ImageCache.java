package com.qiang.qiangmp.util.load_web_image;

import android.graphics.Bitmap;

/**
 * @author xiaoqiang
 * @date 19-4-24
 * @deprecated
 */
public interface ImageCache {
    /**
     * 根据给定的url获取图片资源
     */
    Bitmap get(String url);

    /**
     * 存储图片资源
     */
    void put(String url, Bitmap bitmap);

    /**
     * 清除图片资源
     */
    void clear();
}
