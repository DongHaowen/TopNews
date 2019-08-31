package com.example.topnews.utils;


import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class RecordHandler {
    Set<String> records  = new HashSet<>();
    private String home = "/data/user/0/com.example.topnews/cache/";
    private File recordFile;

    public RecordHandler(final String type){
        recordFile = new File(home+type+".txt");
        load();
    }

    public void add(final String newsID){
        if(records.contains(newsID)) return;
        Log.d("Record Add", newsID);
        records.add(newsID);
    }
    public void remove(final String newsID){
        if(records.contains(newsID)) records.remove(newsID);
    }
    public void save(){
        try {
            recordFile.delete();
            recordFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(recordFile));
            for (String value: records){
                Log.d("Record Save.",value);
                writer.write( value + "\n");
            }
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void load(){
        try {
            if(!recordFile.exists()){
                Log.d("Record Error","File Missing.");
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(recordFile));
            String line = null;
            while ((line = reader.readLine())!=null){
                records.add(line);
                // Log.d("Record Load.",records.lastElement());
            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean has(final String newsID){
        return records.contains(newsID);
    }
}
