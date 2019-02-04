package com.qiang.qiangmp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.qiang.qiangmp.util.Player;

import static com.qiang.qiangmp.activity.SearchActivity.CURRENT_TIME_TYPE;
import static com.qiang.qiangmp.activity.SearchActivity.DURATION_TYPE;
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
        return super.onStartCommand(intent, flags, startId);
    }

    private void startNewMusic(Intent intent) {
        String url = intent.getStringExtra("song_url");
        int position = intent.getIntExtra("position", 0);
        if (player == null) {
            player = new Player();
        }
        player.playUrl(url);
        Player.mediaPlayer.setOnPreparedListener(mp -> {
            player.start();
            int time = Player.mediaPlayer.getDuration();
            Intent i = new Intent("com.qiang.qiangmp.musictime");
            i.putExtra("time", time);
            i.putExtra("type", DURATION_TYPE);
            i.putExtra("position", position);
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
                    Intent intent = new Intent("com.qiang.qiangmp.musictime");
                    intent.putExtra("time", time);
                    intent.putExtra("type", CURRENT_TIME_TYPE);
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
        Log.d(TAG, "onDestroy");
        player.stop();
        player = null;
        super.onDestroy();
    }
}
