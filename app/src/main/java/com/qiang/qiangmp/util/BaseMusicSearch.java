package com.qiang.qiangmp.util;


/**
 * @author xiaoq
 * @date 19-1-23
 */
public abstract class BaseMusicSearch {
    /** url路径 */
    String path = null;
    /** 搜索类型 */
    private static final String TYPE = "song";
    /** 搜索关键词 */
    String s = null;
    /** 请求秘钥 */
    private static final String KEY = "579621905";
    /** 搜索结果数量 */
    private static final String LIMIT = "20";
    /** 搜索结果页数 */
    private static final String OFFSET = "0";

    @Override
    public String toString() {
        return path + "?key=" + KEY + "&s=" + s +
                "&limit=" + LIMIT + "&offset=" +
                OFFSET + "&type=" + TYPE;
    }
}
