package com.qiang.qiangmp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.activity.SongListActivity;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.service.MusicPlayService;

import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-12
 */
public class SongListDetailAdapter extends RecyclerView.Adapter<SongListDetailAdapter.MyViewHolder> {
    private Context mContext;
    private List<Song> data;

    public SongListDetailAdapter(Context context, List<Song> data) {
        this.mContext = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_song_list_detail_adapter, viewGroup, false);
        view.setOnClickListener(v -> {
            SongListActivity a = (SongListActivity) mContext;
            int pos = (int) v.getTag();
            String url = a.updateGlobalSongList(pos);
            Intent intent = new Intent(mContext, MusicPlayService.class);
            intent.putExtra("url", url);
            mContext.startService(intent);
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Song song = data.get(i);
        myViewHolder.tvName.setText(song.getName());
        myViewHolder.tvSinger.setText(song.getSinger());
        myViewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSinger;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSinger = itemView.findViewById(R.id.tv_singer);
        }
    }
}
