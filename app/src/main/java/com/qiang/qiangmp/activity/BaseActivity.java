package com.qiang.qiangmp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.fragment.PlayingControlBarFragment;

/**
 * @author xiaoq
 * @date 19-1-9
 */
public class BaseActivity extends AppCompatActivity {

    private PlayingControlBarFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 不显示ActionBar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        showPlayingControlBar(true);
    }

    /**
     * @param show 显示或关闭底部播放控制栏
     */
    public void showPlayingControlBar(boolean show) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show) {
            if (fragment != null) {
                ft.show(fragment).commitAllowingStateLoss();
            } else {
                fragment = new PlayingControlBarFragment();
                ft.add(R.id.fl_bottom_control_bar, fragment).commitAllowingStateLoss();
            }
        } else {
            if (fragment != null) {
                ft.hide(fragment).commitAllowingStateLoss();
            }
        }
    }
}
