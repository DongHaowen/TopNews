package com.java.lwzdhw.utils;

import com.java.lwzdhw.MainActivity;
import com.java.lwzdhw.bean.News;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

class ShareUploadThread extends  Thread {
    News news = null;
    final static String host = MainActivity.host;
    final static int port = 8891;
    ShareUploadThread(News news){
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

public class ShareHandler {
    public void share(final News news){
        ShareUploadThread shareThread = new ShareUploadThread(news);
        shareThread.start();
    }
}
