package com.qiang.qiangmp.util.load_web_image;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一级缓存
 *
 * @author xiaoqiang
 * @date 19-3-11
 * @deprecated 使用官方API LruCache代替
 */
public class MemoryCache implements ImageCache {
    /**
     * 最大缓存图片数量
     */
    private static final int MAX_CACHE_CAPACITY = 20;

    /**
     * 保证内存足够的情况下不会被回收
     */
    private HashMap<String, SoftReference<Bitmap>> mCacheMap = new LinkedHashMap<String, SoftReference<Bitmap>>() {
        private static final long serialVersionUID = 1L;

        @Override
        // 当缓存数量已满，清除最早放入缓存的资源
        protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
            return size() > MAX_CACHE_CAPACITY;
        }
    };

    /**
     * @param url 图片url
     * @return 放回缓存中的图片
     */
    @Override
    public Bitmap get(String url) {
        if (!mCacheMap.containsKey(url)) {
            return null;
        }
        SoftReference<Bitmap> ref = mCacheMap.get(url);
        return ref != null ? ref.get() : null;
    }

    /**
     * 将图片加入缓存
     */
    @Override
    public void put(String url, Bitmap bitmap) {
        mCacheMap.put(url, new SoftReference<>(bitmap));
    }

    /**
     * 清除所有缓存
     */
    @Override
    public void clear() {
        for (Map.Entry<String, SoftReference<Bitmap>> entry : mCacheMap.entrySet()) {
            SoftReference<Bitmap> ref = entry.getValue();
            if (ref != null) {
                Bitmap bmp = ref.get();
                if (bmp != null) {
                    bmp.recycle();
                }
            }
        }
        mCacheMap.clear();
    }
}
