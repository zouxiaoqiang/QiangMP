package com.qiang.qiangmp.util;

/**
 * @author xiaoqiang
 * @date 19-3-2
 */
public interface QiangMPConstants {

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

    /**
     * 获取QQ音乐热门歌单的url
     */
    String URL_QQ_SONG_LIST = "https://api.bzqll.com/music/tencent/hotSongList?key=579621905&categoryId=10000000&sortId=3&limit=14";

    /**
     * 获取网易云音乐热门歌单的url
     */
    String URL_NETEASE_SONG_LIST = "https://api.bzqll.com/music/netease/hotSongList?key=579621905&cat=%E5%85%A8%E9%83%A8&limit=15&offset=0";
}
