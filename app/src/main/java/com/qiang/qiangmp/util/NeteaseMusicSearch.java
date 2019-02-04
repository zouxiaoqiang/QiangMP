package com.qiang.qiangmp.util;

/**
 * @author xiaoq
 * @date 19-1-28
 */
public class NeteaseMusicSearch extends BaseMusicSearch {
    public NeteaseMusicSearch(String s) {
        this.path = "https://api.bzqll.com/music/netease/search";
        this.s = s;
    }
}
