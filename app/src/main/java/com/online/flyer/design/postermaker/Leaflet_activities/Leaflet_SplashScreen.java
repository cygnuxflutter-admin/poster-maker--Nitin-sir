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
import android.widget.Toast;

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
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AdBlockerDetector;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_InstallReferrerHelper;

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

        // Fetch backend API metadata (field_0..field_48) in background
        fetchBackendApiMetadata();

        // 1. Resolve Install Attribution (Paid vs Organic with 2000ms watchdog / 0ms returning user bypass)
        Leaflet_InstallReferrerHelper.resolveAttribution(this, acquisitionType -> {
            Log.d(TAG, "Install Attribution Resolved: " + acquisitionType);
            // 2. 100% Remote Config Powered Initialization (Loading poster_maker_paid or poster_maker_organic)
            initFirebaseRemoteConfig();
        });
    }

    
    private void fetchBackendApiMetadata() {
        try {
            com.google.firebase.database.FirebaseDatabase.getInstance().getReference("all_data").child("datas")
                    .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                            if (snapshot != null && snapshot.exists()) {
                                for (int i = 0; i <= 48; i++) {
                                    String fieldName = "field_" + i;
                                    if (snapshot.hasChild(fieldName) && snapshot.child(fieldName).getValue() != null) {
                                        preferenceClass.setDataType(fieldName, snapshot.child(fieldName).getValue().toString());
                                    }
                                }
                                Log.d(TAG, "Backend API metadata updated from Realtime Database.");
                            }
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                            Log.w(TAG, "Realtime DB metadata onCancelled: " + error.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Realtime DB metadata fetch error: " + e.getMessage());
        }
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

        // Log & Show which Remote Config is loaded
        String masterJson = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass.getMasterConfigJson(com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance(), this);
        String activeKey = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass.activeConfigKey;
        String activeOrigin = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass.activeConfigOrigin;
        String acqType = preferenceClass.getAcquisitionType();

        android.util.Log.i("REMOTE_CONFIG_STATUS", "===============================================================");
        android.util.Log.i("REMOTE_CONFIG_STATUS", "🚀 [FIREBASE REMOTE CONFIG DEBUG INFO]");
        android.util.Log.i("REMOTE_CONFIG_STATUS", "👉 User Attribution : " + acqType.toUpperCase());
        android.util.Log.i("REMOTE_CONFIG_STATUS", "👉 Active Config Key: " + activeKey + " (" + activeOrigin + ")");
        android.util.Log.i("REMOTE_CONFIG_STATUS", "👉 JSON Length      : " + (masterJson != null ? masterJson.length() : 0) + " chars");
        android.util.Log.i("REMOTE_CONFIG_STATUS", "===============================================================");


        Leaflet_MyApplication.applyScreenshotSecurity(this);

        checkAdBlockerAndContinue();
    }

    private void checkAdBlockerAndContinue() {
        if (preferenceClass != null && preferenceClass.isAdBlockerDetectionEnabled()) {
            Leaflet_AdBlockerDetector.detect(this, isBlocked -> {
                if (isFinishing() || isDestroyed()) return;
                if (isBlocked) {
                    Leaflet_AdBlockerDetector.showAdBlockerDialog(this, this::continueSplashFlow);
                } else {
                    continueSplashFlow();
                }
            });
        } else {
            continueSplashFlow();
        }
    }

    private void continueSplashFlow() {
        if (isFinishing() || isDestroyed()) return;

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

        // Check for App Update (Directly from Remote Config JSON or keys)
        boolean hasUpdate = false;
        boolean isForced = false;
        String customTitle = null;
        String customMessage = null;
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();

            // 1. app_update JSON object
            String appUpdateJson = rc.getString("app_update");
            if (appUpdateJson != null && appUpdateJson.trim().startsWith("{")) {
                org.json.JSONObject updateObj = new org.json.JSONObject(appUpdateJson);
                boolean updateAvailable = parseBooleanOrInt(updateObj, "update_available", "UpdateAvailable");
                boolean forceUpdate = parseBooleanOrInt(updateObj, "force_update", "ForceUpdate");
                String serverVersion = parseString(updateObj, "update_version_name", "UpdateVersionName");
                if (serverVersion == null) serverVersion = BuildConfig.VERSION_NAME;
                int serverCode = parseInt(updateObj, "update_version_code", "UpdateVersionCode");
                customTitle = parseString(updateObj, "update_title", "UpdateTitle");
                customMessage = parseString(updateObj, "update_message", "UpdateMessage", "update_description", "UpdateDescription", "description");

                if (updateAvailable && (serverCode > BuildConfig.VERSION_CODE || isVersionGreater(serverVersion, BuildConfig.VERSION_NAME))) {
                    hasUpdate = true;
                    isForced = forceUpdate;
                    Log.i(TAG, "Update detected via app_update JSON: serverVer=" + serverVersion + ", serverCode=" + serverCode);
                }
            }

            // 2. Master Config JSON / ad_data (from poster_maker_organic or poster_maker_paid)
            if (!hasUpdate) {
                org.json.JSONObject rootObj = preferenceClass.getRootAdConfigJson();
                if (rootObj != null) {
                    org.json.JSONObject adData = rootObj.optJSONObject("ad_data");
                    org.json.JSONObject src = adData != null ? adData : rootObj;
                    boolean updateAvailable = parseBooleanOrInt(src, "update_available", "UpdateAvailable");
                    boolean forceUpdate = parseBooleanOrInt(src, "force_update", "ForceUpdate");
                    String serverVersion = parseString(src, "update_version_name", "UpdateVersionName");
                    if (serverVersion == null) serverVersion = BuildConfig.VERSION_NAME;
                    int serverCode = parseInt(src, "update_version_code", "UpdateVersionCode");
                    String t = parseString(src, "update_title", "UpdateTitle");
                    if (t != null) customTitle = t;
                    String m = parseString(src, "update_message", "UpdateMessage", "update_description", "UpdateDescription", "description");
                    if (m != null) customMessage = m;

                    Log.i("APP_UPDATE_CHECK", "Checking Root JSON: updateAvailable=" + updateAvailable
                            + ", forceUpdate=" + forceUpdate
                            + ", serverVersion=" + serverVersion + " (app=" + BuildConfig.VERSION_NAME + ")"
                            + ", serverCode=" + serverCode + " (app=" + BuildConfig.VERSION_CODE + ")");

                    if (updateAvailable && (serverCode > BuildConfig.VERSION_CODE || isVersionGreater(serverVersion, BuildConfig.VERSION_NAME))) {
                        hasUpdate = true;
                        isForced = forceUpdate;
                        Log.i(TAG, "Update detected via Root JSON: serverVer=" + serverVersion + ", serverCode=" + serverCode);
                    }
                }
            }

            // 3. Direct individual Remote Config keys
            if (!hasUpdate) {
                boolean updateAvailable = false;
                String rcAvail = rc.getString("update_available");
                String rcAvail2 = rc.getString("UpdateAvailable");
                if ("1".equals(rcAvail) || "true".equalsIgnoreCase(rcAvail) || "1".equals(rcAvail2) || "true".equalsIgnoreCase(rcAvail2)) {
                    updateAvailable = true;
                }
                if (!updateAvailable) {
                    updateAvailable = preferenceClass.getInt("update_available", preferenceClass.getInt("UpdateAvailable", 0)) == 1
                            || preferenceClass.getBoolean("update_available", preferenceClass.getBoolean("UpdateAvailable", false));
                }

                boolean forceUpdate = false;
                String rcForce = rc.getString("force_update");
                String rcForce2 = rc.getString("ForceUpdate");
                if ("1".equals(rcForce) || "true".equalsIgnoreCase(rcForce) || "1".equals(rcForce2) || "true".equalsIgnoreCase(rcForce2)) {
                    forceUpdate = true;
                }
                if (!forceUpdate) {
                    forceUpdate = preferenceClass.getInt("force_update", preferenceClass.getInt("ForceUpdate", 0)) == 1
                            || preferenceClass.getBoolean("force_update", preferenceClass.getBoolean("ForceUpdate", false));
                }

                String serverVersion = rc.getString("update_version_name");
                if (serverVersion == null || serverVersion.isEmpty() || serverVersion.equals("null")) {
                    serverVersion = rc.getString("UpdateVersionName");
                }
                if (serverVersion == null || serverVersion.isEmpty() || serverVersion.equals("null")) {
                    serverVersion = preferenceClass.getDataType("update_version_name", preferenceClass.getDataType("UpdateVersionName", BuildConfig.VERSION_NAME));
                }

                int serverCode = 0;
                try {
                    String scStr = rc.getString("update_version_code");
                    if (scStr != null && !scStr.isEmpty() && !scStr.equals("null")) {
                        serverCode = Integer.parseInt(scStr.trim());
                    }
                } catch (Exception ignored) {}
                if (serverCode <= 0) {
                    serverCode = preferenceClass.getInt("update_version_code", preferenceClass.getInt("UpdateVersionCode", 0));
                }

                String rcMsg = rc.getString("update_message");
                if (rcMsg == null || rcMsg.isEmpty() || rcMsg.equals("null")) rcMsg = rc.getString("update_description");
                if (rcMsg == null || rcMsg.isEmpty() || rcMsg.equals("null")) rcMsg = rc.getString("UpdateMessage");
                if (rcMsg == null || rcMsg.isEmpty() || rcMsg.equals("null")) rcMsg = rc.getString("UpdateDescription");
                if (rcMsg != null && !rcMsg.isEmpty() && !rcMsg.equals("null")) customMessage = rcMsg;

                String rcTitle = rc.getString("update_title");
                if (rcTitle == null || rcTitle.isEmpty() || rcTitle.equals("null")) rcTitle = rc.getString("UpdateTitle");
                if (rcTitle != null && !rcTitle.isEmpty() && !rcTitle.equals("null")) customTitle = rcTitle;

                Log.i("APP_UPDATE_CHECK", "Checking Direct RC Keys: updateAvailable=" + updateAvailable
                        + ", forceUpdate=" + forceUpdate
                        + ", serverVersion=" + serverVersion + " (app=" + BuildConfig.VERSION_NAME + ")"
                        + ", serverCode=" + serverCode + " (app=" + BuildConfig.VERSION_CODE + ")");

                if (updateAvailable && (serverCode > BuildConfig.VERSION_CODE || isVersionGreater(serverVersion, BuildConfig.VERSION_NAME))) {
                    hasUpdate = true;
                    isForced = forceUpdate;
                    Log.i(TAG, "Update detected via Direct RC keys: serverVer=" + serverVersion + ", serverCode=" + serverCode);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking update: " + e.getMessage(), e);
        }

        if (hasUpdate) {
            showUpdateDialog(isForced, customTitle, customMessage);
        } else {
            // Splash timer completely controlled by Remote Config (e.g. splash.timer_seconds)
            int timerSeconds = preferenceClass.getSplashTimerSeconds();
            long splashDelayMs = Math.max(0, timerSeconds * 1000L);
            Log.d(TAG, "Splash screen timer: " + timerSeconds + "s (" + splashDelayMs + "ms) from Remote Config.");

            new Handler(Looper.getMainLooper()).postDelayed(this::startIntent, splashDelayMs);
        }
    }

    private void showUpdateDialog(boolean isForceUpdate, String customTitle, String customMessage) {
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

        if (customTitle != null && !customTitle.trim().isEmpty()) {
            msgTextView.setText(customTitle);
        } else {
            msgTextView.setText(isForceUpdate ? "Update Required" : "Update Available");
        }

        if (customMessage != null && !customMessage.trim().isEmpty()) {
            descriptionTextView.setText(customMessage);
        } else {
            if (isForceUpdate) {
                descriptionTextView.setText("A mandatory new version of the app is available. Please update now to continue using the app.");
            } else {
                descriptionTextView.setText("There is a newer version of the app available. Please update it now.");
            }
        }

        if (isForceUpdate) {
            cancelBtn.setVisibility(View.GONE);
        } else {
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
        boolean isSplashAdEnabled = preferenceClass.getInt("splashscreen", 1) == 1
                && preferenceClass.getBoolean("splashscreen", true)
                && (preferenceClass.isSplashAppOpenEnabled() || preferenceClass.isSplashFallbackInterstitialEnabled());

        if (isSplashAdEnabled) {
            callStartActivity();
        } else {
            callMainActivity();
        }
    }

    public void callStartActivity() {
        Application application = getApplication();
        ((Leaflet_MyApplication) application).showAdIfAvailable(Leaflet_SplashScreen.this, () -> {
            Leaflet_MyApplication.isAdsSplash = false;

            boolean isOnboardingDone = preferenceClass.getBoolean("isOnboardingDone", false);
            int showOnboarding = preferenceClass.getInt("show_onboarding", 1);
            boolean isNextOnboarding = (showOnboarding == 1 && !isOnboardingDone);

            // Preload interstitial only if not going to onboarding and enabled in config
            if (!isNextOnboarding && preferenceClass.isGlobalInterstitialEnabled()) {
                if (preferenceClass.getAdsStatus("interstitialAdStatus") == 1 || preferenceClass.getAdsStatus("EditScreenAdCount") == 1) {
                    ((Leaflet_MyApplication) application).getInterstitialAdManager().fetchAdMobAd();
                }
            }

            openNextScreen();
        });
    }

    public void callMainActivity() {
        Leaflet_MyApplication.isAdsSplash = false;

        boolean isOnboardingDoneMain = preferenceClass.getBoolean("isOnboardingDone", false);
        int showOnboardingMain = preferenceClass.getInt("show_onboarding", 1);
        boolean isNextOnboardingMain = (showOnboardingMain == 1 && !isOnboardingDoneMain);

        if (!isNextOnboardingMain && preferenceClass.isGlobalInterstitialEnabled()) {
            if (preferenceClass.getAdsStatus("interstitialAdStatus") == 1 || preferenceClass.getAdsStatus("EditScreenAdCount") == 1) {
                ((Leaflet_MyApplication) getApplicationContext()).getInterstitialAdManager().fetchAdMobAd();
            }
        }

        ((Leaflet_MyApplication) getApplicationContext()).sendRequest();

        openNextScreen();
    }

    private void openNextScreen() {
        boolean isOnboardingDone = preferenceClass.getBoolean("isOnboardingDone", false);
        int showOnboarding = preferenceClass.getInt("show_onboarding", 1);
        Intent intent;
        if (showOnboarding == 1 && !isOnboardingDone) {
            intent = new Intent(getApplicationContext(), Leaflet_OnboardingStep1Activity.class);
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

    private boolean parseBooleanOrInt(org.json.JSONObject obj, String... keys) {
        if (obj == null) return false;
        for (String key : keys) {
            if (key != null && obj.has(key)) {
                Object val = obj.opt(key);
                if (val instanceof Boolean) return (Boolean) val;
                if (val instanceof Number) return ((Number) val).intValue() == 1;
                if (val instanceof String) {
                    String s = ((String) val).trim();
                    return "1".equals(s) || "true".equalsIgnoreCase(s);
                }
            }
        }
        return false;
    }

    private String parseString(org.json.JSONObject obj, String... keys) {
        if (obj == null) return null;
        for (String k : keys) {
            if (obj.has(k)) {
                String v = obj.optString(k, "").trim();
                if (!v.isEmpty() && !v.equals("null")) return v;
            }
        }
        return null;
    }

    private int parseInt(org.json.JSONObject obj, String... keys) {
        if (obj == null) return 0;
        for (String k : keys) {
            if (obj.has(k)) {
                Object val = obj.opt(k);
                if (val instanceof Number) return ((Number) val).intValue();
                if (val instanceof String) {
                    try { return Integer.parseInt(((String) val).trim()); } catch (Exception ignored) {}
                }
            }
        }
        return 0;
    }

}