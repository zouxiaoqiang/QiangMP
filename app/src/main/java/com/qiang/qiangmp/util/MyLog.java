package com.qiang.qiangmp.util;

import android.util.Log;

/**
 * @author xiaoqiang
 * @date 19-3-2
 */
public class MyLog {
    private static final String TAG = "QiangMP";

    public static int v(String tag, String msg) {
        return Log.v(TAG + "_" + tag, msg);
    }

    public static int d(String tag, String msg) {
        return Log.d(TAG + "_" + tag, msg);
    }

    public static int i(String tag, String msg) {
        return Log.i(TAG + "_" + tag, msg);
    }

    public static int w(String tag, String msg) {
        return Log.w(TAG + "_" + tag, msg);
    }

    public static int e(String tag, String msg) {
        return Log.e(TAG + "_" + tag, msg);
    }
}
