package com.online.flyer.design.postermaker.Leaflet_utils;

import android.os.SystemClock;

/**
 * Utility to prevent rapid double-clicks and button spamming across the app.
 * Drops duplicate events within a configurable debounce threshold (default 800ms).
 */
public class Leaflet_ClickUtils {

    private static long lastClickTime = 0;
    private static final long DEFAULT_DEBOUNCE_INTERVAL = 800; // 800ms

    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(DEFAULT_DEBOUNCE_INTERVAL);
    }

    public static synchronized boolean isFastDoubleClick(long intervalMs) {
        long currentTime = SystemClock.elapsedRealtime();
        long diff = currentTime - lastClickTime;
        if (diff > 0 && diff < intervalMs) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
