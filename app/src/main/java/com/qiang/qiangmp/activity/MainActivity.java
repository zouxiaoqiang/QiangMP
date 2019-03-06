package com.qiang.qiangmp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.util.DbUtil;
import com.qiang.qiangmp.util.MyDatabaseHelper;


/**
 * @author xiaoq
 * @date 19-1-8
 */
public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;

    private int playedSongCount() {
        String sqlQuery = "select count(*) from Song";
        SQLiteDatabase db = DbUtil.dbHelper.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor = db.rawQuery(sqlQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbUtil.dbHelper = new MyDatabaseHelper(this);
        DbUtil.dbHelper.getWritableDatabase();
        DbUtil.playedSongCount = playedSongCount();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_toolbar_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                default:
            }
            return true;
        });
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                0, 0);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.left_nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> false);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
