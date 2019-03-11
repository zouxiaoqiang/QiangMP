package com.qiang.qiangmp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.bean.SongList;

import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-11
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {
    private Context mContext;
    private List<SongList> data;

    public SongListAdapter(Context context, List<SongList> data) {
        this.mContext = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_song_list_adapter, viewGroup, false);
        view.setOnClickListener(v -> {

        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        SongList sl = data.get(i);
        myViewHolder.tvName.setText(sl.getName());
        myViewHolder.itemView.setTag(sl.getId());
        myViewHolder.ivPic.setTag(sl.getPic());
        myViewHolder.ivPic.setImageResource(R.drawable.ic_cloud_download_black_48dp);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPic;
        TextView tvName;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_song_list_name);
            ivPic = itemView.findViewById(R.id.iv_song_list_pic);
        }
    }
}
