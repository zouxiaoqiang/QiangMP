package com.qiang.qiangmp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * @author xiaoqiang
 * @date 19-3-4
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static final String CTEATE_SONG = "create table Song ("
            + "id integer primary key autoincrement,"
            + "name text not null,"
            + "singer text default '未知',"
            + "url text not null)";

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CTEATE_SONG);
        Toast.makeText(mContext, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Song");
        onCreate(db);
    }
}
