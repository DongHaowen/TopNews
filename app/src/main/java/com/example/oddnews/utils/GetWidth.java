package com.example.oddnews.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

public class GetWidth {
    /** 获取屏幕的宽度 */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
