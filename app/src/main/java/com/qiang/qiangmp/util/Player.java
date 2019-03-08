package com.qiang.qiangmp.util;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;

import java.io.IOException;

import static com.qiang.qiangmp.QiangMpApplication.globalSongList;
import static com.qiang.qiangmp.QiangMpApplication.globalSongPos;
import static com.qiang.qiangmp.QiangMpApplication.mIsPause;


/**
 * @author xiaoqiang
 */
public class Player {

    private MediaPlayer mediaPlayer;
    private String name = "";
    private String singer = "";
    /**
     * 最近播放列表中的歌曲数量
     */
    private int playedSongCount;

    private boolean isPrepare = false;

    public boolean isPrepare() {
        return isPrepare;
    }

    public Player() {
        initMediaPlayer();
    }

    private void initMediaPlayer() {
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

    public int getPlayedSongCount() {
        return playedSongCount;
    }

    public void setPlayedSongCount(int playedSongCount) {
        this.playedSongCount = playedSongCount;
    }

    public void start() {
        mediaPlayer.start();
    }

    public void playUrl(String url, Context context) {
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            MyLog.d("Player_playUrl", "setDataSource failed. MSG: " + e.getMessage());
            return;
        }
        mediaPlayer.prepareAsync();
        isPrepare = true;
        mediaPlayerListen(context);
    }

    private void mediaPlayerListen(Context context) {
        if (context instanceof MusicPlayService) {
            mediaPlayerListenOnMusicPlayService((MusicPlayService) context);
        }
        mediaPlayer.setOnCompletionListener(mp -> {
            isPrepare = false;
            if (globalSongList != null && !globalSongList.isEmpty()) {
                globalSongPos = (globalSongPos + 1) % globalSongList.size();
                Song song = globalSongList.get(globalSongPos);
                String songUrl = song.getUrl();
                name = song.getName();
                singer = song.getSinger();
                playUrl(songUrl, context);
            } else {
                mediaPlayer.reset();
                mIsPause = true;
                Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
                i.putExtra("time", 0);
                i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
                context.sendBroadcast(i);
                i = new Intent(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
                i.putExtra("time", 0);
                i.putExtra("serial_num", QiangMPConstants.NUM_SONG_CURRENT_POSITION);
                context.sendBroadcast(i);
                i = new Intent(QiangMPConstants.ACTION_SONG_PLAY);
                mIsPause = true;
                i.putExtra("serial_num", QiangMPConstants.NUM_SONG_PLAY);
                context.sendBroadcast(i);
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            mediaPlayer.stop();
            isPrepare = false;
            mediaPlayer.reset();
            mIsPause = true;
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            i.putExtra("time", 0);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            context.sendBroadcast(i);
            i = new Intent(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
            i.putExtra("time", 0);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_CURRENT_POSITION);
            context.sendBroadcast(i);
            i = new Intent(QiangMPConstants.ACTION_SONG_PLAY);
            mIsPause = true;
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_PLAY);
            context.sendBroadcast(i);
            return false;
        });
    }

    private void mediaPlayerListenOnMusicPlayService(MusicPlayService musicPlayService) {
        mediaPlayer.setOnPreparedListener(mp -> {
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            int time = mediaPlayer.getDuration();
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            musicPlayService.sendBroadcast(i);
            i = new Intent(QiangMPConstants.ACTION_SONG_PLAY);
            mIsPause = false;
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_PLAY);
            musicPlayService.sendBroadcast(i);
            musicPlayService.startMusicTimeThread();
        });
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    private void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }
}
