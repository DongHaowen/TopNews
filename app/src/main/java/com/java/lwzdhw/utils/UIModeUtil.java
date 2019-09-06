package com.java.lwzdhw.utils;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

public class UIModeUtil {
    private static UIModeUtil inst = new UIModeUtil();

    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
        Log.d("UIMode", "setMode: " + mode);
    }

    public void changeModeUI(AppCompatActivity activity){
        int currentNightMode = mode;

        if(mode == 2){
            //日间模式,切成夜间模式
            activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            activity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    private UIModeUtil() {
        this.mode = 0;
    }

    public static UIModeUtil getInstance() {
        return inst;
    }
}
