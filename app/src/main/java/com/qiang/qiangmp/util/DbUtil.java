package com.qiang.qiangmp.util;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author xiaoqiang
 * 19-3-6
 */
public class DbUtil {

    @SuppressLint("StaticFieldLeak")
    public static MyDatabaseHelper dbHelper;

    public static int playedSongCount;

    /**
     * @param id delete by id on Song
     */
    public static void deleteOnSong(int id) {
        String sql = "delete from Song where id = ?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{"1"});
    }


    public static void insertOnSong(int id, String name, String singer, String url) {
        String sql = "insert into Song (id, name, singer, url) values(?, ?, ?, ?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{String.valueOf(id), name, singer, url});
    }

    public static void insertOnSong(String name, String singer, String url) {
        String sql = "insert into Song (name, singer, url) values(?, ?, ?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{name, singer, url});
    }

    public static Cursor queryOnSong() {
        String sql = "select name, singer, url from Song";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery(sql, null);
    }
}
