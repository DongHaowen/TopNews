package com.java.lwzdhw.bean;

import java.util.ArrayDeque;
import java.util.Queue;

public class Section {
    public String type;
    public Queue<String> items;
    final static int cacheLimit = 30;
    public Section(final String type){
        this.type = type;
        items = new ArrayDeque<>();
    }
    public void add(final String newsID){
        if(items.contains(newsID)) return;
        items.add(newsID);
        if(items.size() > cacheLimit)
            items.remove();
    }
    public void flush() {
        items.clear();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}