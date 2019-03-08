package com.qiang.qiangmp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.qiang.qiangmp.QiangMpApplication;
import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.RecentSongAdapter;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.util.DbUtil;
import com.qiang.qiangmp.util.MyLog;

import java.util.ArrayList;
import java.util.List;

import static com.qiang.qiangmp.QiangMpApplication.player;


/**
 * @author xiaoq
 * @date 19-1-8
 */
public class MainActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    /**
     * recent played song's name and singer
     */
    private List<Song> mRecentSongs = new ArrayList<>();

    private RecentSongAdapter mRecentSongAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        configRecycleView();
        listen();
        MyLog.d("MainActivity", "mIsPause: " + QiangMpApplication.mIsPause);
        MyLog.d("MainActivity", "pos: " + QiangMpApplication.globalSongPos);
        MyLog.d("MainActivity", "list: " + QiangMpApplication.globalSongList.size());
        MyLog.d("MainActivity", "player : " + player.getName());
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.main_toolbar_menu);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.left_nav_view);
    }

    private void listen() {
        mToolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(intent);
                    break;
                default:
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                0, 0) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateRecentSong();
            }
        };
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);

        mNavigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.item_clear_all:
                    new ClearAllOnSongThread().start();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    player.setPlayedSongCount(0);
                    updateRecentSong();
                    break;
                default:
            }
            return false;
        });
    }

    private class ClearAllOnSongThread extends Thread {
        @Override
        public void run() {
            DbUtil.clearAllOnSong();
        }
    }

    private void configRecycleView() {
        mRecentSongAdapter = new RecentSongAdapter(this, mRecentSongs);
        View headerView = mNavigationView.inflateHeaderView(R.layout.main_left_nav_view_header);
        RecyclerView recyclerView = headerView.findViewById(R.id.navigation_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecentSongAdapter);
    }

    /**
     * 从数据库中更新最近播放歌曲
     */
    private void updateRecentSong() {
        mRecentSongs.clear();
        Cursor cursor = DbUtil.queryOnSong();
        if (cursor.moveToLast()) {
            do {
                Song song = new Song();
                song.setName(cursor.getString(0));
                song.setSinger(cursor.getString(1));
                song.setUrl(cursor.getString(2));
                mRecentSongs.add(song);
            } while (cursor.moveToPrevious());
        }
        mRecentSongAdapter.notifyDataSetChanged();
    }

    /**
     * 当侧滑菜单处于开启状态，按下返回键将会关闭侧滑菜单
     */
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
