package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.net.URLDecoder;
import java.util.concurrent.atomic.AtomicBoolean;

public class Leaflet_InstallReferrerHelper {
    private static final String TAG = "ReferrerHelper";

    public interface AttributionCallback {
        void onAttributionFinished(String acquisitionType); // "paid", "organic", "unknown"
    }

    public static void resolveAttribution(Context context, AttributionCallback callback) {
        if (context == null) {
            if (callback != null) callback.onAttributionFinished("organic");
            return;
        }

        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);

        // 1. Returning User: Instant 0ms bypass using cached attribution (except debug builds to allow testing)
        if (pref.isAttributionResolved() && !com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            String cachedType = pref.getAcquisitionType();
            Log.d(TAG, "Returning User detected: " + cachedType);
            if (callback != null) callback.onAttributionFinished(cachedType);
            return;
        }

        final AtomicBoolean isDone = new AtomicBoolean(false);
        final Handler handler = new Handler(Looper.getMainLooper());

        // 2. 2000ms Watchdog: Prevents splash from hanging if Play Store API is slow
        final Runnable watchdogRunnable = () -> {
            if (isDone.compareAndSet(false, true)) {
                pref.setAcquisitionType("organic");
                pref.setAttributionResolved(false);
                Log.d(TAG, "Attribution watchdog timed out (2000ms), fallback to organic");
                if (callback != null) callback.onAttributionFinished("organic");
            }
        };
        handler.postDelayed(watchdogRunnable, 2000);

        try {
            final InstallReferrerClient referrerClient = InstallReferrerClient.newBuilder(context).build();
            referrerClient.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    handler.removeCallbacks(watchdogRunnable);
                    if (isDone.get()) return;

                    String rawReferrer = "";
                    String type = "organic";

                    if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                        try {
                            ReferrerDetails response = referrerClient.getInstallReferrer();
                            if (response != null) {
                                rawReferrer = response.getInstallReferrer();
                                type = classifyReferrer(rawReferrer);
                            }
                        } catch (Exception ignored) {}
                    }

                    try {
                        referrerClient.endConnection();
                    } catch (Exception ignored) {}

                    if (isDone.compareAndSet(false, true)) {
                        pref.setAcquisitionType(type);

                        // Lock attribution if definitively paid or organic
                        boolean resolved = "paid".equals(type) || "organic".equals(type);
                        pref.setAttributionResolved(resolved);
                        Log.d(TAG, "Attribution resolved: " + type + " (raw: " + rawReferrer + ")");
                        if (callback != null) callback.onAttributionFinished(type);
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    handler.removeCallbacks(watchdogRunnable);
                    if (isDone.compareAndSet(false, true)) {
                        pref.setAcquisitionType("organic");
                        Log.d(TAG, "Install referrer disconnected, fallback to organic");
                        if (callback != null) callback.onAttributionFinished("organic");
                    }
                }
            });
        } catch (Exception e) {
            handler.removeCallbacks(watchdogRunnable);
            if (isDone.compareAndSet(false, true)) {
                pref.setAcquisitionType("organic");
                Log.e(TAG, "Error connecting to Install Referrer: " + e.getMessage(), e);
                if (callback != null) callback.onAttributionFinished("organic");
            }
        }
    }

    /**
     * Identifies whether the referral indicates a Paid campaign (Google Ads, Facebook, etc.) or Organic installation.
     */
    public static String classifyReferrer(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "organic";
        String decoded = raw.trim();
        try {
            decoded = URLDecoder.decode(decoded, "UTF-8");
        } catch (Exception ignored) {}

        // A. PAID USER detection (Google Ads gclid, gad_source, paid utm tags)
        if (decoded.contains("gclid=")) return "paid";
        if (decoded.contains("gad_source=1")) return "paid";
        if (decoded.contains("utm_medium=cpc") || decoded.contains("utm_medium=paid")
                || decoded.contains("utm_medium=cpm")) {
            return "paid";
        }
        if (decoded.contains("utm_medium=ad&") || decoded.endsWith("utm_medium=ad")) {
            return "paid";
        }

        // B. ORGANIC USER detection (Default for all regular installations)
        return "organic";
    }
}
