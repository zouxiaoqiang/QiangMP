package com.qiang.qiangmp.util;

/**
 * @author xiaoq
 * @date 19-1-28
 */
public class KuGouMusicSearch extends BaseMusicSearch {
    public KuGouMusicSearch(String s) {
        this.s = s;
        this.path = "https://api.bzqll.com/music/kugou/search";
    }
}
