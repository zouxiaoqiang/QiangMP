package com.qiang.qiangmp.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.qiang.qiangmp.activity.MainActivity;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.fragment.PlayingControlBarFragment;
import com.qiang.qiangmp.util.DbUtil;
import com.qiang.qiangmp.util.MyLog;
import com.qiang.qiangmp.util.Player;
import com.qiang.qiangmp.util.QiangMPConstants;
import com.qiang.qiangmp.util.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.qiang.qiangmp.activity.SearchActivity.player;

/**
 * @author xiaoq
 * @date 19-1-26
 */
public class MusicPlayService extends Service {
    private static final String TAG = "MusicPlayService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNewMusic(intent);
        storeSongOnThread();
        return super.onStartCommand(intent, flags, startId);
    }

    private void storeSongOnThread() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNamePrefix("operate-database-thread")
                .setDaemon(false)
                .build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory);
        singleThreadPool.execute(this::storePlayedSong);
        singleThreadPool.shutdown();
    }

    /**
     * 将最近播放歌曲写入数据库
     */
    private void storePlayedSong() {
        String sql;
        SQLiteDatabase db = DbUtil.dbHelper.getWritableDatabase();
        Song song = PlayingControlBarFragment.globalSongList.get(PlayingControlBarFragment.globalSongPos);
        if (DbUtil.playedSongCount == QiangMPConstants.MAX_SONG_PLAY_COUNT) {
            sql = "delete from Song where id = 1";
            db.execSQL(sql);
            sql = "insert into Song (id, name, singer, url) values(?, ?, ?, ?)";
            db.execSQL(sql, new String[] {String.valueOf(QiangMPConstants.MAX_SONG_PLAY_COUNT), song.getName(), song.getSinger(), song.getUrl()});
        } else {
            sql = "insert into Song (name, singer, url) values(?, ?, ?)";
            db.execSQL(sql, new String[] {song.getName(), song.getSinger(), song.getUrl()});
            DbUtil.playedSongCount++;
        }
        sql = "select * from Song";
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String singer = cursor.getString(cursor.getColumnIndex("singer"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                MyLog.d("", name);
                MyLog.d("", singer);
                MyLog.d("", url);
                MyLog.d("", "##############");
            } while (cursor.moveToNext());
        }
    }


    private void startNewMusic(Intent intent) {
        String url = intent.getStringExtra("song_url");
        if (player == null) {
            player = new Player();
        }
        player.playUrl(url);
        Player.mediaPlayer.setOnPreparedListener(mp -> {
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            int time = Player.mediaPlayer.getDuration();
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            sendBroadcast(i);
            i = new Intent(QiangMPConstants.ACTION_SONG_PLAY);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_PLAY);
            sendBroadcast(i);
            new MusicTimeThread().start();
        });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MusicTimeThread extends Thread {
        @Override
        public void run() {
            while (player != null) {
                if (Player.mediaPlayer.isPlaying()) {
                    int time = Player.mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
                    intent.putExtra("time", time);
                    intent.putExtra("serial_num", QiangMPConstants.NUM_SONG_CURRENT_POSITION);
                    sendBroadcast(intent);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        MyLog.d("MusicPlaService", "onDestroy");
        player.stop();
        player = null;
        super.onDestroy();
    }
}
