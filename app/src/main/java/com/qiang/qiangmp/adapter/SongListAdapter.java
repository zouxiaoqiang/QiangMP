package com.qiang.qiangmp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.activity.SongListActivity;
import com.qiang.qiangmp.bean.SongList;
import com.qiang.qiangmp.util.load_web_image.AsyncImageLoader;
import com.qiang.qiangmp.util.load_web_image.FileCache;
import com.qiang.qiangmp.util.load_web_image.MemoryAndFileCache;
import com.qiang.qiangmp.util.load_web_image.MemoryCache;

import java.io.File;
import java.util.List;

/**
 * @author xiaoqiang
 * @date 19-3-11
 */
public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {
    private Context mContext;
    private List<SongList> data;
    /**
     *
     */
    private int platform;

    private AsyncImageLoader imageLoader;


    public SongListAdapter(Context context, List<SongList> data, int platform) {
        this.mContext = context;
        this.data = data;
        this.platform=  platform;
        File sdCard = android.os.Environment.getExternalStorageDirectory();
        File cacheDir = new File(sdCard, "QiangMP");
        MemoryAndFileCache memoryAndFileCache = new MemoryAndFileCache(mContext, cacheDir, "song_list_image");
        imageLoader = new AsyncImageLoader();
        imageLoader.setImageCache(memoryAndFileCache);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_song_list_adapter, viewGroup, false);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SongListActivity.class);
            String id = (String) v.getTag();
            intent.putExtra("song_list_id", id);
            intent.putExtra("platform_code", platform);
            mContext.startActivity(intent);
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        SongList sl = data.get(i);
        myViewHolder.tvName.setText(sl.getName());
        myViewHolder.itemView.setTag(sl.getId());
        myViewHolder.ivPic.setTag(sl.getPic());
        Bitmap bmp = imageLoader.loadBitmap(myViewHolder.ivPic, sl.getPic());
        if (bmp == null) {
            myViewHolder.ivPic.setImageResource(R.drawable.ic_cloud_download_black_48dp);
        } else {
            myViewHolder.ivPic.setImageBitmap(bmp);
        }
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

    public void destroy() {
        imageLoader.destroy();
    }
}
