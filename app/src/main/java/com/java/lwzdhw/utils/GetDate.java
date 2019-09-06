package com.java.lwzdhw.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDate {
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        Date date = new Date();
        return sdf.format(date);
    }
}
