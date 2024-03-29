package com.java.lwzdhw.utils;

import android.util.Log;

import com.java.lwzdhw.MainActivity;
import com.java.lwzdhw.bean.News;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

class NewsUploadThread extends  Thread {
    News news = null;
    final static String host = MainActivity.host;
    final static int port = 8890;
    NewsUploadThread(News news){
        this.news = news;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(news);
            writer.write(json+"\n");
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class NewsDownloadThread extends Thread{
    News news = null;
    String newsID = null;
    final static String host = MainActivity.host;
    final static int port = 8889;
    NewsDownloadThread(final String newsID){
        this.newsID = newsID;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(host,port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
            writer.write(newsID + "\n");
            writer.flush();
            // Log.d("TargetSend",newsID);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            String json = reader.readLine();
            // Log.d("TargetGet",newsID);
            Gson gson = new GsonBuilder().create();
            news = gson.fromJson(json,News.class);
            reader.close();
        } catch (Exception e){
            return;
        }
    }
}

public class BackupHandler {
    final static long waitTime = 1000;
    private boolean lost = false;
    final static String TAG = "BackupHandler";
    public void backup(final News news){
        Log.d(TAG,"Uploading " + news.newsID);
        NewsUploadThread uploadThread = new NewsUploadThread(news);
        uploadThread.start();
    }
    public News revert(final String newsID){
        Log.d(TAG,"Downloading " + newsID);
        lost = false;
        NewsDownloadThread downloadThread = new NewsDownloadThread(newsID);
        downloadThread.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                lost = true;
            }
        },waitTime);
        while (!lost && (downloadThread.news == null)){
            Log.d("Downloading",newsID);
            double r = Math.random()*Math.random();
        }
        Log.d("Finish Download",newsID);
        return downloadThread.news;
    }
}
