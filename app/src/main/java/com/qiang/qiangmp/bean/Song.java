package com.qiang.qiangmp.bean;

/**
 * @author xiaoq
 * @date 19-1-23
 */
public class Song {
    private String id;
    private String name;
    /**
     * 歌曲时长，单位为s
     */
    private int time;
    private String singer;
    /**
     * 歌曲地址
     */
    private String url;
    /**
     * 封面图片地址
     */
    private String pic;
    /**
     * 歌词地址
     */
    private String lrc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLrc() {
        return lrc;
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
    }
}
