package com.online.flyer.design.postermaker.Leaflet_adManager;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private static final long FOREGROUND_SHOW_DELAY_MS = 400L;
    private static final long FOREGROUND_FOCUS_RETRY_MS = 300L;
    private static final long FOREGROUND_JIT_TIMEOUT_MS = 3500L;

    private static boolean isLoadingAd = false;
    private static boolean isLoadingSplashAd = false;
    private AppOpenAd appOpenAd = null;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final Leaflet_MyApplication myApplication;
    private static boolean isShowingAd = false;
    private Activity currentActivity;
    private long loadTime = 0;
    private static Leaflet_PreferenceClass preferenceClass;
    private String AD_UNIT_ID1, AD_UNIT_ID2;

    // Handlers & Active Callback Buffers
    private final Handler fgShowHandler = new Handler(Looper.getMainLooper());
    private PendingSplashWaiter pendingSplashWaiter = null;
    private PendingSplashWaiter pendingForegroundWaiter = null;
    private android.app.Dialog fgLoadingDialog = null;
    private boolean hasSplashFetchAttempted = false;
    private boolean lastSplashFetchFailed = false;

    private interface PendingSplashWaiter {
        void onAdReady(@NonNull AppOpenAd ad);
        void onAdFailed();
    }

    // External Intent & Cooldown Tracking
    private static boolean isExternalIntentActive = false;
    private static long externalIntentTime = 0;
    private static boolean isAdClickActive = false;
    private static long adClickTime = 0;
    private static long backgroundEnteredTime = 0;
    private static long lastAdDismissedTime = 0;

    public static void setExternalIntentActive(boolean active) {
        isExternalIntentActive = active;
        if (active) {
            externalIntentTime = System.currentTimeMillis();
            Log.d(LOG_TAG, "setExternalIntentActive: true at " + externalIntentTime);
        }
    }

    public static void setAdClickActive(boolean active) {
        isAdClickActive = active;
        if (active) {
            adClickTime = System.currentTimeMillis();
            Log.d(LOG_TAG, "setAdClickActive: true at " + adClickTime);
        }
    }

    public static void setLastAdDismissedTime(long time) {
        lastAdDismissedTime = time;
    }

    public static long getLastAdDismissedTime() {
        return lastAdDismissedTime;
    }

    public static boolean isAppOpenAdShowing() {
        return isShowingAd;
    }

    /**
     * Constructor
     */
    public Leaflet_AppOpenManager(Leaflet_MyApplication myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    /**
     * Reset splash session flags on fresh SplashScreen launch.
     */
    public void resetSplashState() {
        pendingSplashWaiter = null;
        pendingForegroundWaiter = null;
        hasSplashFetchAttempted = false;
        lastSplashFetchFailed = false;
        dismissFgLoadingDialog();
        fgShowHandler.removeCallbacksAndMessages(null);
    }

    private void attachRevenueListener(AppOpenAd ad, final String unitId) {
        if (ad == null) return;
        ad.setOnPaidEventListener(adValue -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logAdRevenue(
                    myApplication, adValue, unitId, "app_open"
            );
        });
    }

    /**
     * Request a foreground App Open ad (called JIT on background transition).
     */
    public void fetchAd() {
        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }

        // 1. If Foreground App Open is disabled, NEVER send any ad request!
        if (!preferenceClass.isForegroundAppOpenEnabled()) {
            Log.d(LOG_TAG, "fetchAd: Foreground App Open is disabled in Remote Config. Skipping fetch.");
            notifyForegroundWaiterFailed();
            return;
        }

        AD_UNIT_ID1 = preferenceClass.getForegroundAppOpenAdId();
        if (AD_UNIT_ID1 == null || AD_UNIT_ID1.trim().isEmpty() || AD_UNIT_ID1.equals("null")) {
            Log.d(LOG_TAG, "fetchAd: Foreground App Open ID is null or empty. Skipping fetch.");
            notifyForegroundWaiterFailed();
            return;
        }

        if (isAdAvailable()) {
            if (pendingForegroundWaiter != null && appOpenAd != null) {
                PendingSplashWaiter waiter = pendingForegroundWaiter;
                pendingForegroundWaiter = null;
                waiter.onAdReady(appOpenAd);
            }
            return;
        }

        if (isLoadingAd || isLoadingSplashAd) {
            return;
        }

        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            AD_UNIT_ID1 = "ca-app-pub-3940256099942544/9257395921";
            Log.d(LOG_TAG, "fetchAd: Debug Mode: Using Test AppOpen ID");
        } else {
            Log.d(LOG_TAG, "fetchAd: Fetching AppOpen Ad with ID: " + AD_UNIT_ID1);
        }

        final String requestUnitId = AD_UNIT_ID1;
        isLoadingAd = true;
        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                isLoadingAd = false;
                Leaflet_AppOpenManager.this.appOpenAd = ad;
                attachRevenueListener(ad, requestUnitId);
                Leaflet_AppOpenManager.this.loadTime = (new Date()).getTime();
                Log.d(LOG_TAG, "fetchAd: AppOpen loaded successfully.");
                if (pendingForegroundWaiter != null) {
                    PendingSplashWaiter waiter = pendingForegroundWaiter;
                    pendingForegroundWaiter = null;
                    waiter.onAdReady(ad);
                }
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                isLoadingAd = false;
                Log.d(LOG_TAG, "fetchAd: AppOpen failed to load - " + loadAdError.getMessage());
                notifyForegroundWaiterFailed();
            }
        };

        try {
            AdRequest request = getAdRequest();
            AppOpenAd.load(myApplication, requestUnitId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
        } catch (Exception e) {
            isLoadingAd = false;
            Log.e(LOG_TAG, "fetchAd error: " + e.getMessage());
            notifyForegroundWaiterFailed();
        }
    }

    private void notifyForegroundWaiterFailed() {
        if (pendingForegroundWaiter != null) {
            PendingSplashWaiter waiter = pendingForegroundWaiter;
            pendingForegroundWaiter = null;
            waiter.onAdFailed();
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Checks if cached App Open ad is valid regardless of foreground toggle (used by Splash).
     */
    public boolean isSplashAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    /**
     * Utility method that checks if foreground ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        if (preferenceClass != null && !preferenceClass.isForegroundAppOpenEnabled()) {
            return false;
        }
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    public void sendRequest() {
        if (!isShowingAd && isAdAvailable()) {
            if (Leaflet_MyApplication.isShowingAppOpen) {
                // Already cached
            }
        } else {
            fetchAd();
        }
    }

    /**
     * Shows the ad on foreground transition if conditions are met (with 400ms safe hold + window focus check).
     */
    public void showAdIfAvailable() {
        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }

        if (!preferenceClass.isForegroundAppOpenEnabled()) {
            Log.d(LOG_TAG, "showAdIfAvailable: Foreground App Open is disabled via Remote Config.");
            return;
        }

        // Must have actually gone to background (ON_STOP) first
        if (backgroundEnteredTime <= 0) {
            return;
        }

        // 1. Suppress on Ad Click
        if (isAdClickActive) {
            isAdClickActive = false;
            if (preferenceClass.isSuppressOnAdClick()) {
                Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to suppress_on_ad_click");
                return;
            }
        }

        // 2. Suppress on External Intent (Camera, Gallery, File Picker, etc.)
        if (isExternalIntentActive) {
            isExternalIntentActive = false;
            if (preferenceClass.isSuppressOnIntent()) {
                Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to suppress_on_intent");
                return;
            }
            int galleryCooldown = preferenceClass.getGalleryCooldownSeconds();
            if (galleryCooldown > 0 && externalIntentTime > 0) {
                long elapsedSec = (System.currentTimeMillis() - externalIntentTime) / 1000L;
                if (elapsedSec < galleryCooldown) {
                    Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to gallery_cooldown_seconds (" + elapsedSec + "s < " + galleryCooldown + "s)");
                    return;
                }
            }
        }

        // 3. Minimum Background Duration (prevents showing on brief notification pull-down / < 5s switches)
        int minBgSec = preferenceClass.getMinBackgroundSeconds();
        if (minBgSec > 0) {
            long timeInBackgroundSec = (System.currentTimeMillis() - backgroundEnteredTime) / 1000L;
            if (timeInBackgroundSec < minBgSec) {
                Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to min_background_seconds (background time: " + timeInBackgroundSec + "s < " + minBgSec + "s)");
                return;
            }
        }

        // 4. Foreground Cooldown (Background duration & Since last ad dismissed)
        int cooldownSec = preferenceClass.getForegroundCooldownSeconds();
        if (cooldownSec > 0) {
            long timeInBackgroundSec = (System.currentTimeMillis() - backgroundEnteredTime) / 1000L;
            if (timeInBackgroundSec < cooldownSec) {
                Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to cooldown_seconds (background time: " + timeInBackgroundSec + "s < " + cooldownSec + "s)");
                return;
            }
            if (lastAdDismissedTime > 0) {
                long timeSinceDismissSec = (System.currentTimeMillis() - lastAdDismissedTime) / 1000L;
                if (timeSinceDismissSec < cooldownSec) {
                    Log.d(LOG_TAG, "showAdIfAvailable: Suppressed due to cooldown_seconds (time since last ad: " + timeSinceDismissSec + "s < " + cooldownSec + "s)");
                    return;
                }
            }
        }

        if (isShowingAd) {
            Log.d(LOG_TAG, "showAdIfAvailable: Already showing an ad.");
            return;
        }

        if (!isActivityReadyForAd(currentActivity)) {
            return;
        }

        fgShowHandler.removeCallbacksAndMessages(null);

        // 5a. 0ms Cache Hit (e.g. cached from earlier): hold 400ms for focus settle and show
        if (isAdAvailable()) {
            fgShowHandler.postDelayed(() -> attemptShowForegroundAd(0), FOREGROUND_SHOW_DELAY_MS);
            return;
        }

        // 5b. Pure JIT Foreground Load: only request App Open Ad AFTER user actually returned and passed all checks!
        // Eliminates wasted requests from onStop() when users exit the app permanently.
        final Activity targetActivity = currentActivity;
        showFgLoadingDialog(targetActivity);

        final AtomicBoolean waiterDone = new AtomicBoolean(false);
        final Runnable timeoutRunnable = () -> {
            if (waiterDone.compareAndSet(false, true)) {
                pendingForegroundWaiter = null;
                dismissFgLoadingDialog();
                Log.d(LOG_TAG, "showAdIfAvailable: JIT foreground timeout (" + FOREGROUND_JIT_TIMEOUT_MS + "ms) reached.");
            }
        };
        fgShowHandler.postDelayed(timeoutRunnable, FOREGROUND_JIT_TIMEOUT_MS);

        pendingForegroundWaiter = new PendingSplashWaiter() {
            @Override
            public void onAdReady(@NonNull AppOpenAd ad) {
                if (waiterDone.compareAndSet(false, true)) {
                    fgShowHandler.removeCallbacks(timeoutRunnable);
                    dismissFgLoadingDialog();
                    attemptShowForegroundAd(0);
                }
            }

            @Override
            public void onAdFailed() {
                if (waiterDone.compareAndSet(false, true)) {
                    fgShowHandler.removeCallbacks(timeoutRunnable);
                    dismissFgLoadingDialog();
                }
            }
        };

        if (!isLoadingAd && !isLoadingSplashAd) {
            fetchAd();
        }
    }

    private boolean isActivityReadyForAd(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            Log.d(LOG_TAG, "isActivityReadyForAd: activity is null or finishing.");
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            Log.d(LOG_TAG, "isActivityReadyForAd: activity is destroyed.");
            return false;
        }
        if (activity.getClass().getName().contains("AdActivity")) {
            Log.d(LOG_TAG, "isActivityReadyForAd: Suppressed because activity is AdActivity (Ad-on-Ad protection).");
            return false;
        }
        if (myApplication instanceof Leaflet_MyApplication) {
            Leaflet_InterstitialAdManager iam = myApplication.getInterstitialAdManager();
            if (iam != null && iam.isShowingAd()) {
                Log.d(LOG_TAG, "isActivityReadyForAd: Suppressed because Interstitial Ad is already showing.");
                return false;
            }
        }
        if (activity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_SplashScreen
                || activity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_LoadingActivity) {
            Log.d(LOG_TAG, "isActivityReadyForAd: Suppressed on Splash/Loading activity.");
            return false;
        }
        return true;
    }

    private void attemptShowForegroundAd(final int retryCount) {
        if (isShowingAd || !isAdAvailable() || !Leaflet_MyApplication.isShowingAppOpen) {
            return;
        }

        final Activity targetActivity = currentActivity;
        if (!isActivityReadyForAd(targetActivity)) {
            return;
        }

        if (!targetActivity.hasWindowFocus()) {
            if (retryCount < 2) {
                Log.d(LOG_TAG, "attemptShowForegroundAd: Waiting for window focus (retry " + (retryCount + 1) + ")");
                fgShowHandler.postDelayed(() -> attemptShowForegroundAd(retryCount + 1), FOREGROUND_FOCUS_RETRY_MS);
            } else {
                Log.d(LOG_TAG, "attemptShowForegroundAd: Activity never gained window focus. Keeping ad cached safely.");
            }
            return;
        }

        final AppOpenAd adToShow = this.appOpenAd;
        if (adToShow == null) {
            return;
        }

        this.appOpenAd = null;
        isShowingAd = true;

        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                isShowingAd = false;
                lastAdDismissedTime = System.currentTimeMillis();
                // App Open Ad loads JIT in onStop() only
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                isShowingAd = false;
                Log.w(LOG_TAG, "Foreground AppOpen failed to show: " + adError.getMessage());
            }

            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                setAdClickActive(true);
            }
        });

        try {
            adToShow.show(targetActivity);
        } catch (Throwable t) {
            isShowingAd = false;
            Log.e(LOG_TAG, "Error showing Foreground AppOpen: " + t.getMessage());
        }
    }

    /**
     * Pre-fetch App Open ad for the Splash Screen in the background as early as possible.
     */
    public void fetchSplashAd() {
        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }

        if (preferenceClass.getInt("splashscreen", 1) == 0 || !preferenceClass.getBoolean("splashscreen", true)) {
            Log.d(LOG_TAG, "fetchSplashAd: Splash ads disabled via splashscreen=0.");
            notifySplashWaiterFailed();
            return;
        }

        if (!preferenceClass.isSplashAppOpenEnabled()) {
            Log.d(LOG_TAG, "fetchSplashAd: Splash App Open disabled via Remote Config.");
            notifySplashWaiterFailed();
            return;
        }

        AD_UNIT_ID1 = preferenceClass.getSplashAppOpenAdId();
        if (AD_UNIT_ID1 == null || AD_UNIT_ID1.trim().isEmpty() || AD_UNIT_ID1.equals("null")) {
            Log.d(LOG_TAG, "fetchSplashAd: Splash App Open ID is null or empty.");
            notifySplashWaiterFailed();
            return;
        }

        if (isSplashAdAvailable()) {
            Log.d(LOG_TAG, "fetchSplashAd: Already cached.");
            if (pendingSplashWaiter != null && appOpenAd != null) {
                PendingSplashWaiter waiter = pendingSplashWaiter;
                pendingSplashWaiter = null;
                waiter.onAdReady(appOpenAd);
            }
            return;
        }

        if (isLoadingSplashAd) {
            Log.d(LOG_TAG, "fetchSplashAd: Already loading.");
            return;
        }

        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            AD_UNIT_ID1 = "ca-app-pub-3940256099942544/9257395921";
            Log.d(LOG_TAG, "fetchSplashAd: Debug Mode: Using Test AppOpen ID: " + AD_UNIT_ID1);
        }

        final String requestUnitId = AD_UNIT_ID1;
        isLoadingSplashAd = true;
        hasSplashFetchAttempted = true;
        lastSplashFetchFailed = false;
        Log.d(LOG_TAG, "fetchSplashAd: Pre-fetching Splash AppOpen ad in background with ID: " + requestUnitId);

        try {
            AdRequest request = getAdRequest();
            AppOpenAd.load(myApplication, requestUnitId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull AppOpenAd ad) {
                            isLoadingSplashAd = false;
                            lastSplashFetchFailed = false;
                            Leaflet_AppOpenManager.this.appOpenAd = ad;
                            attachRevenueListener(ad, requestUnitId);
                            Leaflet_AppOpenManager.this.loadTime = (new Date()).getTime();
                            Log.d(LOG_TAG, "fetchSplashAd: Splash AppOpen loaded successfully!");

                            if (pendingSplashWaiter != null) {
                                PendingSplashWaiter waiter = pendingSplashWaiter;
                                pendingSplashWaiter = null;
                                waiter.onAdReady(ad);
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            isLoadingSplashAd = false;
                            lastSplashFetchFailed = true;
                            Log.d(LOG_TAG, "fetchSplashAd: Pre-fetch failed - " + loadAdError.getMessage());

                            if (pendingSplashWaiter != null) {
                                PendingSplashWaiter waiter = pendingSplashWaiter;
                                pendingSplashWaiter = null;
                                waiter.onAdFailed();
                            }
                        }
                    });
        } catch (Exception e) {
            isLoadingSplashAd = false;
            lastSplashFetchFailed = true;
            Log.e(LOG_TAG, "fetchSplashAd error: " + e.getMessage());
            notifySplashWaiterFailed();
        }
    }

    private void notifySplashWaiterFailed() {
        if (pendingSplashWaiter != null) {
            PendingSplashWaiter waiter = pendingSplashWaiter;
            pendingSplashWaiter = null;
            waiter.onAdFailed();
        }
    }

    public void showAdIfSplashAvailable(@NonNull final Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }

        final AtomicBoolean isAdCallbackDone = new AtomicBoolean(false);
        final Handler watchdogHandler = new Handler(Looper.getMainLooper());
        final Runnable safeNotifyComplete = () -> {
            pendingSplashWaiter = null;
            if (isAdCallbackDone.compareAndSet(false, true)) {
                watchdogHandler.removeCallbacksAndMessages(null);
                onShowAdCompleteListener.onShowAdComplete();
            }
        };

        if (preferenceClass.getInt("splashscreen", 1) == 0 || !preferenceClass.getBoolean("splashscreen", true)) {
            Log.d(LOG_TAG, "showAdIfSplashAvailable: Splash ads disabled via splashscreen=0.");
            safeNotifyComplete.run();
            return;
        }

        // 1. 0ms RAM Cache Hit: If App Open Ad is ALREADY cached in memory -> SHOW IT IMMEDIATELY!
        if (!isShowingAd && isSplashAdAvailable() && preferenceClass.isSplashAppOpenEnabled()) {
            Log.d(LOG_TAG, "showAdIfSplashAvailable: 0ms RAM Cache Hit! Showing pre-cached App Open Ad.");
            final AppOpenAd cachedAd = this.appOpenAd;
            this.appOpenAd = null;
            showSplashAppOpenAd(activity, cachedAd, safeNotifyComplete, watchdogHandler, isAdCallbackDone, 0);
            return;
        }

        final long watchdogTimeoutMs = preferenceClass.getSplashWatchdogTimeoutMs();

        // 2. If Splash App Open is disabled -> try Fallback Interstitial only
        if (!preferenceClass.isSplashAppOpenEnabled()) {
            Log.d(LOG_TAG, "showAdIfSplashAvailable: Splash App Open disabled, checking fallback interstitial.");
            watchdogHandler.postDelayed(() -> {
                Log.w(LOG_TAG, "showAdIfSplashAvailable: Fallback watchdog (" + watchdogTimeoutMs + "ms) reached.");
                safeNotifyComplete.run();
            }, watchdogTimeoutMs);
            loadFallbackInterstitialWithWatchdog(activity, safeNotifyComplete, watchdogHandler, isAdCallbackDone);
            return;
        }

        // 3. If previous splash fetch already attempted & failed -> go directly to Fallback Interstitial (do NOT hijack while App Open is still loading!)
        if (hasSplashFetchAttempted && lastSplashFetchFailed && !isLoadingSplashAd) {
            Log.d(LOG_TAG, "showAdIfSplashAvailable: App Open failed earlier; trying fallback interstitial.");
            if (preferenceClass.isSplashFallbackInterstitialEnabled() && preferenceClass.isGlobalInterstitialEnabled()) {
                watchdogHandler.postDelayed(() -> {
                    Log.w(LOG_TAG, "showAdIfSplashAvailable: Fallback watchdog (" + watchdogTimeoutMs + "ms) reached.");
                    safeNotifyComplete.run();
                }, watchdogTimeoutMs);
                loadFallbackInterstitialWithWatchdog(activity, safeNotifyComplete, watchdogHandler, isAdCallbackDone);
            } else {
                safeNotifyComplete.run();
            }
            return;
        }

        // 4. Active Callback Buffer (pendingSplashWaiter):
        // Attach to the ALREADY-RUNNING fetchSplashAd() request (or trigger one single fetch if not started yet).
        // Zero duplicate AdMob requests and Fallback Interstitial will NOT steal the slot while App Open is loading!
        Log.d(LOG_TAG, "showAdIfSplashAvailable: App Open still loading (isLoading=" + isLoadingSplashAd + "). Waiting up to " + watchdogTimeoutMs + "ms via active callback.");
        watchdogHandler.postDelayed(() -> {
            Log.w(LOG_TAG, "showAdIfSplashAvailable: Watchdog timeout (" + watchdogTimeoutMs + "ms) reached! Proceeding.");
            safeNotifyComplete.run();
        }, watchdogTimeoutMs);

        pendingSplashWaiter = new PendingSplashWaiter() {
            @Override
            public void onAdReady(@NonNull AppOpenAd ad) {
                if (isAdCallbackDone.get() || activity.isFinishing() ||
                        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
                    Log.d(LOG_TAG, "pendingSplashWaiter: Ad arrived after watchdog/finish. Retaining in cache.");
                    return;
                }
                watchdogHandler.removeCallbacksAndMessages(null);
                Leaflet_AppOpenManager.this.appOpenAd = null;
                showSplashAppOpenAd(activity, ad, safeNotifyComplete, watchdogHandler, isAdCallbackDone, 0);
            }

            @Override
            public void onAdFailed() {
                if (isAdCallbackDone.get() || activity.isFinishing() ||
                        (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
                    return;
                }
                Log.d(LOG_TAG, "pendingSplashWaiter: App Open failed; trying fallback interstitial.");
                loadFallbackInterstitialWithWatchdog(activity, safeNotifyComplete, watchdogHandler, isAdCallbackDone);
            }
        };

        if (!isLoadingSplashAd && !isSplashAdAvailable()) {
            fetchSplashAd();
        }
    }

    private void showSplashAppOpenAd(@NonNull final Activity activity,
                                     @NonNull final AppOpenAd adToShow,
                                     @NonNull final Runnable safeNotifyComplete,
                                     @NonNull final Handler watchdogHandler,
                                     @NonNull final AtomicBoolean isAdCallbackDone,
                                     final int focusRetry) {
        if (isAdCallbackDone.get() || activity.isFinishing() ||
                (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
            this.appOpenAd = adToShow; // Restore to cache if activity exited
            safeNotifyComplete.run();
            return;
        }

        if (!activity.hasWindowFocus() && focusRetry < 2) {
            Log.d(LOG_TAG, "showSplashAppOpenAd: Waiting briefly for window focus (retry " + (focusRetry + 1) + ")");
            watchdogHandler.postDelayed(() -> showSplashAppOpenAd(activity, adToShow, safeNotifyComplete, watchdogHandler, isAdCallbackDone, focusRetry + 1), 150L);
            return;
        }

        if (isShowingAd) {
            this.appOpenAd = adToShow;
            safeNotifyComplete.run();
            return;
        }

        isShowingAd = true;
        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                isShowingAd = false;
                lastAdDismissedTime = System.currentTimeMillis();
                safeNotifyComplete.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                isShowingAd = false;
                Log.w(LOG_TAG, "Splash AppOpen failed to show: " + adError.getMessage());
                safeNotifyComplete.run();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                isShowingAd = true;
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                setAdClickActive(true);
            }
        });

        try {
            adToShow.show(activity);
        } catch (Throwable t) {
            isShowingAd = false;
            Log.e(LOG_TAG, "Error showing Splash AppOpen: " + t.getMessage());
            safeNotifyComplete.run();
        }
    }

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        if (!isShowingAd && isAdAvailable()) {
            final AppOpenAd adToShow = this.appOpenAd;
            if (adToShow == null) {
                onShowAdCompleteListener.onShowAdComplete();
                return;
            }
            if (activity.isFinishing() || (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())) {
                onShowAdCompleteListener.onShowAdComplete();
                return;
            }

            this.appOpenAd = null;
            isShowingAd = true;

            adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    isShowingAd = false;
                    lastAdDismissedTime = System.currentTimeMillis();
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    isShowingAd = false;
                    onShowAdCompleteListener.onShowAdComplete();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    setAdClickActive(true);
                }
            });

            try {
                adToShow.show(activity);
            } catch (Throwable t) {
                isShowingAd = false;
                Log.e(LOG_TAG, "Error showing AppOpen: " + t.getMessage());
                onShowAdCompleteListener.onShowAdComplete();
            }
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
        if (currentActivity == activity) {
            dismissFgLoadingDialog();
            currentActivity = null;
        }
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        if (!Leaflet_MyApplication.isAdsSplash) {
            showAdIfAvailable();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        backgroundEnteredTime = System.currentTimeMillis();
        pendingForegroundWaiter = null;
        dismissFgLoadingDialog();
        fgShowHandler.removeCallbacksAndMessages(null);
        Log.d(LOG_TAG, "App entered background at: " + backgroundEnteredTime + " (zero background ad fetch)");
    }

    private void showFgLoadingDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) return;
        dismissFgLoadingDialog();
        try {
            android.app.Dialog dlg = new android.app.Dialog(activity);
            dlg.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            android.view.View dialogView = android.view.LayoutInflater.from(activity)
                    .inflate(com.online.flyer.design.postermaker.R.layout.leaflet_lottie_anim_dialog, null);
            android.widget.TextView tv = dialogView.findViewById(com.online.flyer.design.postermaker.R.id.textView);
            if (tv != null) {
                tv.setText("Opening...");
                tv.setTextColor(android.graphics.Color.WHITE);
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                tv.setShadowLayer(4f, 0f, 1f, android.graphics.Color.BLACK);
                if (tv.getLayoutParams() instanceof android.widget.RelativeLayout.LayoutParams) {
                    android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) tv.getLayoutParams();
                    lp.addRule(android.widget.RelativeLayout.CENTER_HORIZONTAL);
                    lp.topMargin = (int) android.util.TypedValue.applyDimension(
                            android.util.TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
                    tv.setLayoutParams(lp);
                }
                tv.setVisibility(android.view.View.VISIBLE);
            }
            dlg.setContentView(dialogView);
            if (dlg.getWindow() != null) {
                dlg.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            dlg.show();
            fgLoadingDialog = dlg;
        } catch (Throwable t) {
            Log.w(LOG_TAG, "showFgLoadingDialog skipped: " + t.getMessage());
            fgLoadingDialog = null;
        }
    }

    private void dismissFgLoadingDialog() {
        try {
            if (fgLoadingDialog != null) {
                if (fgLoadingDialog.isShowing()) {
                    fgLoadingDialog.dismiss();
                }
                fgLoadingDialog = null;
            }
        } catch (Throwable ignored) {
            fgLoadingDialog = null;
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

    private void loadFallbackInterstitialWithWatchdog(@NonNull Activity activity,
                                                      @NonNull Runnable safeNotifyComplete,
                                                      @NonNull Handler watchdogHandler,
                                                      @NonNull AtomicBoolean isAdCallbackDone) {
        if (preferenceClass == null) {
            preferenceClass = new Leaflet_PreferenceClass(myApplication);
        }

        if (!preferenceClass.isSplashFallbackInterstitialEnabled()
                || !preferenceClass.isGlobalInterstitialEnabled()
                || !preferenceClass.canShowInterstitialAd()) {
            safeNotifyComplete.run();
            return;
        }

        if (myApplication instanceof Leaflet_MyApplication) {
            Leaflet_InterstitialAdManager iam = myApplication.getInterstitialAdManager();
            if (iam != null && (iam.isAdmobAdAvailable() || iam.isLoading())) {
                Log.d(LOG_TAG, "loadFallbackInterstitialWithWatchdog: Shared interstitial available or actively loading. Delegating!");
                watchdogHandler.removeCallbacksAndMessages(null);
                iam.loadAndShowAdmob(activity, safeNotifyComplete::run);
                return;
            }
        }

        String interstitialId = preferenceClass.getSplashFallbackInterstitialAdId();
        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            interstitialId = "ca-app-pub-3940256099942544/1033173712";
        }

        if (interstitialId == null || interstitialId.trim().isEmpty() || interstitialId.equals("null")) {
            safeNotifyComplete.run();
            return;
        }

        Log.d(LOG_TAG, "Trying fallback Interstitial with ID: " + interstitialId);
        AdRequest adRequest = new AdRequest.Builder().build();
        com.google.android.gms.ads.interstitial.InterstitialAd.load(myApplication, interstitialId, adRequest,
                new com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                        if (myApplication instanceof Leaflet_MyApplication) {
                            myApplication.getInterstitialAdManager().setAdmobInterstitialAd(interstitialAd);
                        }

                        if (isAdCallbackDone.get() || activity.isFinishing() || activity.isDestroyed()) {
                            Log.d(LOG_TAG, "Fallback Interstitial loaded after watchdog/finish. Retaining for next click.");
                            return;
                        }

                        watchdogHandler.removeCallbacksAndMessages(null);
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                super.onAdShowedFullScreenContent();
                                preferenceClass.recordInterstitialAdShown();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                lastAdDismissedTime = System.currentTimeMillis();
                                if (myApplication instanceof Leaflet_MyApplication) {
                                    myApplication.getInterstitialAdManager().setAdmobInterstitialAd(null);
                                }
                                safeNotifyComplete.run();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                if (myApplication instanceof Leaflet_MyApplication) {
                                    myApplication.getInterstitialAdManager().setAdmobInterstitialAd(null);
                                }
                                safeNotifyComplete.run();
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                setAdClickActive(true);
                            }
                        });
                        if (activity != null && !activity.isFinishing()) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                                safeNotifyComplete.run();
                                return;
                            }
                            try {
                                interstitialAd.show(activity);
                            } catch (Throwable t) {
                                Log.e(LOG_TAG, "Error showing fallback Interstitial: " + t.getMessage());
                                safeNotifyComplete.run();
                            }
                        } else {
                            safeNotifyComplete.run();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError fallbackError) {
                        Log.d(LOG_TAG, "Fallback Interstitial load failed: " + fallbackError.getMessage());
                        safeNotifyComplete.run();
                    }
                });
    }

    private void loadFallbackInterstitial(@NonNull Activity activity, @NonNull Leaflet_MyApplication.OnShowAdCompleteListener onShowAdCompleteListener) {
        AtomicBoolean done = new AtomicBoolean(false);
        Handler h = new Handler(Looper.getMainLooper());
        loadFallbackInterstitialWithWatchdog(activity, onShowAdCompleteListener::onShowAdComplete, h, done);
    }
}
