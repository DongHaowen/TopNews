package com.example.topnews;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topnews.adapter.NewsFragmentPagerAdapter;
import com.example.topnews.bean.Category;
import com.example.topnews.bean.CategoryManage;
import com.example.topnews.data.LoginDataSource;
import com.example.topnews.data.model.LoggedInUser;
import com.example.topnews.fragment.LocalFragment;
import com.example.topnews.fragment.NewsFragment;
import com.example.topnews.ui.login.LoginActivity;
import com.example.topnews.utils.GetWidth;
import com.example.topnews.utils.RecordHandler;
import com.example.topnews.utils.StateSaver;
import com.example.topnews.view.ColumnHorizontalScrollView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ColumnHorizontalScrollView scrollView;
    private LinearLayout radioGroup;
    LinearLayout moreColumns;
    RelativeLayout column;
    ImageView buttonMoreColumns;
    ViewPager viewPager;
    ImageView shadeLeft;
    ImageView shadeRight;
    private Toolbar toolbar;
    private BroadcastReceiver webListener;
    private NavigationView naviView;

    private BroadcastReceiver receiver;

    private ArrayList<Category> userChannelList;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private int screenWidth = 0;
    private int channelWidth = 0;
    private int columnSelectIndex = 0;

    final static int REQUEST_CODE = 1;

    public static LoggedInUser user = LoggedInUser.defaultUser;
    public static RecordHandler history = new RecordHandler("history");
    public static RecordHandler favorite = new RecordHandler("favorite");
    public final static StateSaver saver = new StateSaver();
    public static MainActivity base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_CODE);
        screenWidth = GetWidth.getWindowsWidth(this);
        channelWidth = screenWidth / 7;

        setListener();
        initView();
        columnChange();
        base = this;

        // JiebaSegmenter.init(this);

    }

    private void setWebListener(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        webListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("WebChange","WebChange");
                columnChange();
            }
        };
        registerReceiver(webListener,filter);
    }

    private void setListener(){
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("TimeClick","Save all");
                String action = intent.getAction();
                if(action.equals(Intent.ACTION_TIME_TICK)) {
                    history.save();
                    favorite.save();
                    new LoginDataSource().remoteUpdate(user);
                }
            }
        };
        registerReceiver(receiver,filter);
    }

    void initView(){
        scrollView = findViewById(R.id.scroll_view);
        radioGroup = findViewById(R.id.radio_group);
        moreColumns = findViewById(R.id.more_columns);
        column = findViewById(R.id.column);
        buttonMoreColumns = findViewById(R.id.button_more_columns);
        viewPager = findViewById(R.id.view_pager);
        shadeLeft = findViewById(R.id.shade_left);
        shadeRight = findViewById(R.id.shade_right);
        naviView = findViewById(R.id.nav_view);

        buttonMoreColumns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), ChannelActivity.class);
                startActivity(intent);
            }
        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.search_btn){
                    Intent intent = new Intent();
                    intent.setClass(getBaseContext(),SearchActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
        toolbar.setTitle("异闻录");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void columnChange() {
        initColumn();
        initTabColumn();
        initFragment();
    }

    private void initColumn() {
        userChannelList = (ArrayList<Category>) new CategoryManage().getUserChannel();
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

    public void selectTab(int tab_postion) {
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

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null){
            return info.isConnected();
        }
        return false;
    }

    private void initFragment() {
        fragments.clear();
        int cnt = userChannelList.size();
        for (int i = 0; i < cnt; i++) {
            Bundle data = new Bundle();
            data.putString("text", userChannelList.get(i).name);
            data.putInt("id", userChannelList.get(i).id);
            data.putBoolean("gray",true);
            if(isNetworkConnected()) {
                NewsFragment newsFragment = new NewsFragment();
                newsFragment.setArguments(data);
                fragments.add(newsFragment);
            }else {
                LocalFragment localFragment = new LocalFragment();
                localFragment.setArguments(data);
                fragments.add(localFragment);
            }
        }
        NewsFragmentPagerAdapter adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(pageChangeListener);
    }

    public ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) { }

        @Override
        public void onPageSelected(int i) {
            viewPager.setCurrentItem(i);
            selectTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) { }
    };

    @Override
    protected void onDestroy() {
        history.save();
        favorite.save();
        saver.save();
        new LoginDataSource().remoteUpdate(user);
        super.onDestroy();

        unregisterReceiver(receiver);
        if(webListener != null)
            unregisterReceiver(webListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final String NAVI_TAG = "Navigation";
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            // Handle the camera action
            Intent intent = new Intent();
            intent.setClass(getBaseContext(),SearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            Log.d(NAVI_TAG,"Favorite");
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), FavoriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Log.d(NAVI_TAG,"History");
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), HistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_login) {
            Log.d(NAVI_TAG, "Tools");
            Log.d(NAVI_TAG,"History");
            Intent intent = new Intent();
            intent.setClass(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUser(){
        TextView view = findViewById(R.id.user_name);
        if(user != LoggedInUser.defaultUser)
            view.setText(user.getUserId());
        else
            view.setText("本地用户");

    }
}
