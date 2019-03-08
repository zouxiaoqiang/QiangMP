package com.qiang.qiangmp.util;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.qiang.qiangmp.QiangMpApplication.dbHelper;

/**
 * @author xiaoqiang
 * 19-3-6
 */
public class DbUtil {


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

    public static void clearAllOnSong() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "delete from sqlite_sequence where name = 'Song'";
        db.execSQL(sql);
        sql = "delete from Song";
        db.execSQL(sql);
    }

    public static int queryCountOnSOng() {
        String sql = "select count(*) from Song";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }
}
