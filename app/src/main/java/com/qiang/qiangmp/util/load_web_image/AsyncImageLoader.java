package com.qiang.qiangmp.util.load_web_image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.qiang.qiangmp.util.ThreadFactoryBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
public class AsyncImageLoader {
    private ImageCache mImageCache;
    private ExecutorService mExecutorService;

    /**
     * 记录已经加载图片的ImageView
     */
    private Map<ImageView, String> mapImageViews = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * 保存正在加载图片的url, 操作时需要加锁
     */
    private final List<LoadPhotoTask> mTaskQueue = new ArrayList<>();

    public AsyncImageLoader() {
        ThreadFactory tf = new ThreadFactoryBuilder()
                .setNamePrefix("load_pic")
                .setDaemon(false)
                .build();
        mExecutorService = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), tf);
    }

    /**
     * 依赖注入
     */
    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    /**
     * 根据url加载相应的图片
     * @return 先从内存或文件中获取图片，若没有，异步从网络端下载图片
     */
    public Bitmap loadBitmap(ImageView imageView, String url) {
        mapImageViews.put(imageView, url);
        Bitmap bitmap = mImageCache.get(url);
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
     * 从网络端加载图片
     */
    private Bitmap loadBitmapFromWeb(String url) {
        HttpURLConnection conn = null;
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());

            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
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
                Bitmap bmp = loadBitmapFromWeb(url);
                if (!imageViewReused(imageView, url)) {
                    BitmapDisplay bd = new BitmapDisplay(bmp, imageView, url);
                    Activity a = (Activity) imageView.getContext();
                    a.runOnUiThread(bd);
                }
                removeTask(this);
                mImageCache.put(url, bmp);
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

    /**
     * 释放资源
     */
    public void destroy() {
        mImageCache.clear();
        mapImageViews.clear();
        mapImageViews = null;
        mTaskQueue.clear();
        mExecutorService.shutdown();
        mExecutorService = null;
    }
}
