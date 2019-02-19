package com.qiang.qiangmp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.util.QQMusicSearch;

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
