package com.qiang.qiangmp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.qiang.qiangmp.QiangMpApplication;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.fragment.PlayingControlBarFragment;
import com.qiang.qiangmp.util.DbUtil;
import com.qiang.qiangmp.util.Player;
import com.qiang.qiangmp.util.QiangMPConstants;
import com.qiang.qiangmp.util.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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
        Song song = QiangMpApplication.globalSongList.get(QiangMpApplication.globalSongPos);
        String name = song.getName();
        String singer = song.getSinger();
        String url = song.getUrl();
        if (DbUtil.playedSongCount == QiangMPConstants.MAX_SONG_PLAY_COUNT) {
            DbUtil.deleteOnSong(1);
            DbUtil.insertOnSong(QiangMPConstants.MAX_SONG_PLAY_COUNT, name, singer, url);
        } else {
            DbUtil.insertOnSong(name, singer, url);
            DbUtil.playedSongCount++;
        }
    }


    private void startNewMusic(Intent intent) {
        String url = intent.getStringExtra("url");
        String name = intent.getStringExtra("name");
        String singer = intent.getStringExtra("singer");
        QiangMpApplication.player.setName(singer);
        QiangMpApplication.player.setSinger(singer);
        QiangMpApplication.player.playUrl(url);
        Player.mediaPlayer.setOnPreparedListener(mp -> {
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            int time = Player.mediaPlayer.getDuration();
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            sendBroadcast(i);
//            i = new Intent(QiangMPConstants.ACTION_SONG_AND_SINGER);
//            i.putExtra("name", name);
//            i.putExtra("singer", singer);
//            i.putExtra("serial_num", QiangMPConstants.ACTION_SONG_AND_SINGER);
//            sendBroadcast(i);
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
            int lastPosition = 0;
            while (true) {
                if (Player.mediaPlayer.isPlaying()) {
                    int time = Player.mediaPlayer.getCurrentPosition();
                    if (time < lastPosition) {
                        break;
                    } else {
                        lastPosition = time;
                    }
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
}
