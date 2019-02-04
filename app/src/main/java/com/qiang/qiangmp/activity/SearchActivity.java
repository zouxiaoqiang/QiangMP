package com.qiang.qiangmp.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.MusicSearchFragmentPagerAdapter;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;
import com.qiang.qiangmp.util.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author xiaoq
 * @date 19-1-9
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SearchActivity";

    public static final int DURATION_TYPE = 1;
    public static final int CURRENT_TIME_TYPE = 2;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    /**
     * 当前歌曲在列表中的位置
     */
    private int position;

    public static Player player;
    /**
     * 搜索关键词
     */
    private String s;
    private SeekBar mSeekBar;
    private TextView mTextViewCurrentTime, mTextViewDuration;
    private ImageButton mIbtnPlay, mIbtnPrevious, mIbtnNext;
    private MusicPlayBroadcast musicPlayBroadcast;
    private MusicSearchFragmentPagerAdapter mFragmentPagerAdapter;
    /**
     * 记录暂停状态
     */
    private boolean mIsPause = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 将ViewPager和Fragment绑定
        mViewPager = findViewById(R.id.viewpager);
        mFragmentPagerAdapter = new MusicSearchFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);

        // 将ViewPager和TabLayout绑定
        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mFragmentPagerAdapter.getCurrentFragment().searchMusic(s);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        mSeekBar = findViewById(R.id.seek_bar);
        mTextViewCurrentTime = findViewById(R.id.tv_current_time);
        mTextViewDuration = findViewById(R.id.tv_duration);
        mIbtnPlay = findViewById(R.id.ibtn_play);
        mIbtnPrevious = findViewById(R.id.ibtn_previous_music);
        mIbtnNext = findViewById(R.id.ibtn_next_music);
        mIbtnPlay.setOnClickListener(this);
        mIbtnNext.setOnClickListener(this);
        mIbtnPrevious.setOnClickListener(this);
        ImageButton imgBtnBack = findViewById(R.id.ibtn_back);
        imgBtnBack.setOnClickListener(this);
        EditText edtSearch = findViewById(R.id.edt_search);
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                s = edtSearch.getText().toString();
                if (!"".equals(s)) {
                    mFragmentPagerAdapter.getCurrentFragment().searchMusic(s);
                }
                return true;
            }
            return false;
        });

        // 注册广播, 设置SeekBar的进度
        musicPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayBroadcast.MUSIC_TIME_ACTION);
        filter.addAction(MusicPlayBroadcast.SONG_POSITION);
        registerReceiver(musicPlayBroadcast, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicPlayBroadcast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.ibtn_play:
                Log.d(TAG, "play");
                if (player != null) {
                    mIsPause = !mIsPause;
                    setPlayState();
                }
                break;
            case R.id.ibtn_next_music:
                if (player != null) {
                    List<Song> list = mFragmentPagerAdapter.getCurrentFragment().getSongList();
                    position = (position + 1) % list.size();
                    Song song = list.get(position);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(this, MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", position);
                        startService(i);
                    }).start();
                }
                break;
            case R.id.ibtn_previous_music:
                if (player != null) {
                    List<Song> list = mFragmentPagerAdapter.getCurrentFragment().getSongList();
                    position = (position + list.size() - 1) % list.size();
                    Song song = list.get(position);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(this, MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", position);
                        startService(i);
                    }).start();
                }
                break;
            default:
        }
    }

    /**
     * 接收MusicPlayService发送的当前播放时间和总时长
     */
    public class MusicPlayBroadcast extends BroadcastReceiver {
        public static final String SONG_POSITION = "com.qiang.qiangmp.songposition";
        public static final String MUSIC_TIME_ACTION = "com.qiang.qiangmp.musictime";

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            // 毫秒
            int time = intent.getIntExtra("time", 0);
            Date dateDuration = new Date(time);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            switch (type) {
                case DURATION_TYPE:
                    position = intent.getIntExtra("position", 0);
                    mIsPause = !mIsPause;
                    setPlayState();
                    mSeekBar.setMax(time);
                    mTextViewDuration.setText(simpleDateFormat.format(dateDuration));
                    break;
                case CURRENT_TIME_TYPE:
                    mSeekBar.setProgress(time);
                    mTextViewCurrentTime.setText(simpleDateFormat.format(dateDuration));
                    break;
                default:
                    break;
            }
        }
    }

    private void setPlayState() {
        if (mIsPause) {
            player.pause();
            mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp, null));
        } else {
            player.start();
            mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp, null));
        }
    }
}
