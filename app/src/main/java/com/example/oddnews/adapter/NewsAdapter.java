package com.example.oddnews.adapter;

import android.app.Activity;
import android.content.Intent;
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
import com.example.oddnews.MainActivity;
import com.example.oddnews.NewsActivity;
import com.example.oddnews.R;
import com.example.oddnews.bean.News;

import com.example.oddnews.utils.FileHandler;
import com.example.oddnews.view.HeadListView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends BaseAdapter implements SectionIndexer, HeadListView.HeaderAdapter, AbsListView.OnScrollListener {
    ArrayList<News> newsList;
    Activity activity;
    LayoutInflater inflater = null;

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
        ViewHolder viewHolder;
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
        viewHolder.itemTitle.setText(news.title);
        viewHolder.itemSource.setText(news.publisher);
        viewHolder.publishTime.setText(news.publishTime);
        viewHolder.itemAbstract.setText("GET SOME ABSTRACT");
        viewHolder.rightPaddingView.setVisibility(View.VISIBLE);
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("News",news.toJson());
                intent.setClass(activity, NewsActivity.class);
                activity.startActivity(intent);
                MainActivity.history.add(news.newsID);
                try {
                    new FileHandler().store(news);
                } catch (Exception e){

                }
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
