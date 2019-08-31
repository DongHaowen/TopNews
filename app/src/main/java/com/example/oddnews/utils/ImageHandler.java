package com.example.oddnews.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.example.oddnews.NewsActivity;

import java.io.File;
import java.util.Vector;

public class ImageHandler {
    private DownloadManager manager;
    private Context context;
    private String home;
    private BroadcastReceiver receiver;

    public ImageHandler(Context context){
        this.context = context;
        home = new File("/storage/emulated/0/images").getAbsolutePath();
        File homedir = new File(home);
        if(!homedir.exists()) homedir.mkdirs();
        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    private boolean exists(final String name) {
        try {
            File file = new File(getAbsPath(name));
            return file == null;
        } catch (Exception e) {
            return false;
        }
    }
    private String getAbsPath(final String name){
        return home + "/" + name;
    }
    private String getAbsPath(final String newsID, final String dst){
        return home + "/" + newsID + "/" + dst;
    }

    public long downloadImage(final String newsID, final String src, final int ID){
        String[] temp = src.split("[.]");
        String type ;
        if(temp[temp.length-1] == "png")
            type = "png";
        else
            type = "jpeg";
        return downloadImage(newsID,src,"image"+String.valueOf(ID)+ "." + temp[temp.length-1],type);
    }

    private void fileTransform(final String newsID, final String dst){
        Log.d("Download Finished.",getAbsPath(newsID,dst));
        try {
            NewsActivity newsActivity = (NewsActivity) context;
            newsActivity.updateImages();
        } catch (Exception e){

        }
        context.unregisterReceiver(receiver);
    }

    private void listener(final long ID, final String newsID, final String dst){
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long fID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(fID == ID){
                    fileTransform(newsID, dst);
                }
            }
        };
        context.registerReceiver(receiver,filter);
    }

    public long downloadImage(final String newsID, final String src, final String dst, final String type){
        if(!exists(newsID)) new File(getAbsPath(newsID)).mkdirs();
        Log.d("Direct Create New:",getAbsPath(newsID));
        if(exists(newsID,dst)) {
            Log.d("Downloaded:",newsID + "/" + dst);
            return -1;
        }
        Log.d("Downloading:",newsID + "/" + dst);
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(src.trim()));
        } catch (Exception e){
            return -1;
        }
        request.setTitle(src.trim());
        request.setDescription(src.trim());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);
        request.setMimeType("image/"+type);
        request.setDestinationInExternalPublicDir("images/"+newsID,dst);
        long downloadID = manager.enqueue(request);
        listener(downloadID,newsID,dst);
        return downloadID;
    }
    public File[] loadImage(final String newsID){
        Log.d("DirectOpen:",getAbsPath(newsID));

        File file = new File(getAbsPath(newsID)+"/");
        // Log.d("DirectSize:",String.valueOf(file.listFiles().length));
        return file.listFiles();
    }

    public boolean exists(final String newsID, final String dst){
        File image = new File(getAbsPath(newsID) + "/" + dst);
        // Log.d("Download Test:",image.getAbsolutePath());
        return image.exists();
    }

    public int[] finishList(final String newsID){
        if(!exists(newsID)) return null;
        File base = new File(getAbsPath(newsID));
        File[] flist = base.listFiles();
        Vector<Integer> vector = new Vector<Integer>();
        for(File file:flist){
            int i = Integer.parseInt(file.getName().split("[.]")[0].substring(5));
            Log.d("Finished.",String.valueOf(i));
            vector.add(i);
        }
        int[] result = new int[vector.size()];
        int index = 0;
        for(int r: result){
            result[index] = r;
            index ++;
        }
        return result;
    }
}
