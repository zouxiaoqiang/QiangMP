package com.qiang.qiangmp.fragment;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import static com.qiang.qiangmp.activity.SearchActivity.player;

/**
 * @author xiaoq
 * @date 19-2-16
 */
public class PlayingControlBarFragment extends Fragment implements View.OnClickListener {
    /**
     * 记录暂停状态
     */
    private boolean mIsPause = true;
    /**
     * 当前歌曲位置
     */
    private int position;
    /**
     * 当前缓存歌曲列表， 默认为空
     */
    private List<Song> songList;
    private SeekBar mSeekBar;
    private TextView mTextViewCurrentTime, mTextViewDuration;
    private ImageButton mIbtnPlay, mIbtnPrevious, mIbtnNext;

    public PlayingControlBarFragment() {
        songList = new ArrayList<>();
        position = -1;
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
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_play:
                if (player != null && !songList.isEmpty()) {
                    mIsPause = !mIsPause;
                    setPlayState();
                }
                break;
            case R.id.ibtn_next_music:
                if (player != null && !songList.isEmpty()) {
                    position = (position + 1) % songList.size();
                    Song song = songList.get(position);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(getActivity(), MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", position);
                        getActivity().startService(i);
                    }).start();
                }
                break;
            case R.id.ibtn_previous_music:
                if (player != null && !songList.isEmpty()) {
                    position = (position + songList.size() - 1) % songList.size();
                    Song song = songList.get(position);
                    String url = song.getUrl();
                    new Thread(() -> {
                        Intent i = new Intent(getActivity(), MusicPlayService.class);
                        i.putExtra("song_url", url);
                        i.putExtra("position", position);
                        getActivity().startService(i);
                    }).start();
                }
                break;
            default:
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
