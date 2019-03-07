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

    private static final int VERSION = 2;

    private static final String DATABASE = "QiangMP.db";

    private static final String CREATE_SONG = "create table Song ("
            + "id integer primary key autoincrement,"
            + "name text not null,"
            + "singer text default '未知',"
            + "url text not null)";

    private static final String RESET_ID = "create trigger reset_id " +
            "after delete on Song " +
            "begin " +
            "update Song set id=id-1; " +
            "end";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SONG);
        db.execSQL(RESET_ID);
        Toast.makeText(mContext, "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Song");
        db.execSQL("drop trigger if exists reset_id");
        onCreate(db);
    }
}
