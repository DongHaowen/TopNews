package com.example.topnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Adapter;
import android.widget.TextView;

import com.example.topnews.adapter.NewsFragmentPagerAdapter;
import com.example.topnews.fragment.RecordFragment;
import com.example.topnews.fragment.ResultFragment;
import com.example.topnews.utils.RecordAdpter;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_record);
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.result_view_pager);
        viewPager.setCurrentItem(0);
        TextView title = findViewById(R.id.record_title);
        title.setText("历史记录");
        initFragment();
    }

    private void initFragment() {
        fragments.clear();
        RecordAdpter adpter = new RecordAdpter(MainActivity.history);
        adpter.setStep(-1);
        adpter.setStart(MainActivity.history.size()-1);
        RecordFragment newsFragment = new RecordFragment();
        newsFragment.setAdapter(adpter);
        fragments.add(newsFragment);
        NewsFragmentPagerAdapter adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
    }

    public ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
