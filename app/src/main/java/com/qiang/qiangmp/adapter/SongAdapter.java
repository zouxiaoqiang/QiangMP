package com.qiang.qiangmp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.bean.Song;

import java.util.List;

/**
 * @author xiaoq
 * @date 19-1-23
 */
public class SongAdapter extends BaseAdapter {
    private Context mContext;
    private List<Song> mList;

    public SongAdapter(Context context, List<Song> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_song_adapter, null);
            viewHolder.tvName=  convertView.findViewById(R.id.tv_name);
            viewHolder.tvSinger = convertView.findViewById(R.id.tv_singer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Song song = mList.get(position);
        viewHolder.tvName.setText(song.getName());
        viewHolder.tvSinger.setText(song.getSinger());
        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvSinger;
    }
}
