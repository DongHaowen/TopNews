package com.java.lwzdhw.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.lwzdhw.MainActivity;
import com.java.lwzdhw.NewsActivity;
import com.java.lwzdhw.R;
import com.java.lwzdhw.bean.News;

import com.java.lwzdhw.utils.BackupHandler;
import com.java.lwzdhw.utils.FileHandler;
import com.java.lwzdhw.view.HeadListView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter implements SectionIndexer, HeadListView.HeaderAdapter, AbsListView.OnScrollListener {
    ArrayList<News> newsList;
    Activity activity;
    LayoutInflater inflater = null;
    private boolean grayable = false;

    public void setGray(){
        grayable = true;
    }

    public NewsAdapter(ArrayList<News> newsList, Activity activity) {
        this.newsList = newsList;
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        if (newsList != null) {
            initDateHead();
        }
    }

    List<Integer> positions;
    List<String> sections;

    private void initDateHead() {
        sections = new ArrayList<>();
        positions = new ArrayList<>();
        for (int i = 0; i < newsList.size(); i++) {
            sections.add(newsList.get(i).publishTime);
            positions.add(i);
        }
    }

    @Override
    public int getCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public News getItem(int position) {
        if (newsList != null && newsList.size() > 0) {
            return newsList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.itemLayout = view.findViewById(R.id.item_layout);
            viewHolder.itemTitle = view.findViewById(R.id.item_title);
            viewHolder.itemSource = view.findViewById(R.id.item_source);
            viewHolder.publishTime = view.findViewById(R.id.publish_time);
            viewHolder.itemAbstract = view.findViewById(R.id.item_abstract);
            viewHolder.rightImage = view.findViewById(R.id.right_image);
            viewHolder.rightPaddingView = view.findViewById(R.id.right_padding_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final News news = getItem(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Log.d("RecordID",news.newsID);
                    new FileHandler().store(news);
                    MainActivity.saver.addNews(news.category,news.newsID);
                } catch (Exception e){

                }
            }
        }).start();


        if(grayable && MainActivity.history.has(news.newsID)){
            viewHolder.itemTitle.setTextColor(Color.GRAY);
            viewHolder.itemSource.setTextColor(Color.GRAY);
            viewHolder.publishTime.setTextColor(Color.GRAY);
            viewHolder.itemAbstract.setTextColor(Color.GRAY);
        }else {
            viewHolder.itemTitle.setTextColor(activity.getResources().getColor(R.color.default_text));
            viewHolder.itemSource.setTextColor(activity.getResources().getColor(R.color.default_text));
            viewHolder.publishTime.setTextColor(activity.getResources().getColor(R.color.default_text));
            viewHolder.itemAbstract.setTextColor(activity.getResources().getColor(R.color.default_text));
        }
        viewHolder.itemTitle.setText(news.title);
        viewHolder.itemSource.setText(news.publisher);
        viewHolder.publishTime.setText(news.publishTime);
        viewHolder.itemAbstract.setText(news.content);
        viewHolder.rightPaddingView.setVisibility(View.VISIBLE);
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.history.add(news.newsID);
                if(!MainActivity.runLocal()){
                    new BackupHandler().backup(news);
                }
                if(grayable && MainActivity.history.has(news.newsID)){
                    viewHolder.itemTitle.setTextColor(Color.GRAY);
                    viewHolder.itemSource.setTextColor(Color.GRAY);
                    viewHolder.publishTime.setTextColor(Color.GRAY);
                    viewHolder.itemAbstract.setTextColor(Color.GRAY);
                }else {
                    viewHolder.itemTitle.setTextColor(activity.getResources().getColor(R.color.default_text));
                    viewHolder.itemSource.setTextColor(activity.getResources().getColor(R.color.default_text));
                    viewHolder.publishTime.setTextColor(activity.getResources().getColor(R.color.default_text));
                    viewHolder.itemAbstract.setTextColor(activity.getResources().getColor(R.color.default_text));
                }
                Intent intent = new Intent();
                intent.putExtra("News",news.toJson());
                intent.setClass(activity, NewsActivity.class);
                activity.startActivity(intent);

            }
        });

        String[] imgUrlList = news.getImage();
        if (imgUrlList != null && imgUrlList.length > 0) {
//            new ImageHandler(activity.getBaseContext()).downloadImage(news.newsID, imgUrlList[0], 0);
//            File[] file = new ImageHandler(activity.getBaseContext()).loadImage(news.newsID);
            viewHolder.rightImage.setVisibility(View.VISIBLE);
            Glide.with(activity.getBaseContext()).load(imgUrlList[0]).into(viewHolder.rightImage);
        } else {
            viewHolder.rightImage.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof HeadListView) {
            ((HeadListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public int getHeaderState(int position) {
        return 0;
    }

    @Override
    public void configureHeader(View header, int position, int alpha) {

    }

    static class ViewHolder {
        LinearLayout itemLayout;
        TextView itemTitle;
        TextView itemSource;
        TextView publishTime;
        TextView itemAbstract;
        ImageView rightImage;
        View rightPaddingView;
    }
}
