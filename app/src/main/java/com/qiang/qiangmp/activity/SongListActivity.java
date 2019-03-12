package com.qiang.qiangmp.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.qiang.qiangmp.QiangMpApplication;
import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.SongListDetailAdapter;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.util.QiangMPConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.qiang.qiangmp.QiangMpApplication.globalSongList;
import static com.qiang.qiangmp.QiangMpApplication.globalSongPos;
import static com.qiang.qiangmp.QiangMpApplication.player;

/**
 * 歌单详细界面
 *
 * @author xiaoqiang
 * @date 19-3-12
 */
public class SongListActivity extends BaseActivity {
    TextView tvSlName;
    RecyclerView rvSlDetail;
    SongListDetailAdapter adapter;
    List<Song> songInSl = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        initView();
        displaySongs();
    }

    private void initView() {
        ImageButton imgBtnBack = findViewById(R.id.ibtn_back);
        imgBtnBack.setOnClickListener(v -> finish());
        tvSlName = findViewById(R.id.tv_song_list_name);
        rvSlDetail = findViewById(R.id.rv_song_list_detail);
    }

    private void displaySongs() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String slId = getIntent().getStringExtra("song_list_id");
        int platform = getIntent().getIntExtra("platform_code", -1);
        String url = "";
        switch (platform) {
            case QiangMPConstants.PLATFORM_CODE_QQ:
                url = QiangMPConstants.URL_SONG_LIST_DETAIL_QQ + slId;
                break;
            case QiangMPConstants.PLATFORM_CODE_NETEASE:
                url = QiangMPConstants.URL_SONG_LIST_DETAIL_NETEASE + slId;
                break;
            default:
                Toast.makeText(this, "paltform error", Toast.LENGTH_SHORT).show();
                break;
        }
        StringRequest reqSlDetail = new StringRequest(url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");
                String name = "";
                if (platform == QiangMPConstants.PLATFORM_CODE_QQ) {
                    name = data.getString("title");
                } else if (platform == QiangMPConstants.PLATFORM_CODE_NETEASE) {
                    name = data.getString("songListName");
                }
                tvSlName.setText(name);
                JSONArray songs = data.getJSONArray("songs");
                int size = songs.length();
                songInSl.clear();
                for (int i = 0; i < size; i++) {
                    JSONObject obj = songs.getJSONObject(i);
                    Song song = new Song();
                    song.setId(obj.getString("id"));
                    song.setName(obj.getString("name"));
                    song.setSinger(obj.getString("singer"));
                    song.setUrl(obj.getString("url"));
                    songInSl.add(song);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new SongListDetailAdapter(this, songInSl);
            rvSlDetail.setLayoutManager(new LinearLayoutManager(this));
            rvSlDetail.setAdapter(adapter);
        }, error -> Toast.makeText(this, "获取歌单中的歌曲失败", Toast.LENGTH_SHORT).show());
        requestQueue.add(reqSlDetail);
    }

    /**
     * 当点击列表中的歌曲时，更新全局播放歌曲列表和当前歌曲名、歌手，并返回将要播放歌曲的url
     * @param position 点击歌曲的当前位置
     * @return 当前歌曲的url
     */
    public String updateGlobalSongList(int position) {
        globalSongList.clear();
        globalSongList.addAll(songInSl);
        globalSongPos = position;
        Song song = globalSongList.get(globalSongPos);
        player.setName(song.getName());
        player.setSinger(song.getSinger());
        return song.getUrl();
    }
}
