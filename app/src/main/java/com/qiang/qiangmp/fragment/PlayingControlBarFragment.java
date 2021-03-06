package com.qiang.qiangmp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
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
import java.util.Date;
import java.util.Objects;

import static com.qiang.qiangmp.QiangMpApplication.globalSongList;
import static com.qiang.qiangmp.QiangMpApplication.globalSongPos;
import static com.qiang.qiangmp.QiangMpApplication.mIsPause;

/**
 * @author xiaoq
 * @date 19-2-16
 */
public class PlayingControlBarFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PlayingControlBarFragment";

    private SeekBar mSeekBar;
    private TextView mTextViewCurrentTime, mTextViewDuration;
    private ImageButton mIbtnPlay;
    private MusicPlayBroadcastReceiver mMusicPlayBroadcastReceiver;
    private TextView tvName, tvSinger;
    private Player player = Player.getInstance();

    private static IntentFilter musicPlayerFilter = new IntentFilter();

    static {
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
        super.onViewCreated(view, savedInstanceState);
        MyLog.d("PlayingControlBarFragment", "onViewCreated");
        initView(view);
        listen();
    }

    private void initView(View view) {
        mSeekBar = view.findViewById(R.id.seek_bar);
        mTextViewCurrentTime = view.findViewById(R.id.tv_current_time);
        mTextViewDuration = view.findViewById(R.id.tv_duration);
        mIbtnPlay = view.findViewById(R.id.ibtn_play);
        tvName = view.findViewById(R.id.tv_name);
        tvSinger = view.findViewById(R.id.tv_singer);
        ImageButton mIbtnPrevious = view.findViewById(R.id.ibtn_previous_music);
        ImageButton mIbtnNext = view.findViewById(R.id.ibtn_next_music);
        mIbtnPlay.setOnClickListener(this);
        mIbtnNext.setOnClickListener(this);
        mIbtnPrevious.setOnClickListener(this);
    }

    private void listen() {
        // SeekBar滑块的滑动监听
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当用户拖动滑块会触发函数
                if (fromUser) {
                    // 改变歌曲进度
                    player.seekTo(progress);
                    mSeekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!globalSongList.isEmpty()) {
                    Drawable drawable = getResources().getDrawable(R.drawable.seekbar_thumb_pressed, null);
                    seekBar.setThumb(drawable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!globalSongList.isEmpty()) {
                    Drawable drawable = getResources().getDrawable(R.drawable.seekbar_thumb_normal, null);
                    seekBar.setThumb(drawable);
                    int progress = seekBar.getProgress();
                    player.seekTo(progress);
                    mTextViewCurrentTime.setText(formatDate(progress));
                }
            }
        });
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
        onChangeActivityInit();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(mMusicPlayBroadcastReceiver);
        MyLog.d("PlayingControlBarFragment", "onStop");
    }

    @Override
    public void onDestroy() {
        MyLog.d("PlayingControlBarFragment", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_play:
                if (player.isPrepare()) {
                    mIsPause = !mIsPause;
                    setPlayState();
                }
                break;
            case R.id.ibtn_next_music:
                if (!globalSongList.isEmpty()) {
                    if (!mIsPause) {
                        mIsPause = true;
                        setPlayState();
                    }
                    globalSongPos = (globalSongPos + 1) % globalSongList.size();
                    startMusicPlayerService();
                }
                break;
            case R.id.ibtn_previous_music:
                if (!globalSongList.isEmpty()) {
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
        player.setName(song.getName());
        player.setSinger(song.getSinger());
        Intent i = new Intent(getActivity(), MusicPlayService.class);
        i.putExtra("url", url);
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
                    // 设置歌曲名和歌手名
                    tvName.setText(player.getName());
                    tvSinger.setText(player.getSinger());
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

    public void setPlayState() {
        if (mIsPause) {
            // 暂停
            player.pause();
            mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp, null));
        } else {
            // 播放
            player.start();
            mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp, null));
        }
    }

    /**
     * 切换界面时，需要保持原有界面的播放状态。
     */
    private void onChangeActivityInit() {
        if (player.isPrepare()) {
            if (mIsPause) {
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp, null));
            } else {
                mIbtnPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp, null));
            }
            int time = player.getDuration();
            MyLog.d("onChangeActivtiyInit", "duration: " + time);
            Intent i = new Intent(QiangMPConstants.ACTION_SONG_DURATION);
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_DURATION);
            Objects.requireNonNull(getActivity()).sendBroadcast(i);
            time = player.getCurrentPosition();
            MyLog.d("onChangeActivtiyInit", "current: " + time);
            i = new Intent(QiangMPConstants.ACTION_SONG_CURRENT_POSITION);
            i.putExtra("time", time);
            i.putExtra("serial_num", QiangMPConstants.NUM_SONG_CURRENT_POSITION);
            getActivity().sendBroadcast(i);
        }
    }
}
