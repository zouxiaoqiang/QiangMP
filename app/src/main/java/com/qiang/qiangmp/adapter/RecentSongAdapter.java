package com.qiang.qiangmp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qiang.qiangmp.QiangMpApplication;
import com.qiang.qiangmp.R;
import com.qiang.qiangmp.bean.Song;
import com.qiang.qiangmp.fragment.PlayingControlBarFragment;
import com.qiang.qiangmp.service.MusicPlayService;

import java.util.List;
import java.util.Objects;

/**
 * @author xiaoqiang
 * @date 19-3-7
 */
public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.MyViewHolder> {
    private Context context;
    private List<Song> data;
    private AlertDialog deleteSongDialog;

    public RecentSongAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
        deleteSongDialog = new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_delete_indigo_200_24dp)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_navigation_recycle_view, viewGroup, false);
        view.setOnClickListener(v -> {
            QiangMpApplication.globalSongList.clear();
            QiangMpApplication.globalSongList.addAll(data);
            QiangMpApplication.globalSongPos = (int) v.getTag();
            Song song = QiangMpApplication.globalSongList.get(QiangMpApplication.globalSongPos);
            String url = song.getUrl();
            String name = song.getName();
            String singer = song.getSinger();
            Intent intent = new Intent(context, MusicPlayService.class);
            intent.putExtra("url", url);
            intent.putExtra("name", name);
            intent.putExtra("singer", singer);
            context.startService(intent);
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
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
