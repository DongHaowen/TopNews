package com.java.lwzdhw.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.java.lwzdhw.R;
import com.java.lwzdhw.adapter.NewsAdapter;
import com.java.lwzdhw.bean.News;
import com.java.lwzdhw.bean.Page;
import com.java.lwzdhw.utils.GetDate;
import com.java.lwzdhw.utils.MaskHandler;
import com.java.lwzdhw.utils.ServerHandler;
import com.java.lwzdhw.view.HeadListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultFragment extends Fragment {
    private final static String TAG = "NewsFragment";
    Activity activity;
    ArrayList<News> newsList = new ArrayList<>();
    HeadListView headListView;
    NewsAdapter adapter;
    String keywords;
    ImageView detail_loading;
    public final static int SET_NEWSLIST = 0;
    public final static int MORE_NEWS = 1;
    //Toast提示框
    private RelativeLayout notify_view;
    private TextView notify_view_text;
    int moreTimes = 0;

    RefreshLayout refreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        keywords = args != null ? args.getString("keywords") : "";
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
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
        headListView = view.findViewById(R.id.mListView);
        detail_loading = view.findViewById(R.id.detail_loading);

        refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getMoreData();
            }
        });
        return view;
    }

    public void getMoreData() {
        ++moreTimes;
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverHandler = new ServerHandler();
                if (keywords != null) serverHandler.setWords(keywords);
                serverHandler.setSize(10 + 10 * moreTimes);
                serverHandler.setEndDate(GetDate.getCurrentDate());
                try {
                    page = serverHandler.getPage();
                    news = page.data;
                    int masked = 0;
                    for (int i = 0 ; i < news.length; i++) {
                        if(newsList.contains(news[i])) continue;
                        if(masked < 5 && !MaskHandler.getInstance().check(news[i])){
                            masked ++;
                            continue;
                        }
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
                serverHandler.setSize(10);
                if (keywords != null) serverHandler.setWords(keywords);
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
                newsList = new ArrayList<>(Arrays.asList(news));
                int masked = 0;
                for (int i = 0 ; i < newsList.size() ; ++i){
                    if(masked > 5) break;
                    if(!MaskHandler.getInstance().check(newsList.get(i))){
                        newsList.remove(i);
                        masked ++;
                    }
                }
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
                    adapter.setGray();
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
