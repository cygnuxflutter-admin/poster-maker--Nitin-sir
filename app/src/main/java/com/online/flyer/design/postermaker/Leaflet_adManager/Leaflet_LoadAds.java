package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class Leaflet_LoadAds {
    public static void loadAdmobBannerAd(Activity activity, RelativeLayout mainLayout) {
        if (activity == null || mainLayout == null) return;
        
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(activity);
        String bannerId = pref.getAdsId("BannerAdunitID");
        
        if (BuildConfig.DEBUG) {
            bannerId = "ca-app-pub-3940256099942544/6300978111";
            Log.d("AdManager", "Debug Mode: Using Test Banner ID");
        } else {
            Log.d("AdManager", "Fetching Banner Ad from Firebase (via Prefs): " + bannerId);
        }

        if (bannerId == null || bannerId.trim().isEmpty() || bannerId.equals("null")) {
            mainLayout.setVisibility(View.GONE);
            return;
        }

        AdView adView = new AdView(activity);
        
        // Calculate full-width adaptive banner size
        android.util.DisplayMetrics outMetrics = new android.util.DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int adWidth = (int) (outMetrics.widthPixels / outMetrics.density);
        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
        
        adView.setAdSize(adSize);
        adView.setAdUnitId(bannerId);

        mainLayout.removeAllViews();

        // Add Shimmer layout as a loading indicator
        View shimmerView = android.view.LayoutInflater.from(activity).inflate(com.online.flyer.design.postermaker.R.layout.leaflet_banner_shimmer, mainLayout, false);
        RelativeLayout.LayoutParams progressParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int padPx = (int) (8 * activity.getResources().getDisplayMetrics().density); // 8dp gap
        progressParams.setMargins(0, padPx, 0, padPx);
        progressParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.addView(shimmerView, progressParams);

        // Create a clear, visible line (pati) at the TOP of the ad container
        android.view.View topLine = new android.view.View(activity);
        topLine.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E")); // Dark grey line
        RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (1.5 * activity.getResources().getDisplayMetrics().density)); // 1.5dp thickness
        topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mainLayout.addView(topLine, topParams);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, padPx, 0, padPx);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.addView(adView, params);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("AdManager", "Banner Ad Loaded");
                shimmerView.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.d("AdManager", "Banner Ad Failed: " + adError.getMessage());
                // User requested NOT to hide the box if AdMob fails, keep the loader spinning
                adView.setVisibility(View.GONE);
                shimmerView.setVisibility(View.VISIBLE);
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mainLayout.setVisibility(View.VISIBLE); // Show shimmer immediately
        adView.loadAd(adRequest);
    }
}
