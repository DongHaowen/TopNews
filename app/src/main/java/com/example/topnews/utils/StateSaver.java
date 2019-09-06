package com.example.topnews.utils;

import android.util.Log;

import com.example.topnews.bean.Section;
import com.example.topnews.bean.State;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.Vector;

public class StateSaver {
    // final static File savings =  new File("/storage/emulated/0/savings.txt");
    final static File savings =  new File("/data/user/0/com.example.topnews/cache/savings.txt");
    public State state = new State();

    public void setRank(final String type, final int r){
        Log.d("Put",type);
        state.rank.put(type,r);
    }
    public int getRank(final String type){
        if(state.rank.get(type) == null) return -1;
        return state.rank.get(type);
    }
    public boolean load(){
        if(!savings.exists()) return false;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            FileReader reader = new FileReader(savings);
            state = gson.fromJson(reader,State.class);
            reader.close();
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    public void save(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            savings.delete();
            savings.createNewFile();
            FileWriter writer = new FileWriter(savings);
            String stateInfo = gson.toJson(state,State.class);
            Log.d("GSON:",stateInfo);
            writer.write(stateInfo);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void updateSection(final Vector<String> types){
        Vector<String> has = state.getSections();
        has.removeAll(types);
        for (String type : has){
            state.removeSection(type);
        }
        for (String type : types){
            state.addSection(type);
        }
    }
    public Vector<String> getSections(){
        return state.getSections();
    }
    public void addNews(final String type, final String newsID){
        state.add(type,newsID);
    }
    public Queue<String> getQueue(final String type){
        return state.map.get(type).items;
    }
}
