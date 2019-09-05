package com.example.topnews.utils;


import android.util.Log;

import com.example.topnews.MainActivity;
import com.example.topnews.data.model.LoggedInUser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class RecordHandler {
    public Vector<String> records  = new Vector<>();
    private final static String TAG = "RecordHandler";
    private final String home = "/data/user/0/com.example.topnews/cache/";
    private String type;
    private File recordFile;
    private boolean initial = true;

    public RecordHandler(final String type){
        String thome = home + MainActivity.user.getUserId() + "/";
        File homedir = new File(thome);
        if(!homedir.exists()) homedir.mkdirs();
        this.type = type;
        recordFile = new File(home + MainActivity.user.getUserId() + "/" +type+".txt");
        load();
    }

    public void updateUser(){
        records.clear();
        String thome = home + MainActivity.user.getUserId() + "/";
        File homedir = new File(thome);
        if(!homedir.exists()) homedir.mkdirs();
        recordFile = new File(home + MainActivity.user.getUserId() + "/" +type+".txt");
    }

    public void add(final String newsID){
        if(records.contains(newsID)) return;
        // Log.d("Record Add", newsID);
        records.add(newsID);
    }

    public void remove(final String newsID){
        if(records.contains(newsID)) records.remove(newsID);
    }
    public void save(){
        try {
            Log.d(TAG,"Save"+recordFile.getAbsolutePath());
            recordFile.delete();
            recordFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(recordFile));
            for (String value: records){
                writer.write( value + "\n");
            }
            writer.close();
            initial = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void load(){
        try {
            if(!recordFile.exists()){
                // Log.d("Record Error","File Missing.");
                records.clear();
                return;
            }
            Log.d(TAG,"Load"+recordFile.getAbsolutePath());
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

    public int size(){
        return records.size();
    }

    public long getModifyTime(){
        if(initial) return -1;
        try {
            return recordFile.lastModified();
        } catch (Exception e){
            return 0;
        }
    }
}
