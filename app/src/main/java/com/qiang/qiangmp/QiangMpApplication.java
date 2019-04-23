package com.qiang.qiangmp;

import android.annotation.SuppressLint;
import android.app.Application;

import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.util.DbUtil;
import com.qiang.qiangmp.util.MyDatabaseHelper;
import com.qiang.qiangmp.util.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-7
 */
public class QiangMpApplication extends Application {
    /**
     * 记录暂停状态
     */
    public static boolean mIsPause = true;

    /**
     * 当前歌曲位置
     */
    public static int globalSongPos = -1;

    /**
     * 当前缓存歌曲列表， 默认为空
     */
    public static List<Song> globalSongList = new ArrayList<>();

    /**
     * 本地数据库
     */
    @SuppressLint("StaticFieldLeak")
    public static MyDatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new MyDatabaseHelper(this);
        dbHelper.getWritableDatabase();
        Player.getInstance().setPlayedSongCount(DbUtil.queryCountOnSong());
    }
}
