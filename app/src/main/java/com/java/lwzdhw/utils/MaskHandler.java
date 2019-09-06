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

    public String getString() {
        String str =  "";
        if (badWords.size() > 0) {
            for (String s : badWords) {
                str = str + s + " ";
            }
        }
        str = str.trim();
        return str;
    }

    public boolean check(final News news){
        if(badWords.size() == 0) return true;
        News.ScoreWord[] keywords = news.getKeywords();
        Vector<String> vector = new Vector<>();
        for (int i = 0 ; i < keywords.length ; ++i){
            for(String word:badWords){
                if(keywords[i].score < minThreshold) continue;
                if(keywords[i].word.contains(word)) return false;
            }
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
