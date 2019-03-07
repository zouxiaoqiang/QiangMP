package com.qiang.qiangmp.util;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import com.qiang.qiangmp.fragment.PlayingControlBarFragment;

import java.io.IOException;


/**
 * @author xiaoqiang
 */
public class Player {

    public static MediaPlayer mediaPlayer;
    private String name = "";
    private String singer = "";
    /**
     * 当前应用是否有缓存的歌曲可以播放
     */
    private boolean isCacheSong = false;

    public Player() {
        mediaPlayer = new MediaPlayer();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(attributes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public boolean isCacheSong() {
        return isCacheSong;
    }

    public void setCacheSong(boolean cacheSong) {
        isCacheSong = cacheSong;
    }

    public void start() {
        mediaPlayer.start();
    }

    public void playUrl(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
