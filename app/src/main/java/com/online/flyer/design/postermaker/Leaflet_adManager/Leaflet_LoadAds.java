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
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(bannerId);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d("AdManager", "Banner Ad Loaded");
                mainLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                Log.d("AdManager", "Banner Ad Failed: " + adError.getMessage());
                mainLayout.setVisibility(View.GONE);
            }
        });

        mainLayout.removeAllViews();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mainLayout.addView(adView, params);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
