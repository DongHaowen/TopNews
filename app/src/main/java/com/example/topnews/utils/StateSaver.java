package com.example.topnews.utils;

import com.example.topnews.bean.State;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class StateSaver {
    final static File savings =  new File("/data/user/0/com.example.topnews/cache/savings.txt");
    public State state = new State();
    public StateSaver(){
        load();
    }
    public void load(){
        if(!savings.exists()) return;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            FileReader reader = new FileReader(savings);
            state = gson.fromJson(reader,State.class);
            reader.close();
        } catch (IOException e){
            return;
        }
    }
    public void save(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            if(!savings.exists()) savings.createNewFile();
            FileWriter writer = new FileWriter(savings);
            String stateInfo = gson.toJson(state,State.class);
            writer.write(stateInfo);
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
}
