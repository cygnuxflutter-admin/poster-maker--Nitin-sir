package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.util.concurrent.atomic.AtomicBoolean;

public class Leaflet_InterstitialAdManager {

    private static final String TAG = "AdManager";
    private static final long INTERSTITIAL_WAIT_TIMEOUT_MS = 4000L;
    private static final long FOCUS_RETRY_DELAY_MS = 150L;

    private String admobInterstitialAdId;
    private final String customAdUnitId;
    private final Context context;
    private final Leaflet_PreferenceClass preferenceClass;
    private InterstitialAd admobInterstitialAd;
    private boolean isLoading = false;
    private boolean isShowingAd = false;
    private OnAdLoadInterface onAdLoadInterface;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private PendingLoadCallback pendingLoadCallback = null;
    private Dialog loadingDialog = null;

    private interface PendingLoadCallback {
        void onAdLoaded(@NonNull InterstitialAd ad);
        void onAdFailed();
    }

    public Leaflet_InterstitialAdManager(Context context) {
        this(context, null);
    }

    public Leaflet_InterstitialAdManager(Context context, String customAdUnitId) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.customAdUnitId = customAdUnitId;
        this.preferenceClass = new Leaflet_PreferenceClass(this.context != null ? this.context : context);
        if (customAdUnitId != null && !customAdUnitId.trim().isEmpty() && !customAdUnitId.equals("null") && !customAdUnitId.contains("xxxx")) {
            admobInterstitialAdId = customAdUnitId.trim();
        } else {
            admobInterstitialAdId = preferenceClass.getGlobalInterstitialAdId();
        }

        if (BuildConfig.DEBUG) {
            admobInterstitialAdId = "ca-app-pub-3940256099942544/1033173712";
            Log.d(TAG, "Debug Mode: Using Test Interstitial ID");
        } else {
            Log.d(TAG, "Fetching Interstitial Ad from Firebase (via Prefs): " + admobInterstitialAdId);
        }
    }

    public void fetchAdMobAd() {
        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(context);

        // 1. Always enforce global enable + daily/hourly cap BEFORE sending any AdMob network request!
        if (!pref.isGlobalInterstitialEnabled() || pref.isInterstitialDailyCapReached() || pref.isInterstitialHourlyCapReached()) {
            Log.d(TAG, "fetchAdMobAd: Interstitial Ad is DISABLED or Cap reached. Skipping request completely.");
            notifyPendingFailed();
            return;
        }

        if (customAdUnitId == null || customAdUnitId.trim().isEmpty()) {
            String rcId = pref.getGlobalInterstitialAdId();
            if (rcId != null && !rcId.trim().isEmpty() && !rcId.equals("null") && !rcId.contains("xxxx")) {
                admobInterstitialAdId = rcId;
            }
        }
        if (BuildConfig.DEBUG) {
            admobInterstitialAdId = "ca-app-pub-3940256099942544/1033173712";
        }
        if (admobInterstitialAdId == null || admobInterstitialAdId.trim().isEmpty() || admobInterstitialAdId.equals("null")) {
            Log.d(TAG, "fetchAdMobAd: Interstitial Ad ID is null or empty. Skipping request.");
            notifyPendingFailed();
            return;
        }

        if (isAdmobAdAvailable()) {
            if (pendingLoadCallback != null && admobInterstitialAd != null) {
                PendingLoadCallback cb = pendingLoadCallback;
                pendingLoadCallback = null;
                cb.onAdLoaded(admobInterstitialAd);
            }
            return;
        }

        if (isLoading) {
            return; // Already loading; waiter will receive callback
        }

        final String requestUnitId = admobInterstitialAdId;
        isLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, requestUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        isLoading = false;
                        admobInterstitialAd = interstitialAd;

                        interstitialAd.setOnPaidEventListener(adValue -> {
                            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logAdRevenue(
                                    context, adValue, requestUnitId, "interstitial"
                            );
                        });

                        Log.d(TAG, "Interstitial Ad Loaded");
                        if (pendingLoadCallback != null) {
                            PendingLoadCallback cb = pendingLoadCallback;
                            pendingLoadCallback = null;
                            cb.onAdLoaded(interstitialAd);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoading = false;
                        admobInterstitialAd = null;
                        Log.d(TAG, "Interstitial Ad Failed: " + loadAdError.getMessage());
                        notifyPendingFailed();
                    }
                });
    }

    private void notifyPendingFailed() {
        if (pendingLoadCallback != null) {
            PendingLoadCallback cb = pendingLoadCallback;
            pendingLoadCallback = null;
            cb.onAdFailed();
        }
    }

    public void setAdmobInterstitialAd(InterstitialAd admobInterstitialAd) {
        this.admobInterstitialAd = admobInterstitialAd;
        if (admobInterstitialAd != null) {
            final String unitId = admobInterstitialAdId;
            admobInterstitialAd.setOnPaidEventListener(adValue -> {
                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logAdRevenue(
                        context, adValue, unitId, "interstitial"
                );
            });
        }
        Log.d(TAG, "Cached Interstitial Ad set externally (e.g. from splash fallback). Available: " + (admobInterstitialAd != null));
    }

    public boolean isAdmobAdAvailable() {
        return admobInterstitialAd != null;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isShowingAd() {
        return isShowingAd;
    }

    public void showAdIfAvailable(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        loadAndShowAdmob(activity, onAdLoadInterface);
    }

    public void showInterstitialAd(Activity activity, OnAdLoadInterface onAdLoadInterface) {
        loadAndShowAdmob(activity, onAdLoadInterface);
    }

    public void showInterstitialAd(Activity activity, String loadingMessage, OnAdLoadInterface onAdLoadInterface) {
        loadAndShowAdmob(activity, loadingMessage, onAdLoadInterface);
    }

    public void loadAndShowAdmob(final Activity activity, final OnAdLoadInterface listener) {
        loadAndShowAdmob(activity, "Opening...", listener);
    }

    /**
     * RTO-style JIT Interstitial loader & shower:
     * 1. If cached in RAM -> shows in 0ms.
     * 2. If loading or not loaded yet -> shows brief Loader Dialog (up to 4s), waits for load, then shows immediately.
     * Ensures matched ads are never skipped & orphaned in RAM!
     */
    public void loadAndShowAdmob(final Activity activity, final String loadingMessage, final OnAdLoadInterface listener) {
        final AtomicBoolean callbackFired = new AtomicBoolean(false);
        final Runnable safeClose = () -> {
            if (callbackFired.compareAndSet(false, true)) {
                dismissAdLoadingDialog();
                if (listener != null) {
                    listener.onAdClose();
                }
            }
        };

        if (!isActivityValid(activity)) {
            safeClose.run();
            return;
        }

        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(activity);
        if (!pref.isGlobalInterstitialEnabled() || !pref.canShowInterstitialAd()) {
            Log.d(TAG, "loadAndShowAdmob: Interstitial disabled or frequency cap/cooldown active.");
            safeClose.run();
            return;
        }

        if (isShowingAd || Leaflet_AppOpenManager.isAppOpenAdShowing()) {
            Log.d(TAG, "loadAndShowAdmob: Another full-screen ad is already showing. Skipping safely.");
            safeClose.run();
            return;
        }

        // 1. 0ms Cache Hit
        if (isAdmobAdAvailable()) {
            safeShowInterstitial(activity, pref, listener, callbackFired, 0);
            return;
        }

        // 2. Ad not in RAM yet (either already loading or JIT fetch needed):
        // Show clean brief Loader Dialog so in-flight/JIT ad is displayed instead of skipped & wasted.
        showAdLoadingDialog(activity, loadingMessage);

        final Handler waitHandler = new Handler(Looper.getMainLooper());
        final Runnable timeoutRunnable = () -> {
            pendingLoadCallback = null;
            Log.w(TAG, "loadAndShowAdmob: Timeout (" + INTERSTITIAL_WAIT_TIMEOUT_MS + "ms) reached. Proceeding cleanly.");
            safeClose.run();
        };
        waitHandler.postDelayed(timeoutRunnable, INTERSTITIAL_WAIT_TIMEOUT_MS);

        pendingLoadCallback = new PendingLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                waitHandler.removeCallbacks(timeoutRunnable);
                dismissAdLoadingDialog();
                if (callbackFired.get() || !isActivityValid(activity)) {
                    Log.d(TAG, "loadAndShowAdmob: Ad arrived after timeout/finish. Retaining in cache.");
                    return;
                }
                safeShowInterstitial(activity, pref, listener, callbackFired, 0);
            }

            @Override
            public void onAdFailed() {
                waitHandler.removeCallbacks(timeoutRunnable);
                Log.d(TAG, "loadAndShowAdmob: Ad failed to load. Proceeding.");
                safeClose.run();
            }
        };

        if (!isLoading && !isAdmobAdAvailable()) {
            fetchAdMobAd();
        }
    }

    private void safeShowInterstitial(final Activity activity,
                                      final Leaflet_PreferenceClass pref,
                                      final OnAdLoadInterface listener,
                                      final AtomicBoolean callbackFired,
                                      final int focusRetry) {
        if (callbackFired.get() || !isActivityValid(activity)) {
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
            return;
        }

        if (isShowingAd || Leaflet_AppOpenManager.isAppOpenAdShowing() || !isAdmobAdAvailable()) {
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
            return;
        }

        // Android 13/14/15 & OneUI Window Focus Fix
        if (!activity.hasWindowFocus() && focusRetry < 2) {
            Log.d(TAG, "safeShowInterstitial: Waiting briefly for window focus (retry " + (focusRetry + 1) + ")");
            mainHandler.postDelayed(() -> safeShowInterstitial(activity, pref, listener, callbackFired, focusRetry + 1), FOCUS_RETRY_DELAY_MS);
            return;
        }

        final InterstitialAd adToShow = admobInterstitialAd;
        if (adToShow == null) {
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
            return;
        }

        admobInterstitialAd = null;
        isShowingAd = true;
        this.onAdLoadInterface = listener;

        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                isShowingAd = true;
                pref.recordInterstitialAdShown();
                Log.d(TAG, "onAdShowedFullScreenContent: Interstitial shown & frequency capping updated.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                isShowingAd = false;
                Leaflet_AppOpenManager.setLastAdDismissedTime(System.currentTimeMillis());
                if (callbackFired.compareAndSet(false, true) && listener != null) {
                    listener.onAdClose();
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
                Log.w(TAG, "Interstitial failed to show: " + adError.getMessage());
                if (callbackFired.compareAndSet(false, true) && listener != null) {
                    listener.onAdClose();
                }
            }
        });

        try {
            adToShow.show(activity);
        } catch (Throwable t) {
            isShowingAd = false;
            Log.e(TAG, "Error showing Interstitial: " + t.getMessage());
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
        }
    }

    public void showUnlockInterstitialAd(Activity activity, int minWatchSeconds, OnAdLoadInterface onAdLoadInterface) {
        final AtomicBoolean callbackFired = new AtomicBoolean(false);
        final Runnable safeClose = () -> {
            if (callbackFired.compareAndSet(false, true)) {
                dismissAdLoadingDialog();
                if (onAdLoadInterface != null) {
                    onAdLoadInterface.onAdClose();
                }
            }
        };

        Leaflet_PreferenceClass pref = preferenceClass != null ? preferenceClass : new Leaflet_PreferenceClass(context != null ? context : activity);
        if (!pref.isGlobalInterstitialEnabled() || pref.isInterstitialDailyCapReached() || pref.isInterstitialHourlyCapReached()) {
            Log.d(TAG, "showUnlockInterstitialAd: Interstitial disabled or daily/hourly cap reached.");
            safeClose.run();
            return;
        }

        if (isShowingAd || Leaflet_AppOpenManager.isAppOpenAdShowing() || !isActivityValid(activity)) {
            Log.d(TAG, "showUnlockInterstitialAd: Ad already showing or invalid activity.");
            safeClose.run();
            return;
        }

        this.onAdLoadInterface = onAdLoadInterface;

        if (isAdmobAdAvailable()) {
            safeShowUnlockInterstitial(activity, pref, minWatchSeconds, onAdLoadInterface, callbackFired);
            return;
        }

        showAdLoadingDialog(activity, "Opening...");
        final Handler waitHandler = new Handler(Looper.getMainLooper());
        final Runnable timeoutRunnable = () -> {
            pendingLoadCallback = null;
            Log.w(TAG, "showUnlockInterstitialAd: Timeout (" + INTERSTITIAL_WAIT_TIMEOUT_MS + "ms) reached.");
            safeClose.run();
        };
        waitHandler.postDelayed(timeoutRunnable, INTERSTITIAL_WAIT_TIMEOUT_MS);

        pendingLoadCallback = new PendingLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd ad) {
                waitHandler.removeCallbacks(timeoutRunnable);
                dismissAdLoadingDialog();
                if (callbackFired.get() || !isActivityValid(activity)) {
                    return;
                }
                safeShowUnlockInterstitial(activity, pref, minWatchSeconds, onAdLoadInterface, callbackFired);
            }

            @Override
            public void onAdFailed() {
                waitHandler.removeCallbacks(timeoutRunnable);
                safeClose.run();
            }
        };

        if (!isLoading && !isAdmobAdAvailable()) {
            fetchAdMobAd();
        }
    }

    private void safeShowUnlockInterstitial(Activity activity,
                                            Leaflet_PreferenceClass pref,
                                            int minWatchSeconds,
                                            OnAdLoadInterface listener,
                                            AtomicBoolean callbackFired) {
        if (callbackFired.get() || !isActivityValid(activity) || isShowingAd || Leaflet_AppOpenManager.isAppOpenAdShowing() || !isAdmobAdAvailable()) {
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
            return;
        }

        final InterstitialAd adToShow = admobInterstitialAd;
        if (adToShow == null) {
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
            return;
        }

        admobInterstitialAd = null;
        isShowingAd = true;
        final long startTime = System.currentTimeMillis();
        final long minDurationMs = Math.max(3000, minWatchSeconds * 1000L);

        adToShow.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();
                isShowingAd = true;
                pref.recordInterstitialAdShown();
                Log.d(TAG, "showUnlockInterstitialAd: onAdShowedFullScreenContent recorded.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                isShowingAd = false;
                long dismissTime = System.currentTimeMillis();
                Leaflet_AppOpenManager.setLastAdDismissedTime(dismissTime);
                long elapsed = dismissTime - startTime;

                if (elapsed >= minDurationMs) {
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            if (callbackFired.compareAndSet(false, true) && listener != null) {
                                listener.onAdClose();
                            }
                        });
                    }
                } else {
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            android.widget.Toast.makeText(activity, "Please watch the full ad to unlock pro template", android.widget.Toast.LENGTH_SHORT).show();
                            if (callbackFired.compareAndSet(false, true) && listener != null) {
                                listener.onAdFailedToEarnReward();
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
                if (callbackFired.compareAndSet(false, true) && listener != null) {
                    listener.onAdClose();
                }
            }
        });

        try {
            adToShow.show(activity);
        } catch (Throwable t) {
            Log.e(TAG, "Error showing Interstitial: " + t.getMessage());
            isShowingAd = false;
            if (callbackFired.compareAndSet(false, true) && listener != null) {
                listener.onAdClose();
            }
        }
    }

    private boolean isActivityValid(Activity activity) {
        if (activity == null || activity.isFinishing()) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) return false;
        return true;
    }

    private void showAdLoadingDialog(Activity activity, String loadingMessage) {
        if (!isActivityValid(activity)) return;
        dismissAdLoadingDialog();
        try {
            Dialog dlg = new Dialog(activity);
            dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            android.view.View dialogView = android.view.LayoutInflater.from(activity)
                    .inflate(com.online.flyer.design.postermaker.R.layout.leaflet_lottie_anim_dialog, null);
            android.widget.TextView tv = dialogView.findViewById(com.online.flyer.design.postermaker.R.id.textView);
            if (tv != null) {
                String msg = (loadingMessage != null && !loadingMessage.trim().isEmpty()) ? loadingMessage.trim() : "Opening...";
                tv.setText(msg);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                tv.setShadowLayer(4f, 0f, 1f, Color.BLACK);
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
                dlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dlg.show();
            loadingDialog = dlg;
        } catch (Throwable t) {
            Log.w(TAG, "showAdLoadingDialog skipped: " + t.getMessage());
            loadingDialog = null;
        }
    }

    private void dismissAdLoadingDialog() {
        try {
            if (loadingDialog != null) {
                if (loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                loadingDialog = null;
            }
        } catch (Throwable ignored) {
            loadingDialog = null;
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
