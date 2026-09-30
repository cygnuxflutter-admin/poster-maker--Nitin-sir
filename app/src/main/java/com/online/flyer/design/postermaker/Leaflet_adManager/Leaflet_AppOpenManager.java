package com.online.flyer.design.postermaker.Leaflet_adManager;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class Leaflet_AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private static final String LOG_TAG = "AppOpenManager";
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final Leaflet_MyApplication myApplication;
    private static boolean isShowingAd = false;
    private Activity currentActivity;
    private long loadTime = 0;
    private static Leaflet_PreferenceClass preferenceClass;
    private String AD_UNIT_ID1, AD_UNIT_ID2;

    /**
     * Constructor
     */
    public Leaflet_AppOpenManager(Leaflet_MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                Leaflet_AppOpenManager.this.appOpenAd = ad;
                Leaflet_AppOpenManager.this.loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                // Handle the error.
            }
        };

        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }
        
        if (preferenceClass.getInt("show_app_open_ad", 1) == 0) {
            return;
        }

        AD_UNIT_ID1 = preferenceClass.getAdsId("AppOpenID");

        if (AD_UNIT_ID1 == null || AD_UNIT_ID1.trim().isEmpty() || AD_UNIT_ID1.equals("null")) {
            return;
        }

        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            AD_UNIT_ID1 = "ca-app-pub-3940256099942544/9257395921";
            Log.d("AdManager", "Debug Mode: Using Test AppOpen ID");
        } else {
            Log.d("AdManager", "Fetching AppOpen Ad from Firebase (via Prefs): " + AD_UNIT_ID1);
        }

        try {
            AdRequest request = getAdRequest();
            AppOpenAd.load(myApplication, AD_UNIT_ID1, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        } catch (Exception e) {
            // Ignore error
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        if (preferenceClass != null && preferenceClass.getInt("show_app_open_ad", 1) == 0) {
            return false;
        }
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void sendRequest() {
        if (!isShowingAd && isAdAvailable()) {
            if (Leaflet_MyApplication.isShowingAppOpen) {

            }
        } else {
            fetchAd();
        }
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            if (Leaflet_MyApplication.isShowingAppOpen) {
                FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        Leaflet_AppOpenManager.this.appOpenAd = null;
                        isShowingAd = false;
                        fetchAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        isShowingAd = true;
                    }
                };

                appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                appOpenAd.show(currentActivity);
            }
        } else {
            fetchAd();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    Leaflet_AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    fetchAd();
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(activity);
        } else {
            Log.d(LOG_TAG, "showAdIfSplashAvailable: ad not available, loading now");
            loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(AppOpenAd ad) {
                    Log.d(LOG_TAG, "showAdIfSplashAvailable: ad loaded!");
                    Leaflet_AppOpenManager.this.appOpenAd = ad;
                    Leaflet_AppOpenManager.this.loadTime = (new Date()).getTime();

                    FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Leaflet_AppOpenManager.this.appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                            onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.d(LOG_TAG, "showAdIfSplashAvailable: failed to show - " + adError.getMessage());
                            onShowAdCompleteListener.onShowAdComplete();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };
                    appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
                    if (currentActivity != null) {
                        appOpenAd.show(currentActivity);
                    } else {
                        Log.d(LOG_TAG, "showAdIfSplashAvailable: currentActivity is null, using passed activity");
                        appOpenAd.show(activity);
                    }
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    Log.d(LOG_TAG, "showAdIfSplashAvailable: AppOpen failed.");
                    loadFallbackInterstitial(activity, onShowAdCompleteListener);
                }
            };
            if (preferenceClass == null) {
                preferenceClass = new Leaflet_PreferenceClass(myApplication);
            }
            
            if (preferenceClass.getInt("show_app_open_ad", 1) == 0) {
                Log.d(LOG_TAG, "show_app_open_ad is 0, skipping App Open globally...");
                loadFallbackInterstitial(activity, onShowAdCompleteListener);
                return;
            }

            if (preferenceClass.getInt("show_splash_app_open", 1) == 0) {
                Log.d(LOG_TAG, "show_splash_app_open is 0, skipping App Open only on Splash...");
                loadFallbackInterstitial(activity, onShowAdCompleteListener);
                return;
            }

            AD_UNIT_ID1 = preferenceClass.getAdsId("AppOpenID");
            
            Log.d(LOG_TAG, "AppOpenID from prefs: " + AD_UNIT_ID1);
            if (AD_UNIT_ID1 == null || AD_UNIT_ID1.trim().isEmpty() || AD_UNIT_ID1.equals("null")) {
                Log.d(LOG_TAG, "AppOpenID is null/empty, skipping App Open...");
                loadFallbackInterstitial(activity, onShowAdCompleteListener);
                return;
            }

            if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
                AD_UNIT_ID1 = "ca-app-pub-3940256099942544/9257395921";
                Log.d(LOG_TAG, "Debug Mode: Using Test AppOpen ID: " + AD_UNIT_ID1);
            }
            
            AdRequest request = getAdRequest();
            Log.d(LOG_TAG, "Calling AppOpenAd.load with ID: " + AD_UNIT_ID1);
            AppOpenAd.load(myApplication, AD_UNIT_ID1, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        }
    }

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!isShowingAd && isAdAvailable()) {
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    Leaflet_AppOpenManager.this.appOpenAd = null;
                    isShowingAd = false;
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            };
            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(activity);
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    /**
     * ActivityLifecycleCallback methods
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        currentActivity = null;
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!Leaflet_MyApplication.isAdsSplash) {
            showAdIfAvailable();
        }
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    private void loadFallbackInterstitial(@NonNull Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (preferenceClass.getInt("show_fallback_interstitial", 0) == 0) {
            Log.d(LOG_TAG, "Fallback Interstitial is disabled via Firebase.");
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }

        Log.d(LOG_TAG, "Trying fallback Interstitial...");

        String interstitialId = preferenceClass.getAdsId("InterstitalAdunitID");
        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            interstitialId = "ca-app-pub-3940256099942544/1033173712"; // Test Interstitial
        }

        if (interstitialId == null || interstitialId.trim().isEmpty() || interstitialId.equals("null")) {
            onShowAdCompleteListener.onShowAdComplete();
            return;
        }

        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(myApplication, interstitialId, adRequest,
                new com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        interstitialAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                onShowAdCompleteListener.onShowAdComplete();
                            }
                        });
                        interstitialAd.show(activity);
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError fallbackError) {
                        Log.d(LOG_TAG, "showAdIfSplashAvailable: Fallback Interstitial also failed - " + fallbackError.getMessage());
                        onShowAdCompleteListener.onShowAdComplete();
                    }
                });
    }
}