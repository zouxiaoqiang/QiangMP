package com.qiang.qiangmp.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.qiang.qiangmp.R;
import com.qiang.qiangmp.adapter.MusicSearchFragmentPagerAdapter;

/**
 * @author xiaoq
 * @date 19-1-9
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 搜索关键词
     */
    private String s = null;
    private MusicSearchFragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 将ViewPager和Fragment绑定
        ViewPager mViewPager = findViewById(R.id.viewpager);
        mFragmentPagerAdapter = new MusicSearchFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mFragmentPagerAdapter);

        // 将ViewPager和TabLayout绑定
        TabLayout mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (s != null && s.length() != 0) {
                    mFragmentPagerAdapter.getCurrentFragment().searchMusic(s);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        ImageButton imgBtnBack = findViewById(R.id.ibtn_back);
        imgBtnBack.setOnClickListener(this);
        EditText edtSearch = findViewById(R.id.edt_search);
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                s = edtSearch.getText().toString();
                if (!"".equals(s)) {
                    mFragmentPagerAdapter.getCurrentFragment().searchMusic(s);
                }
                return true;
            }
            return false;
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibtn_back) {
            finish();
        }
    }
}
