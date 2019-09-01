package com.example.topnews.bean;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class State {
    public Map<String, Section> map = new HashMap<>();
    public Map<String, Integer> rank = new HashMap<>();

    public void addRecord(final String type, final String newsID){
        if(map.get(type) == null){
            addSection(type);
        }
        map.get(type).add(newsID);
    }
    public void addSection(final String type){
        if(hasSection(type)) return;
        Section section = new Section(type);
        map.put(type, section);
        Log.d("SectionAdd",type);
    }
    public void removeSection(final String type){
        Section section = map.get(type);
        if(section == null) return;
        Log.d("SectionRemove",type);
        map.remove(type);
    }
    public void flushSection(final String type){
        Section section = map.get(type);
        if(section == null) return;
        section.flush();
    }
    public boolean hasSection(final String type){
        return map.get(type) != null;
    }
    public Vector<String> getSections(){
        Vector<String> types = new Vector<>();

        for (String type : map.keySet())
            types.add(type);
        return types;
    }
    public void add(final String type, final String newsID){
        addSection(type);
        map.get(type).add(newsID);
    }
}
