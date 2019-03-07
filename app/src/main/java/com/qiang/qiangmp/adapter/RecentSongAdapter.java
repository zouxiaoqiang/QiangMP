package com.qiang.qiangmp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qiang.qiangmp.R;

import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-7
 */
public class RecentSongAdapter extends RecyclerView.Adapter<RecentSongAdapter.MyViewHolder> {
    private Context context;
    private List<String[]> data;
    private String songUrl;

    public RecentSongAdapter(Context context, List<String[]> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_navigation_recycle_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        String[] ss = data.get(i);
        myViewHolder.tvName.setText(ss[0]);
        myViewHolder.tvSinger.setText(ss[1]);
        songUrl = ss[2];
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getSongUrl() {
        return songUrl;
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
