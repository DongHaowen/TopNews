package com.example.topnews.utils;

import android.util.Log;

import com.example.topnews.bean.News;
import com.example.topnews.bean.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import jackmego.com.jieba_android.*;

public class RecommendAdpter {
    Vector<News> recommends = new Vector<>();
    private int index = 0;
    final static int showLimit = 5;
    final static int fetchLimit = 20;
    private News news;
    final private String TAG = "RecommendAdapter";
    final private double minScore = 0.9;
    public RecommendAdpter(News news){
        this.news = news;
        recommends.clear();
    }

    public String getKeyword(){
        String content = news.title + "\n" + news.content;
        List<String> list = new ArrayList<>();
        // JiebaSegmenter segmenter = JiebaSegmenter.getJiebaSegmenterSingleton();
        // ArrayList<String> list = segmenter.getDividedString(content);
        News.ScoreWord[] keywords = news.getKeywords();
        for(News.ScoreWord word:keywords){
            if(word.score > minScore)
                list.add(word.word);
        }
        if(list.size() == 0)
            return null;
        return list.get((int)(Math.random()* list.size()));
    }

    public void recommend(){
        String keyword = getKeyword();
        while (keyword != null && recommends.size() < showLimit) {
            Log.d(TAG, "Keyword:" + keyword);
            ServerHandler serverHandler = new ServerHandler();
            serverHandler.setSize(fetchLimit);
            serverHandler.setCategories(news.category);
            serverHandler.setEndDate(GetDate.getCurrentDate());
            serverHandler.setWords(keyword);
            Page page = serverHandler.getPage();
            // Log.d(TAG,"PageSize:"+String.valueOf(page.getPageSize()));
            for (int i = 0; i < page.getPageSize(); ++i) {
                Log.d(TAG, page.getNews(i).title);
                if (page.getNews(i).newsID == news.newsID) continue;
                if(recommends.contains(news)) continue;
                recommends.add(page.getNews(i));
                if (recommends.size() >= showLimit) return;
            }
            keyword = getKeyword();
        }
    }

    public News next(){
        try {
            index += 1;
            return recommends.get(index-1);
        } catch (Exception e){
            return null;
        }
    }
}
