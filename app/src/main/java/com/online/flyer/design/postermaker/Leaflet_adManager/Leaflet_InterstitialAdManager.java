package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class Leaflet_InterstitialAdManager {

    private final String admobInterstitialAdId, fbInterstitialAdId;
    private final Context context;
    private final Leaflet_PreferenceClass preferenceClass;
    private final String adXInterstitialAdId;
    private InterstitialAd admobInterstitialAd;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private OnAdLoadInterface onAdLoadInterface;
    private boolean isFailed = false;

    public Leaflet_InterstitialAdManager(Context context) {
        this.context = context;
        preferenceClass = new Leaflet_PreferenceClass(this.context);
        admobInterstitialAdId = preferenceClass.getAdsId("InterstitalAdunitID");
        adXInterstitialAdId = preferenceClass.getAdsId("AdxInterstitalAdunitID");
        fbInterstitialAdId = preferenceClass.getAdsId("fbInterstitalAdunitID");
        // fetchAdMobAd(); // Interstitial ads disabled
    }

    private void fetchFbAd() {
        // Interstitial ads disabled
    }

    public void fetchAdMobAd() {
        // Interstitial ads disabled
    }

    public void fetchAdXAd() {
        // Interstitial ads disabled
    }


    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdmobAdAvailable() {
        return false;
    }

    public boolean isFbAdAvailable() {
        return false;
    }

    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public void showInterstitialAd(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public void showEDitAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        this.onAdLoadInterface = onAdLoadInterface;
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public interface OnAdLoadInterface {
        void onAdClose();
    }

    public interface OnRewardAdLoadInterface {
        void onAdClose();
    }
}
