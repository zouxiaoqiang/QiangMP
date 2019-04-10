package com.qiang.qiangmp.util.load_web_image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.qiang.qiangmp.util.MyLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author xiaoqiang
 * @date 19-3-11
 */
class ImageUtil {
    static Bitmap decodeFile(File f) {
        try {
            return BitmapFactory.decodeStream(new FileInputStream(f), null, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static Bitmap loadBitmapFromWeb(String url, File f) {
        HttpURLConnection conn = null;
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setInstanceFollowRedirects(true);
            try (InputStream is = conn.getInputStream();
                 OutputStream os = new FileOutputStream(f)) {
                copyStream(is, os);
            }
            bitmap = decodeFile(f);
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

    /**
     * 将图片缓存到磁盘
     *
     * @param is
     * @param os
     */
    private static void copyStream(InputStream is, OutputStream os) {
        final int bufferSize = 1024;
        try {
            byte[] bytes = new byte[bufferSize];
            while (true) {
                int count = is.read(bytes, 0, bufferSize);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
