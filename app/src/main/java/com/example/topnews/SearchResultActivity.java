package com.example.topnews;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.topnews.adapter.NewsFragmentPagerAdapter;
import com.example.topnews.bean.Category;
import com.example.topnews.fragment.ResultFragment;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    ViewPager viewPager;

    private ArrayList<Category> userChannelList;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    final static int REQUEST_CODE = 1;

    private String keywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_record);
        keywords = getIntent().getStringExtra("keywords");

        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.result_view_pager);
        viewPager.setCurrentItem(0);
        initFragment();
    }

    private void initFragment() {
        fragments.clear();
        Bundle data = new Bundle();
        data.putString("keywords", keywords);
        ResultFragment newsFragment = new ResultFragment();
        newsFragment.setArguments(data);
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
