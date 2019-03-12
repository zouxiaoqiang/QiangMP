package com.qiang.qiangmp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.SongAdapter;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.qiang.qiangmp.QiangMpApplication.globalSongList;
import static com.qiang.qiangmp.QiangMpApplication.globalSongPos;
import static com.qiang.qiangmp.QiangMpApplication.player;


/**
 * @author xiaoq
 * @date 19-1-23
 */
public abstract class BaseMusicSearchFragment extends Fragment {
    private List<Song> songList = new ArrayList<>();
    private SongAdapter songAdapter;
    private RequestQueue requestQueue;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        ListView listView = view.findViewById(R.id.lv);
        songAdapter = new SongAdapter(getActivity(), songList);
        listView.setAdapter(songAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            globalSongList.clear();
            globalSongList.addAll(songList);
            globalSongPos = position;
            Song song = globalSongList.get(globalSongPos);
            String url = song.getUrl();
            player.setName(song.getName());
            player.setSinger(song.getSinger());
            Intent i = new Intent(getActivity(), MusicPlayService.class);
            i.putExtra("url", url);
            Objects.requireNonNull(getActivity()).startService(i);
        });
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 搜索音乐
     */
    public void searchMusic(String s) {
        String url = getSearchUrl(s);
        StringRequest searchMusic = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray data = jsonObject.getJSONArray("data");
                songList.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    Song song = new Song();
                    song.setId(object.getString("id"));
                    song.setName(object.getString("name"));
                    song.setTime(object.getInt("time"));
                    song.setSinger(object.getString("singer"));
                    song.setUrl(object.getString("url"));
                    song.setPic(object.getString("pic"));
                    song.setLrc(object.getString("lrc"));
                    songList.add(song);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            songAdapter.notifyDataSetChanged();
        }, error -> Toast.makeText(getActivity(), "单曲搜索失败",
                Toast.LENGTH_SHORT).show());
        requestQueue.add(searchMusic);
    }

    /**
     * 获取相关的搜索路径
     *
     * @param s 搜索关键词
     * @return 搜索路径
     */
    public abstract String getSearchUrl(String s);
}
