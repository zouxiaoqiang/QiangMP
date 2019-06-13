package com.qiang.qiangmp.util.load_web_image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.qiang.qiangmp.util.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.qiang.qiangmp.util.QiangMPConstants.COUNT_FILES_SONG_LIST_IMAGE;

/**
 * 二级缓存
 *
 * @author xiaoqiang
 * @date 19-3-11
 * @deprecated 使用了第三方框架DiskLruCache代替
 */
public class FileCache implements ImageCache {
    /**
     * 缓存目录
     */
    private File mCacheDir;

    /**
     * 创建图片缓存目录，如果有SD卡，则使用SD，否则使用系统自带缓存目录
     *
     * @param cacheDir 图片缓存的一级目录
     */
    FileCache(Context context, File cacheDir, String dir) {
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
            clear();
        }
    }

    @Override
    public Bitmap get(String url) {
        String filename = resolveFilename(url);
        return BitmapFactory.decodeFile(mCacheDir + "/" + filename);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        String filename = resolveFilename(url);
        MyLog.d("dd", filename);
        try(FileOutputStream fos = new FileOutputStream(new File(mCacheDir, filename))) {
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析url获得文件名
     */
    private String resolveFilename(String url) {
        Uri uri = Uri.parse(url);
        String filename = uri.getPath();
        return filename == null ? "" : filename.replace("/", "_");
    }

    @Override
    public void clear() {
        File[] files = mCacheDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }
}
