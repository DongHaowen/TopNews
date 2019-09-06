package com.java.lwzdhw;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.java.lwzdhw.adapter.NewsFragmentPagerAdapter;
import com.java.lwzdhw.bean.Category;
import com.java.lwzdhw.fragment.ResultFragment;
import com.java.lwzdhw.utils.UIModeUtil;

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

        UIModeUtil.getInstance().changeModeUI(this);

        setContentView(R.layout.content_record);
        keywords = getIntent().getStringExtra("keywords");
        TextView view = findViewById(R.id.record_title);
        view.setText("搜索结果");

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
    }
}
