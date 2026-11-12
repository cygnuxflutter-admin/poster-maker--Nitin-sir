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
    private final String customAdUnitId;
    private final Context context;
    private final Leaflet_PreferenceClass preferenceClass;
    private InterstitialAd admobInterstitialAd;
    private boolean isLoading = false;
    private OnAdLoadInterface onAdLoadInterface;

    public Leaflet_InterstitialAdManager(Context context) {
        this(context, null);
    }

    public Leaflet_InterstitialAdManager(Context context, String customAdUnitId) {
        this.context = context;
        this.customAdUnitId = customAdUnitId;
        preferenceClass = new Leaflet_PreferenceClass(this.context);
        if (customAdUnitId != null && !customAdUnitId.trim().isEmpty() && !customAdUnitId.equals("null") && !customAdUnitId.contains("xxxx")) {
            admobInterstitialAdId = customAdUnitId;
        } else {
            admobInterstitialAdId = preferenceClass.getAdsId("InterstitalAdunitID");
        }
        
        if (BuildConfig.DEBUG) {
            admobInterstitialAdId = "ca-app-pub-3940256099942544/1033173712";
            Log.d("AdManager", "Debug Mode: Using Test Interstitial ID");
        } else {
            Log.d("AdManager", "Fetching Interstitial Ad from Firebase (via Prefs): " + admobInterstitialAdId);
        }
    }

    public void fetchAdMobAd() {
        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(context);

        // 1. If Interstitial is disabled via Remote Config, NEVER send any ad request!
        if (!pref.isGlobalInterstitialEnabled()) {
            Log.d("AdManager", "fetchAdMobAd: Interstitial Ad is DISABLED in Remote Config. Skipping request completely.");
            return;
        }

        if (customAdUnitId == null || customAdUnitId.trim().isEmpty()) {
            String rcId = preferenceClass.getGlobalInterstitialAdId();
            if (rcId != null && !rcId.trim().isEmpty() && !rcId.equals("null") && !rcId.contains("xxxx")) {
                admobInterstitialAdId = rcId;
            }
        }
        if (BuildConfig.DEBUG) {
            admobInterstitialAdId = "ca-app-pub-3940256099942544/1033173712";
        }
        if (admobInterstitialAdId == null || admobInterstitialAdId.trim().isEmpty() || admobInterstitialAdId.equals("null")) {
            Log.d("AdManager", "fetchAdMobAd: Interstitial Ad ID is null or empty. Skipping request.");
            return;
        }
        
        if (isLoading || isAdmobAdAvailable()) {
            return; // Already loading or loaded
        }

        isLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, admobInterstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        isLoading = false;
                        admobInterstitialAd = interstitialAd;
                        Log.d("AdManager", "Interstitial Ad Loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoading = false;
                        admobInterstitialAd = null;
                        Log.d("AdManager", "Interstitial Ad Failed: " + loadAdError.getMessage());
                    }
                });
    }

    public void setAdmobInterstitialAd(InterstitialAd admobInterstitialAd) {
        this.admobInterstitialAd = admobInterstitialAd;
        Log.d("AdManager", "Cached Interstitial Ad set externally (e.g. from splash fallback). Available: " + (admobInterstitialAd != null));
    }

    public boolean isAdmobAdAvailable() {
        return admobInterstitialAd != null;
    }

    private boolean isShowingAd = false;

    public boolean isShowingAd() {
        return isShowingAd;
    }

    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        showInterstitialAd(activity, onAdLoadInterface);
    }

    public void showInterstitialAd(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(context != null ? context : activity);
        if (!pref.isGlobalInterstitialEnabled()) {
            Log.d("AdManager", "showInterstitialAd: Interstitial disabled via Remote Config.");
            if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
            return;
        }

        if (isShowingAd) {
            Log.d("AdManager", "Interstitial Ad is already showing, ignoring duplicate request");
            return;
        }
        this.onAdLoadInterface = onAdLoadInterface;
        if (isAdmobAdAvailable()) {
            isShowingAd = true;
            admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isShowingAd = false;
                    admobInterstitialAd = null;
                    Leaflet_AppOpenManager.setLastAdDismissedTime(System.currentTimeMillis());
                    if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                        Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Leaflet_AppOpenManager.setAdClickActive(true);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isShowingAd = false;
                    admobInterstitialAd = null;
                    if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                        Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                    }
                }
            });
            admobInterstitialAd.show(activity);
        } else {
            isShowingAd = false;
            if (preferenceClass != null && preferenceClass.isGlobalInterstitialEnabled()) {
                fetchAdMobAd();
            }
            if (this.onAdLoadInterface != null) {
                this.onAdLoadInterface.onAdClose();
            }
        }
    }

    public void showUnlockInterstitialAd(Activity activity, int minWatchSeconds, OnAdLoadInterface onAdLoadInterface) {
        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(context != null ? context : activity);
        if (!pref.isGlobalInterstitialEnabled()) {
            Log.d("AdManager", "showUnlockInterstitialAd: Interstitial disabled via Remote Config.");
            if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
            return;
        }

        if (isShowingAd) {
            Log.d("AdManager", "Interstitial Ad is already showing, ignoring duplicate request");
            return;
        }
        this.onAdLoadInterface = onAdLoadInterface;
        if (isAdmobAdAvailable()) {
            isShowingAd = true;
            final long startTime = System.currentTimeMillis();
            final long minDurationMs = Math.max(3000, minWatchSeconds * 1000L);

            admobInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isShowingAd = false;
                    admobInterstitialAd = null;
                    long dismissTime = System.currentTimeMillis();
                    Leaflet_AppOpenManager.setLastAdDismissedTime(dismissTime);
                    long elapsed = dismissTime - startTime;

                    if (elapsed >= minDurationMs) {
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                                    Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                                }
                            });
                        }
                    } else {
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                android.widget.Toast.makeText(activity, "Please watch the full ad to unlock pro template", android.widget.Toast.LENGTH_SHORT).show();
                                if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                                    Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdFailedToEarnReward();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Leaflet_AppOpenManager.setAdClickActive(true);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isShowingAd = false;
                    admobInterstitialAd = null;
                    if (Leaflet_InterstitialAdManager.this.onAdLoadInterface != null) {
                        Leaflet_InterstitialAdManager.this.onAdLoadInterface.onAdClose();
                    }
                }
            });
            admobInterstitialAd.show(activity);
        } else {
            isShowingAd = false;
            if (preferenceClass != null && preferenceClass.isGlobalInterstitialEnabled()) {
                fetchAdMobAd();
            }
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
        default void onAdFailedToEarnReward() {}
    }

    public interface OnRewardAdLoadInterface {
        void onAdClose();
        default void onAdFailedToEarnReward() {}
    }
}
