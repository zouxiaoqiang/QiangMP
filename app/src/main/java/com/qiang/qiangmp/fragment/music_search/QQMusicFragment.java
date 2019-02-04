package com.qiang.qiangmp.fragment.music_search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.SongAdapter;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;
import com.qiang.qiangmp.util.QQMusicSearch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xiaoq
 * @date 19-1-23
 */
public class QQMusicFragment extends BaseMusicSearchFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qq_music, container, false);
    }

    @Override
    public String getSearchUrl(String s) {
        return new QQMusicSearch(s).toString();
    }
}
