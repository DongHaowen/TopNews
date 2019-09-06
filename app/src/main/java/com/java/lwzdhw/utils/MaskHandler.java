package com.java.lwzdhw.utils;

import com.java.lwzdhw.bean.News;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MaskHandler {
    private static MaskHandler instance = null;
    private Set<String> badWords = new HashSet<>();
    final private static double minThreshold = 0.5;

    private MaskHandler(){

    }
    public void add(final String word){
        badWords.add(word);
    }

    public void remove(final String word){
        badWords.remove(word);
    }

    public void flush(){
        badWords.clear();
    }

    public boolean check(final News news){
        if(badWords.size() == 0) return true;
        News.ScoreWord[] keywords = news.getKeywords();
        Vector<String> vector = new Vector<>();
        for (int i = 0 ; i < keywords.length ; ++i){
            if (keywords[i].score > minThreshold && badWords.contains(keywords[i].word))
                return false;
        }
        return true;
    }

    public static MaskHandler getInstance(){
        if(instance == null) {
            instance = new MaskHandler();
        }
        return instance;
    }
}
