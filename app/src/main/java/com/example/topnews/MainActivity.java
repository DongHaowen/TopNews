package com.example.topnews;

import android.Manifest;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topnews.adapter.NewsFragmentPagerAdapter;
import com.example.topnews.bean.Category;
import com.example.topnews.bean.CategoryManage;
import com.example.topnews.fragment.NewsFragment;
import com.example.topnews.utils.GetWidth;
import com.example.topnews.utils.RecordHandler;
import com.example.topnews.utils.StateSaver;
import com.example.topnews.view.ColumnHorizontalScrollView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ColumnHorizontalScrollView scrollView;
    private LinearLayout radioGroup;
    LinearLayout moreColumns;
    RelativeLayout column;
    ImageView buttonMoreColumns;
    ViewPager viewPager;
    ImageView shadeLeft;
    ImageView shadeRight;
    ImageView topHead;
    ImageView topMore;
    ImageView topRefresh;
    ProgressBar topProgress;

    private ArrayList<Category> userChannelList;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int screenWidth = 0;
    private int channelWidth = 0;
    private int columnSelectIndex = 0;

    final static int REQUEST_CODE = 1;

    public final static RecordHandler history = new RecordHandler("history");
    public final static RecordHandler favorite = new RecordHandler("favorite");
    public final static StateSaver saver = new StateSaver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_CODE);
        Log.d("Path:",getFilesDir().getPath());
        screenWidth = GetWidth.getWindowsWidth(this);
        channelWidth = screenWidth / 7;
        initView();
    }

    private void initView() {
        scrollView = findViewById(R.id.scroll_view);
        radioGroup = findViewById(R.id.radio_group);
        moreColumns = findViewById(R.id.more_columns);
        column = findViewById(R.id.column);
        buttonMoreColumns = findViewById(R.id.button_more_columns);
        viewPager = findViewById(R.id.view_pager);
        shadeLeft = findViewById(R.id.shade_left);
        shadeRight = findViewById(R.id.shade_right);
        topHead = findViewById(R.id.top_head);
        topMore = findViewById(R.id.top_more);
        topRefresh = findViewById(R.id.top_refresh);
        topProgress = findViewById(R.id.top_progress);

        buttonMoreColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), ChannelActivity.class);
                startActivity(intent);
            }
        });

        columnChange();
    }

    private void columnChange() {
        initColumn();
        initTabColumn();
        initFragment();
    }

    private void initColumn() {
        userChannelList = (ArrayList<Category>) CategoryManage.getManage().getUserChannel();
    }

    private void initTabColumn() {
        radioGroup.removeAllViews();
        int cnt = userChannelList.size();
        scrollView.setParam(this, screenWidth, radioGroup, shadeLeft, shadeRight, moreColumns, column);
        for (int i = 0; i < cnt; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(channelWidth , ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
//			TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(userChannelList.get(i).getName());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_view_text_normal_day));
            viewPager.setCurrentItem(0);
            if(columnSelectIndex == i){
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for(int i = 0;i < radioGroup.getChildCount();i++){
                        View localView = radioGroup.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else{
                            localView.setSelected(true);
                            viewPager.setCurrentItem(i);
                        }
                    }
                    Toast.makeText(getApplicationContext(), userChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            radioGroup.addView(columnTextView, i ,params);
        }
    }

    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View checkView = radioGroup.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - screenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            scrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }

        //判断是否选中
        for (int j = 0; j <  radioGroup.getChildCount(); j++) {
            View checkView = radioGroup.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }

    private void initFragment() {
        fragments.clear();
        int cnt = userChannelList.size();
        for (int i = 0; i < cnt; i++) {
            Bundle data = new Bundle();
            data.putString("text", userChannelList.get(i).name);
            data.putInt("id", userChannelList.get(i).id);
            NewsFragment newsFragment = new NewsFragment();
            newsFragment.setArguments(data);
            fragments.add(newsFragment);
        }
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
            viewPager.setCurrentItem(i);
            selectTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        history.save();
        favorite.save();
    }
}
