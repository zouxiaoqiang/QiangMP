package com.qiang.qiangmp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;
import com.qiang.qiangmp.util.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.qiang.qiangmp.activity.SearchActivity.player;

/**
 * @author xiaoq
 * @date 19-2-16
 */
public class PlayingControlBarFragment extends Fragment implements View.OnClickListener {

    public static final int DURATION_TYPE = 1;
    public static final int CURRENT_TIME_TYPE = 2;

    /**
     * 记录暂停状态
     */
    private boolean mIsPause = true;
    /**
     * 当前歌曲位置
     */
    public static int globalSongPos;
    /**
     * 当前缓存歌曲列表， 默认为空
     */
    public static List<Song> globalSongList;
    /**
     * 当前歌曲时长
     */
    public static int duration = 0;
    private SeekBar mSeekBar;
    private TextView mTextViewCurrentTime, mTextViewDuration;
    private ImageButton mIbtnPlay, mIbtnPrevious, mIbtnNext;
    private MusicPlayBroadcast musicPlayBroadcast;

    public PlayingControlBarFragment() {
        globalSongList = new ArrayList<>();
        globalSongPos = -1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing_control_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSeekBar = view.findViewById(R.id.seek_bar);
        mTextViewCurrentTime = view.findViewById(R.id.tv_current_time);
        mTextViewDuration = view.findViewById(R.id.tv_duration);
        mIbtnPlay = view.findViewById(R.id.ibtn_play);
        mIbtnPrevious = view.findViewById(R.id.ibtn_previous_music);
        mIbtnNext = view.findViewById(R.id.ibtn_next_music);
        mIbtnPlay.setOnClickListener(this);
        mIbtnNext.setOnClickListener(this);
        mIbtnPrevious.setOnClickListener(this);
        // 注册广播, 设置SeekBar的进度
        musicPlayBroadcast = new MusicPlayBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayBroadcast.MUSIC_TIME_ACTION);
        Objects.requireNonNull(getActivity()).registerReceiver(musicPlayBroadcast, filter);
        if (player != null) {
            int time = Player.mediaPlayer.getDuration();
            Intent i = new Intent("com.qiang.qiangmp.musictime");
            i.putExtra("time", time);
            i.putExtra("type", DURATION_TYPE);
            getActivity().sendBroadcast(i);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getActivity()).unregisterReceiver(musicPlayBroadcast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_play:
                if (player != null && !globalSongList.isEmpty()) {
                    mIsPause = !mIsPause;
                    setPlayState();
                }
                break;
            case R.id.ibtn_next_music:
                if (player != null && !globalSongList.isEmpty()) {
                    globalSongPos = (globalSongPos + 1) % globalSongList.size();
                    Song song = globalSongList.get(globalSongPos);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(getActivity(), MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", globalSongPos);
                        getActivity().startService(i);
                    }).start();
                }
                break;
            case R.id.ibtn_previous_music:
                if (player != null && !globalSongList.isEmpty()) {
                    globalSongPos = (globalSongPos + globalSongList.size() - 1) % globalSongList.size();
                    Song song = globalSongList.get(globalSongPos);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(getActivity(), MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", globalSongPos);
                        getActivity().startService(i);
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
        public static final String MUSIC_TIME_ACTION = "com.qiang.qiangmp.musictime";

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            // 毫秒
            int time = intent.getIntExtra("time", 0);
            Date date = new Date(time);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            switch (type) {
                case DURATION_TYPE:
                    duration = time;
                    mIsPause = !mIsPause;
                    setPlayState();
                    mSeekBar.setMax(time);
                    mTextViewDuration.setText(simpleDateFormat.format(date));
                    break;
                case CURRENT_TIME_TYPE:
                    mSeekBar.setProgress(time);
                    mTextViewCurrentTime.setText(simpleDateFormat.format(date));
                    break;
                default:
                    break;
            }
        }
    }

    public void setDuration() {
        Date date = new Date(duration);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        mSeekBar.setMax(duration);
        mTextViewDuration.setText(simpleDateFormat.format(date));
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
