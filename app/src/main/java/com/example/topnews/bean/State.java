package com.example.topnews.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class Section {
    public String type;
    public Vector<String> items;
    public Section(final String type){
        this.type = type;
        items = new Vector<>();
    }
    public void add(final String newsID){
        items.add(newsID);
    }
    public void flush() {
        items.clear();
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}

public class State {
    public Vector<Section> sections = new Vector<>();
    public Map<String, Section> map = new HashMap<>();

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
        sections.add(section);
    }
    public void removeSection(final String type){
        Section section = map.get(type);
        if(section == null) return;
        Log.d("SectionRemove",type);
        sections.remove(section);
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
        for (Section section : sections)
            types.add(section.type);
        return types;
    }
}
