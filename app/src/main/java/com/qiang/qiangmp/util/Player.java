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

    private static MediaPlayer mediaPlayer;


    public Player() {
        mediaPlayer = new MediaPlayer();
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mediaPlayer.setAudioAttributes(attributes);
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

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
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
