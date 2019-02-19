package com.qiang.qiangmp.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.qiang.qiangmp.fragment.BaseMusicSearchFragment;
import com.qiang.qiangmp.fragment.KuGouMusicFragment;
import com.qiang.qiangmp.fragment.NeteaseMusicFragment;
import com.qiang.qiangmp.fragment.QQMusicFragment;

/**
 * @author xiaoq
 * @date 19-1-23
 */
public class MusicSearchFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = {"QQ音乐", "网易云音乐", "酷狗音乐"};
    private BaseMusicSearchFragment currentFragment;

    public MusicSearchFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public BaseMusicSearchFragment getItem(int i) {
        switch (i) {
            case 1:
                return new NeteaseMusicFragment();
            case 2:
                return new KuGouMusicFragment();
            default:
                return new QQMusicFragment();
        }
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        currentFragment = (BaseMusicSearchFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public BaseMusicSearchFragment getCurrentFragment() {
        return currentFragment;
    }
}
