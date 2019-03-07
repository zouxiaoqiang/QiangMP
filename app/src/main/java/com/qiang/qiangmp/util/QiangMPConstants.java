package com.qiang.qiangmp.util;

/**
 * @author xiaoqiang
 * @date 19-3-2
 */
public interface QiangMPConstants {

    /**
     * 歌名和歌手名
     */
    String ACTION_SONG_AND_SINGER = "com.qiang.qiangmp.song_and_singer";

    /**
     * 相应的ACTION编号
     */
    int NUM_SONG_ADN_SINGER = 0;

    /**
     * 歌曲总时长
     */
    String ACTION_SONG_DURATION = "com.qiang.qiangmp.song_duration";

    /**
     * 相应的ACTION编号
     */
    int NUM_SONG_DURATION = 1;

    /**
     * 歌曲当前时长
     */
    String ACTION_SONG_CURRENT_POSITION = "com.qiang.qiangmp.song_current_position";

    /**
     * 相应的ACTION编号
     */
    int NUM_SONG_CURRENT_POSITION = 2;

    /**
     * 控制歌曲播放
     */
    String ACTION_SONG_PLAY = "com.qiang.qiangmp.song_play";

    /**
     * 相应的ACTION编号
     */
    int NUM_SONG_PLAY = 3;

    /**
     * 最近播放歌曲的最大数量
     */
    int MAX_SONG_PLAY_COUNT = 100;
}
