package com.example.topnews.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.topnews.R;
import com.example.topnews.adapter.NewsAdapter;
import com.example.topnews.bean.News;
import com.example.topnews.bean.Page;
import com.example.topnews.utils.GetDate;
import com.example.topnews.utils.Sample;
import com.example.topnews.utils.ServerHandler;
import com.example.topnews.view.HeadListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsFragment extends Fragment {
    private final static String TAG = "NewsFragment";
    Activity activity;
    ArrayList<News> newsList = new ArrayList<>();
    HeadListView headListView;
    NewsAdapter adapter;
    String text;
    int categoryId;
    ImageView detail_loading;
    public final static int SET_NEWSLIST = 0;
    public final static int MORE_NEWS = 1;
    int moreTimes = 0;
    private final int newsListSize = 30;

    public boolean grayable = false;

    RefreshLayout refreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        text = args != null ? args.getString("text") : "";
        categoryId = args != null ? args.getInt("id", 0) : 0;
        grayable = args != null ? args.getBoolean("gray"):false;
        initData();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时加载数据
            if(newsList != null && newsList.size() !=0){
                handler.obtainMessage(SET_NEWSLIST).sendToTarget();
            } // else{
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                        handler.obtainMessage(SET_NEWSLIST).sendToTarget();
//                    }
//                }).start();
//            }
//        }else{
//            //fragment不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
        headListView = view.findViewById(R.id.mListView);
        TextView itemTextView = view.findViewById(R.id.item_textview);
        detail_loading = view.findViewById(R.id.detail_loading);

        itemTextView.setText(text);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
                refreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
//                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
                getMoreData();
            }
        });
        return view;
    }

    public void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverHandler = new ServerHandler();
                serverHandler.setSize(newsListSize);
                serverHandler.setCategories(text);
                serverHandler.setEndDate(GetDate.getCurrentDate());
                while (page == null) {
                    page = serverHandler.getPage();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                news = page.data;
                Log.d(TAG, "run: " + categoryId + " " + text  + " " + news.length + " " + moreTimes);
                newsList = new ArrayList<>(Arrays.asList(news));
                newsList = (ArrayList<News>) Sample.createRandomList(newsList, 10);
                handler.obtainMessage(SET_NEWSLIST).sendToTarget();
                refreshLayout.finishRefresh();
            }
        }).start();
    }

    public void getMoreData() {
        ++moreTimes;
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverHandler = new ServerHandler();
                serverHandler.setSize(newsListSize + 10 * moreTimes);
                serverHandler.setCategories(text);
                serverHandler.setEndDate(GetDate.getCurrentDate());
                try {
                    page = serverHandler.getPage();
                    news = page.data;
                    Log.d(TAG, "run: " + categoryId + " " + text  + " " + news.length + " " + moreTimes);
                    for (int i = newsList.size(); i < news.length; i++) {
                        newsList.add(news[i]);
                    }
                    handler.obtainMessage(MORE_NEWS).sendToTarget();
                    refreshLayout.finishLoadMore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public ServerHandler serverHandler;
    public Page page;
    public News[] news;

    public void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverHandler = new ServerHandler();
                serverHandler.setSize(newsListSize);
                serverHandler.setCategories(text);
                serverHandler.setEndDate(GetDate.getCurrentDate());
                while (page == null) {
                    page = serverHandler.getPage();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                news = page.data;
                Log.d(TAG, "run: " + categoryId + " " + text  + " " + news.length + " " + moreTimes);
                newsList = new ArrayList<>(Arrays.asList(news));
                newsList = (ArrayList<News>) Sample.createRandomList(newsList, 10);
                handler.obtainMessage(SET_NEWSLIST).sendToTarget();
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_NEWSLIST:
                    detail_loading.setVisibility(View.GONE);
//                    if (adapter == null) {
                        adapter = new NewsAdapter(newsList, activity);
                        if(grayable) adapter.setGray();
//                    }
                    int currentPos = headListView.getFirstVisiblePosition();
                    headListView.setAdapter(adapter);
                    headListView.setSelection(currentPos);
                    headListView.setOnScrollListener(adapter);
                    Log.d(TAG, "handleMessage: " + currentPos);
                    break;
                case MORE_NEWS:
                    adapter.notifyDataSetChanged();
                default:
                    break;
            }
        super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
    }
}
