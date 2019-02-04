package com.qiang.qiangmp.util;


/**
 * @author xiaoq
 * @date 19-1-23
 */
public class QQMusicSearch extends BaseMusicSearch {
    public QQMusicSearch(String s) {
        this.s = s;
        this.path = "https://api.bzqll.com/music/tencent/search";
    }

}
