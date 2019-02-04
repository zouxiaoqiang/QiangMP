package com.qiang.qiangmp.util;

/**
 * @author xiaoq
 * @date 19-1-23
 */
public abstract class BaseMusicSearch {
    /** url路径 */
    String path = null;
    /** 搜索类型 */
    static final String type = "song";
    /** 搜索关键词 */
    String s = null;
    /** 请求秘钥 */
    static final String key = "579621905";
    /** 搜索结果数量 */
    static final String limit = "20";
    /** 搜索结果页数 */
    static final String offset = "0";

    @Override
    public String toString() {
        return path + "?key=" + key + "&s=" + s +
                "&limit=" + limit + "&offset=" +
                offset + "&type=" + type;
    }
}
