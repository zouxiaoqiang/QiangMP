package com.qiang.qiangmp.util.load_web_image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.qiang.qiangmp.util.MyLog;
import com.qiang.qiangmp.util.ThreadFactoryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步加载图片组件
 *
 * @author xiaoqiang
 * @date 19-3-11
 */
public class AsnycImageLoader {
    private MemoryCache mMemoryCache;
    private FileCache mFileCache;
    private ExecutorService mExecutorService;

    /**
     * 记录已经加载图片的ImageView
     */
    private Map<ImageView, String> mapImageViews = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 保存正在加载图片的url, 操作时需要加锁
     */
    private final List<LoadPhotoTask> mTaskQueue = new ArrayList<>();

    public AsnycImageLoader(Context context, MemoryCache memoryCache, FileCache fileCache) {
        mMemoryCache = memoryCache;
        mFileCache = fileCache;
        ThreadFactory tf = new ThreadFactoryBuilder()
                .setNamePrefix("load_pic")
                .setDaemon(false)
                .build();
        mExecutorService = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), tf);
    }

    /**
     * 根据url加载相应的图片
     *
     * @param url
     * @return 先从内存中获取图片，若没有，则异步从文件中获取图片，还是没有，就只能从网络端下载图片
     */
    public Bitmap loadBitmap(ImageView imageView, String url) {
        mapImageViews.put(imageView, url);
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap == null) {
            enqueueLoadPhoto(url, imageView);
        }
        return bitmap;
    }

    private void enqueueLoadPhoto(String url, ImageView imageView) {
        if (!isTaskExisted(url)) {
            LoadPhotoTask task = new LoadPhotoTask(url, imageView);
            synchronized (mTaskQueue) {
                mTaskQueue.add(task);
            }
            mExecutorService.execute(task);
        }

    }

    /**
     * 判断加载队列中是否存在该任务
     */
    private boolean isTaskExisted(String url) {
        if (url == null) {
            return false;
        }
        synchronized (mTaskQueue) {
            int size = mTaskQueue.size();
            for (int i = 0; i < size; i++) {
                LoadPhotoTask task = mTaskQueue.get(i);
                if (task != null && task.getUrl().equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 该ImageView是否已经加载过图片
     *
     * @param imageView
     * @param url
     * @return
     */
    private boolean imageViewReused(ImageView imageView, String url) {
        String tag = mapImageViews.get(imageView);
        return tag == null || !tag.equals(url);
    }

    private void removeTask(LoadPhotoTask task) {
        synchronized (mTaskQueue) {
            mTaskQueue.remove(task);
        }
    }

    /**
     * 从缓存文件或者网络端获取图片
     *
     * @param url
     * @return
     */
    private Bitmap getBitmapByUrl(String url) {
        File f = mFileCache.getFile(url);
        Bitmap b = ImageUtil.decodeFile(f);
        if (b != null) {
            return b;
        }
        return ImageUtil.loadBitmapFromWeb(url, f);
    }

    class LoadPhotoTask implements Runnable {
        private String url;
        private ImageView imageView;

        LoadPhotoTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            if (imageViewReused(imageView, url)) {
                removeTask(this);
            } else {
                Bitmap bmp = getBitmapByUrl(url);
                mMemoryCache.put(url, bmp);
                if (!imageViewReused(imageView, url)) {
                    BitmapDisplay bd = new BitmapDisplay(bmp, imageView, url);
                    Activity a = (Activity) imageView.getContext();
                    a.runOnUiThread(bd);
                }
                removeTask(this);
            }
        }

        String getUrl() {
            return url;
        }
    }

    class BitmapDisplay implements Runnable {
        private Bitmap bitmap;
        private ImageView imageView;
        private String url;

        BitmapDisplay(Bitmap b, ImageView imageView, String url) {
            this.bitmap = b;
            this.imageView = imageView;
            this.url = url;
        }

        @Override
        public void run() {
            if (!imageViewReused(imageView, url) && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
