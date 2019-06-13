package com.qiang.qiangmp.util.load_web_image;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

/**
 * @author xiaoqiang
 * @date 19-4-24
 * @deprecated
 */
public class MemoryAndFileCache implements ImageCache {
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;

    public MemoryAndFileCache(Context context, File cacheDir, String dir) {
        mMemoryCache = new MemoryCache();
        mFileCache = new FileCache(context, cacheDir, dir);
    }

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            bitmap = mFileCache.get(url);
        }
        return bitmap;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        mMemoryCache.put(url, bitmap);
        mFileCache.put(url, bitmap);
    }

    @Override
    public void clear() {
        mMemoryCache.clear();
    }
}
