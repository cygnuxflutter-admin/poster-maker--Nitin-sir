package com.online.flyer.design.postermaker.Leaflet_activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Leaflet_SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    public static final boolean USE_TEST_ADS = BuildConfig.DEBUG; // Uses test ads in debug, live ads in release

    private Leaflet_PreferenceClass preferenceClass;
    private Dialog dialog;
    private boolean isProceeded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceClass = new Leaflet_PreferenceClass(this);

        if (USE_TEST_ADS) {
            preferenceClass.setDataType("BannerAdunitID", "ca-app-pub-3940256099942544/6300978111");
            preferenceClass.setDataType("InterstitalAdunitID", "ca-app-pub-3940256099942544/1033173712");
            preferenceClass.setDataType("RewardVideoUnitID", "ca-app-pub-3940256099942544/5224354917");
            preferenceClass.setDataType("NativeUnitID", "ca-app-pub-3940256099942544/2247696110");
            preferenceClass.setDataType("AppOpenID", "ca-app-pub-3940256099942544/3419835294");
            preferenceClass.setAdsId("google_Rw_ID", "ca-app-pub-3940256099942544/5224354917");
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.leaflet_activity_splash_screen);

        // Minimal and professional animation for Logo
        View logoCard = findViewById(R.id.logo_card);
        if (logoCard != null) {
            android.view.animation.AnimationSet animationSet = new android.view.animation.AnimationSet(true);
            animationSet.setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f));

            android.view.animation.ScaleAnimation scaleAnimation = new android.view.animation.ScaleAnimation(0.85f, 1.0f, 0.85f, 1.0f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(800);

            android.view.animation.AlphaAnimation alphaAnimation = new android.view.animation.AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(800);

            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(alphaAnimation);

            logoCard.startAnimation(animationSet);
        }

        // Subscribe to FCM global broadcast topic
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Subscribed to global topic: all_users");
                        } else {
                            Log.e(TAG, "Topic subscription failed: all_users", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error subscribing to topic all_users", e);
        }

        Leaflet_MyApplication.isAdsSplash = true;

        if (!Leaflet_NetworkUtils.isNetworkAvailable(this)) {
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(this, "Make sure you are connected to internet !!");
            return;
        }

        // 100% Remote Config Powered Initialization
        initFirebaseRemoteConfig();
    }

    private void initFirebaseRemoteConfig() {
        try {
            FirebaseRemoteConfig rc = FirebaseRemoteConfig.getInstance();
            long fetchInterval = BuildConfig.DEBUG ? 0 : 30;
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(fetchInterval)
                    .build();
            rc.setConfigSettingsAsync(configSettings);
            rc.setDefaultsAsync(R.xml.remote_config_defaults);
            rc.fetchAndActivate().addOnCompleteListener(task -> {
                Log.d(TAG, "Remote Config fetch completed. Success: " + task.isSuccessful());
                proceedAfterRemoteConfig();
            });

            // Fallback timeout in case fetch takes longer than 2.5s (offline/poor network)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isProceeded && !isFinishing() && !isDestroyed()) {
                    Log.d(TAG, "Remote Config fetch timeout reached. Proceeding with active/default config.");
                    proceedAfterRemoteConfig();
                }
            }, 2500);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing Remote Config: " + e.getMessage(), e);
            proceedAfterRemoteConfig();
        }
    }

    private synchronized void proceedAfterRemoteConfig() {
        if (isProceeded) return;
        isProceeded = true;

        // Save first launch date
        if (preferenceClass.getFirstDate() == null) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Calendar calender = Calendar.getInstance();
            preferenceClass.setFirstDate(simpleDateFormat.format(calender.getTime()));
        }

        if (preferenceClass.isFirstTimeLaunch()) {
            preferenceClass.setFirstTimeLaunch(false);
        }

        // Check for App Update (Directly from Remote Config)
        int updateType = preferenceClass.getInt("UpdateAvailable", 0);
        int forceUpdate = preferenceClass.getInt("force_update", 0);
        String serverVersion = preferenceClass.getDataType("UpdateVersionName", BuildConfig.VERSION_NAME);
        boolean isNewVersion = isVersionGreater(serverVersion, BuildConfig.VERSION_NAME);

        if (updateType == 1 && isNewVersion) {
            showUpdateDialog(forceUpdate == 1);
        } else {
            // Splash timer completely controlled by Remote Config (e.g. splash.timer_seconds)
            int timerSeconds = preferenceClass.getSplashTimerSeconds();
            long splashDelayMs = Math.max(0, timerSeconds * 1000L);
            Log.d(TAG, "Splash screen timer: " + timerSeconds + "s (" + splashDelayMs + "ms) from Remote Config.");

            new Handler(Looper.getMainLooper()).postDelayed(this::startIntent, splashDelayMs);
        }
    }

    private void showUpdateDialog(boolean isForceUpdate) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.leaflet_dialog_app_info);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView descriptionTextView = dialog.findViewById(R.id.descriptionTextView);
        Button cancelBtn = dialog.findViewById(R.id.dialogCancelButton);
        TextView msgTextView = dialog.findViewById(R.id.titleTextView);
        Button okBtn = dialog.findViewById(R.id.dialogOkButton);

        descriptionTextView.setTextColor(getResources().getColor(R.color.black));
        msgTextView.setTextColor(getResources().getColor(R.color.black));

        if (isForceUpdate) {
            msgTextView.setText("Update Required");
            descriptionTextView.setText("A mandatory new version of the app is available. Please update now to continue using the app.");
            cancelBtn.setVisibility(View.GONE);
        } else {
            msgTextView.setText("Update Available");
            descriptionTextView.setText("There is a newer version of the app available. Please update it now.");
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setText("Later");
            cancelBtn.setOnClickListener(view -> {
                dialog.dismiss();
                startIntent();
            });
        }

        dialog.setOnKeyListener((dialogInterface, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (isForceUpdate) {
                    finishAffinity();
                    return true;
                }
            }
            return false;
        });

        okBtn.setText("Update Now");
        okBtn.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        if (!isFinishing() && !isDestroyed()) {
            dialog.show();
        }
    }

    private void startIntent() {
        if (preferenceClass.getInt("splashscreen", 1) == 1) {
            callStartActivity();
        } else {
            callMainActivity();
        }
    }

    public void callStartActivity() {
        Application application = getApplication();
        ((Leaflet_MyApplication) application).showAdIfAvailable(Leaflet_SplashScreen.this, () -> {
            Leaflet_MyApplication.isAdsSplash = false;

            // Preload interstitial if target count is 1
            if (preferenceClass.getAdsStatus("interstitialAdStatus") == 1) {
                ((Leaflet_MyApplication) application).getInterstitialAdManager().fetchAdMobAd();
            }
            if (preferenceClass.getAdsStatus("EditScreenAdCount") == 1) {
                ((Leaflet_MyApplication) application).getInterstitialAdManager().fetchAdMobAd();
            }

            openNextScreen();
        });
    }

    public void callMainActivity() {
        Leaflet_MyApplication.isAdsSplash = false;

        // Preload interstitial if target count is 1
        if (preferenceClass.getAdsStatus("interstitialAdStatus") == 1) {
            ((Leaflet_MyApplication) getApplicationContext()).getInterstitialAdManager().fetchAdMobAd();
        }
        if (preferenceClass.getAdsStatus("EditScreenAdCount") == 1) {
            ((Leaflet_MyApplication) getApplicationContext()).getInterstitialAdManager().fetchAdMobAd();
        }

        ((Leaflet_MyApplication) getApplicationContext()).sendRequest();

        openNextScreen();
    }

    private void openNextScreen() {
        boolean isOnboardingDone = preferenceClass.getBoolean("isOnboardingDone", false);
        int showOnboarding = preferenceClass.getInt("show_onboarding", 1);
        Intent intent;
        if (showOnboarding == 1 && !isOnboardingDone) {
            intent = new Intent(getApplicationContext(), Leaflet_OnboardingActivity.class);
        } else {
            if (preferenceClass.isLoadingScreenEnabled() && preferenceClass.isShowLoadingAfterSplash()) {
                intent = new Intent(getApplicationContext(), Leaflet_LoadingActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), Leaflet_PosterMainActivity.class);
            }
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }

    private boolean isVersionGreater(String serverVer, String appVer) {
        if (serverVer == null || serverVer.trim().isEmpty() || serverVer.equals("null")
                || appVer == null || appVer.trim().isEmpty()) {
            return false;
        }
        try {
            String[] serverParts = serverVer.trim().split("\\.");
            String[] appParts = appVer.trim().split("\\.");
            int length = Math.max(serverParts.length, appParts.length);
            for (int i = 0; i < length; i++) {
                int s = i < serverParts.length ? Integer.parseInt(serverParts[i].replaceAll("[^0-9]", "")) : 0;
                int a = i < appParts.length ? Integer.parseInt(appParts[i].replaceAll("[^0-9]", "")) : 0;
                if (s > a) return true;
                if (s < a) return false;
            }
            return false;
        } catch (Exception e) {
            return !serverVer.equals(appVer);
        }
    }
}
