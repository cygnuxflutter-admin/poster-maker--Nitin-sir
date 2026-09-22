package com.online.flyer.design.postermaker.Leaflet_utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Leaflet_TimeUtils {
    public static String getTimeStamp() {
        Date today = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-dd-MM hh.mm.ss");
        return format.format(today);
    }
}
