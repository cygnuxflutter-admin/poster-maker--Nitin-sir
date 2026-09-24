package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class Leaflet_InterstitialAdManager {

    private String admobInterstitialAdId;
    private final Context context;
    private final Leaflet_PreferenceClass preferenceClass;
    private InterstitialAd admobInterstitialAd;
    private OnAdLoadInterface onAdLoadInterface;

    public Leaflet_InterstitialAdManager(Context context) {
        this.context = context;
        preferenceClass = new Leaflet_PreferenceClass(this.context);
        admobInterstitialAdId = preferenceClass.getAdsId("InterstitalAdunitID");
        
        if (BuildConfig.DEBUG) {
            admobInterstitialAdId = "ca-app-pub-3940256099942544/1033173712";
            Log.d("AdManager", "Debug Mode: Using Test Interstitial ID");
        } else {
            Log.d("AdManager", "Fetching Interstitial Ad from Firebase (via Prefs): " + admobInterstitialAdId);
        }
        
        fetchAdMobAd();
    }

    public void fetchAdMobAd() {
        if (admobInterstitialAdId == null || admobInterstitialAdId.trim().isEmpty() || admobInterstitialAdId.equals("null")) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, admobInterstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        admobInterstitialAd = interstitialAd;
                        Log.d("AdManager", "Interstitial Ad Loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        admobInterstitialAd = null;
                        Log.d("AdManager", "Interstitial Ad Failed: " + loadAdError.getMessage());
                    }
                });
    }

    public boolean isAdmobAdAvailable() {
        return admobInterstitialAd != null;
    }

    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        showInterstitialAd(activity, onAdLoadInterface);
    }

    public void showInterstitialAd(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;
        if (isAdmobAdAvailable()) {
            admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    admobInterstitialAd = null;
                    if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                        Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                    }
                    fetchAdMobAd(); // Preload next
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    admobInterstitialAd = null;
                    if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                        Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                    }
                    fetchAdMobAd(); // Preload next
                }
            });
            admobInterstitialAd.show(activity);
        } else {
            if (this.onAdLoadInterface != null) {
                this.onAdLoadInterface.onAdClose();
            }
        }
    }

    public void showEDitAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        showInterstitialAd(activity, onAdLoadInterface);
    }

    public interface OnAdLoadInterface {
        void onAdClose();
    }

    public interface OnRewardAdLoadInterface {
        void onAdClose();
    }
}
