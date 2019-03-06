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
import com.qiang.qiangmp.util.MyLog;
import com.qiang.qiangmp.util.Player;
import com.qiang.qiangmp.util.QiangMPConstants;

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


    /**
     * 记录暂停状态
     */
    private static boolean mIsPause;
    /**
     * 当前歌曲位置
     */
    public static int globalSongPos;
    /**
     * 当前缓存歌曲列表， 默认为空
     */
    public static List<Song> globalSongList;

    private SeekBar mSeekBar;
    private TextView mTextViewCurrentTime, mTextViewDuration;
    private ImageButton mIbtnPlay;
    private MusicPlayBroadcastReceiver mMusicPlayBroadcastReceiver;

    private static IntentFilter musicPlayerFilter = new IntentFilter();

    static {
        mIsPause = true;
        globalSongList = new ArrayList<>();
        globalSongPos = -1;
        musicPlayerFilter.addAction(QiangMPConstants.ACTION_SONG_PLAY);
        musicPlayerFilter.addAction(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
        musicPlayerFilter.addAction(QiangMPConstants.ACTION_SONG_DURATION);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playing_control_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        onChangeActivityInit();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView(View view) {
        mSeekBar = view.findViewById(R.id.seek_bar);
        mTextViewCurrentTime = view.findViewById(R.id.tv_current_time);
        mTextViewDuration = view.findViewById(R.id.tv_duration);
        mIbtnPlay = view.findViewById(R.id.ibtn_play);
        ImageButton mIbtnPrevious = view.findViewById(R.id.ibtn_previous_music);
        ImageButton mIbtnNext = view.findViewById(R.id.ibtn_next_music);
        mIbtnPlay.setOnClickListener(this);
        mIbtnNext.setOnClickListener(this);
        mIbtnPrevious.setOnClickListener(this);
    }

    /**
     * receiver接收广播，控制播放栏图标的变化，播放状态的变化
     * 在fragment可见时，注册receiver
     * 不可见时，注销receiver
     */
    @Override
    public void onStart() {
        super.onStart();
        mMusicPlayBroadcastReceiver = new MusicPlayBroadcastReceiver();
        Objects.requireNonNull(getActivity()).registerReceiver(mMusicPlayBroadcastReceiver, musicPlayerFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(mMusicPlayBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    if (!mIsPause) {
                        mIsPause = true;
                        setPlayState();
                    }
                    globalSongPos = (globalSongPos + 1) % globalSongList.size();
                    startMusicPlayerService();
                }
                break;
            case R.id.ibtn_previous_music:
                if (player != null && !globalSongList.isEmpty()) {
                    if (!mIsPause) {
                        mIsPause = true;
                        setPlayState();
                    }
                    globalSongPos = (globalSongPos + globalSongList.size() - 1) % globalSongList.size();
                    startMusicPlayerService();
                }
                break;
            default:
        }
    }

    /**
     * 播放前/后一首歌曲，开启MusicPlayerService
     */
    private void startMusicPlayerService() {
        Song song = globalSongList.get(globalSongPos);
        String url = song.getUrl();
        Intent i = new Intent(getActivity(), MusicPlayService.class);
        i.putExtra("song_url", url);
        Objects.requireNonNull(getActivity()).startService(i);
    }

    /**
     * 接收MusicPlayService发送的当前播放时间和总时长
     */
    public class MusicPlayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int serialNum = intent.getIntExtra("serial_num", 0);
            int time;
            switch (serialNum) {
                case QiangMPConstants.NUM_SONG_DURATION:
                    // 毫秒
                    time = intent.getIntExtra("time", 0);
                    mSeekBar.setMax(time);
                    mTextViewDuration.setText(formatDate(time));
                    break;
                case QiangMPConstants.NUM_SONG_CURRENT_POSITION:
                    time = intent.getIntExtra("time", 0);
                    mSeekBar.setProgress(time);
                    mTextViewCurrentTime.setText(formatDate(time));
                    break;
                case QiangMPConstants.NUM_SONG_PLAY:
                    mIsPause = !mIsPause;
                    setPlayState();
                    break;
                default:
                    break;
            }
        }
    }

    private String formatDate(int time) {
        Date date = new Date(time);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        return simpleDateFormat.format(date);
    }

    private void setPlayState() {
        if (player != null) {
            if (mIsPause) {
                player.pause();
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp, null));
            } else {
                player.start();
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp, null));
            }
        }
    }

    /**
     * 切换界面时，需要保持原有界面的播放状态。
     */
    private void onChangeActivityInit() {
        if (player != null) {
            if (mIsPause) {
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp, null));
            } else {
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp, null));
            }
            int time = Player.mediaPlayer.getDuration();
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            Objects.requireNonNull(getActivity()).sendBroadcast(i);
            time = Player.mediaPlayer.getCurrentPosition();
            i = new Intent(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_CURRENT_POSITION);
            getActivity().sendBroadcast(i);
        }
    }
}
