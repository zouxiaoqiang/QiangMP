package com.qiang.qiangmp.util.load_web_image;

import android.content.Context;
import android.os.Environment;

import com.qiang.qiangmp.util.QiangMPConstants;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.qiang.qiangmp.util.QiangMPConstants.COUNT_FILES_SONG_LIST_IMAGE;

/**
 * 二级缓存
 *
 * @author xiaoqiang
 * @date 19-3-11
 */
public class FileCache {
    /**
     * 缓存目录
     */
    private File mCacheDir;

    /**
     * 创建图片缓存目录，如果有SD卡，则使用SD，否则使用系统自带缓存目录
     *
     * @param cacheDir 图片缓存的一级目录
     */
    public FileCache(Context context, File cacheDir, String dir) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCacheDir = new File(cacheDir, dir);
        } else {
            mCacheDir = context.getCacheDir();
        }
        // 如果目录不存在，则创建目录
        // 如果目录下的文件数量超过给定的最大值，则清空文件夹
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        } else if (mCacheDir.listFiles().length > COUNT_FILES_SONG_LIST_IMAGE) {
            mCacheDir.delete();
        }
    }

    File getFile(String url) {
        File f = null;
        try {
            String filename = URLEncoder.encode(url, "utf-8");
            f = new File(mCacheDir, filename);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void clear() {
        File[] files = mCacheDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }
}
