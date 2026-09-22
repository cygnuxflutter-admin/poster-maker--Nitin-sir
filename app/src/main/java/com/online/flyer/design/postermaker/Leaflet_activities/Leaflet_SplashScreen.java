package com.online.flyer.design.postermaker.Leaflet_activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
//import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


public class Leaflet_SplashScreen extends AppCompatActivity {

    public static final boolean USE_TEST_ADS = BuildConfig.DEBUG; // Uses test ads in debug, live ads in release
    private Leaflet_PreferenceClass preferenceClass;
    private FirebaseDatabase database;
    private DatabaseReference project_data2;
//    private AppUpdateManager appUpdateManager;
    private InterstitialAd interstitial = null;
    public com.facebook.ads.InterstitialAd interstitialFB;
    private Dialog dialog;

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
            preferenceClass.setDataType("AdxBannerAdunitID", "");
            preferenceClass.setDataType("AdxInterstitalAdunitID", "");
            preferenceClass.setDataType("AdxRewardVideoUnitID", "");
            preferenceClass.setDataType("AdxNativeUnitID", "");
            preferenceClass.setDataType("AdxAppOpenID", "");
            preferenceClass.setDataType("fbNativeUnitID", "");
            preferenceClass.setDataType("fbInterstitalAdunitID", "");
            preferenceClass.setDataType("fbBannerAdunitID", "");
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.leaflet_activity_splash_screen);
        Leaflet_MyApplication.isAdsSplash = true;


//        if (!preferenceClass.isFirstTimeLaunch()) {
//            getData();
//        } else {
//            @SuppressLint("SimpleDateFormat")
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//            Calendar calender = Calendar.getInstance();
//            String start_date = preferenceClass.getFirstDate();
//            String end_date = simpleDateFormat.format(calender.getTime());
//
//            if (start_date != null) {
//                long findDiff = findDifference(start_date, end_date);
//                if (findDiff >= 2) {
//                    getData();
//                } else {
//                    save_token(false);
//                }
//            } else {
//                getData();
//            }
//        }

        if (Leaflet_NetworkUtils.isNetworkAvailable(Leaflet_SplashScreen.this)) {
            getData();
        }
    }

    private long findDifference(String start_date, String end_date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            Date d1 = simpleDateFormat.parse(start_date);
            Date d2 = simpleDateFormat.parse(end_date);

            long difference_In_Time = d2.getTime() - d1.getTime();

            return (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private void getData() {
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
            database = FirebaseDatabase.getInstance();
            DatabaseReference project_data = database.getReference("all_data").child("datas");
            project_data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (int i = 0; i <= 48; i++) {
                        String fieldName = "field_" + i;
                        preferenceClass.setDataType(fieldName, getStringValue(snapshot, fieldName, ""));
                    }

                    project_data2 = database.getReference("all_data").child("ad_data");
                    project_data2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (USE_TEST_ADS) {
                                preferenceClass.setDataType("BannerAdunitID", "ca-app-pub-3940256099942544/6300978111");
                                preferenceClass.setDataType("InterstitalAdunitID", "ca-app-pub-3940256099942544/1033173712");
                                preferenceClass.setDataType("RewardVideoUnitID", "ca-app-pub-3940256099942544/5224354917");
                                preferenceClass.setDataType("NativeUnitID", "ca-app-pub-3940256099942544/2247696110");
                                preferenceClass.setDataType("AppOpenID", "ca-app-pub-3940256099942544/3419835294");
                                preferenceClass.setAdsId("google_Rw_ID", "ca-app-pub-3940256099942544/5224354917");

                                preferenceClass.setDataType("AdxBannerAdunitID", "");
                                preferenceClass.setDataType("AdxInterstitalAdunitID", "");
                                preferenceClass.setDataType("AdxRewardVideoUnitID", "");
                                preferenceClass.setDataType("AdxNativeUnitID", "");
                                preferenceClass.setDataType("AdxAppOpenID", "");
                                preferenceClass.setDataType("fbNativeUnitID", "");
                                preferenceClass.setDataType("fbInterstitalAdunitID", "");
                                preferenceClass.setDataType("fbBannerAdunitID", "");
                            } else {
                                preferenceClass.setDataType("BannerAdunitID", getStringValue(snapshot, "BannerAdunitID", "ca-app-pub-7915560734124811/1184742625"));
                                preferenceClass.setDataType("InterstitalAdunitID", getStringValue(snapshot, "InterstitalAdunitID", "ca-app-pub-7915560734124811/6506808330"));
                                preferenceClass.setDataType("RewardVideoUnitID", getStringValue(snapshot, "RewardVideoUnitID", "ca-app-pub-7915560734124811/4221969500"));
                                preferenceClass.setDataType("NativeUnitID", getStringValue(snapshot, "NativeUnitID", "ca-app-pub-7915560734124811/5807970180"));

                                preferenceClass.setDataType("AdxBannerAdunitID", getStringValue(snapshot, "AdxBannerAdunitID", ""));
                                preferenceClass.setDataType("AdxInterstitalAdunitID", getStringValue(snapshot, "AdxInterstitalAdunitID", ""));
                                preferenceClass.setDataType("AdxRewardVideoUnitID", getStringValue(snapshot, "AdxRewardVideoUnitID", ""));
                                preferenceClass.setDataType("AdxNativeUnitID", getStringValue(snapshot, "AdxNativeUnitID", ""));

                                preferenceClass.setDataType("AppOpenID", getStringValue(snapshot, "AppOpenID", "ca-app-pub-7915560734124811/1238169780"));
                                preferenceClass.setDataType("AdxAppOpenID", getStringValue(snapshot, "AdxAppOpenID", ""));

                                preferenceClass.setDataType("fbNativeUnitID", getStringValue(snapshot, "fbNativeUnitID", ""));
                                preferenceClass.setDataType("fbInterstitalAdunitID", getStringValue(snapshot, "fbInterstitalAdunitID", ""));
                                preferenceClass.setDataType("fbBannerAdunitID", getStringValue(snapshot, "fbBannerAdunitID", ""));

                                preferenceClass.setAdsId("google_Rw_ID", getStringValue(snapshot, "google_Rw_ID", "ca-app-pub-7915560734124811/4221969500"));
                            }
                            preferenceClass.setAdsStatus("bannerAdStatus", getIntValue(snapshot, "bannerAdStatus", 1));
                            preferenceClass.setAdsStatus("interstitalAdStatus", getIntValue(snapshot, "interstitalAdStatus", 1));
                            preferenceClass.setAdsStatus("EditScreenAdCount", getIntValue(snapshot, "EditScreenAdCount", 3));

                            preferenceClass.setDataType("PremiumAdType", getStringValue(snapshot, "PremiumAdType", "Reward"));

                            preferenceClass.setAdsId("AD_FB_Rw_ID", getStringValue(snapshot, "AD_FB_Rw_ID", ""));

                            preferenceClass.setInt("UpdateAvailable", getIntValue(snapshot, "UpdateAvailable", 0));
                            preferenceClass.setDataType("UpdateVersionName", getStringValue(snapshot, "UpdateVersionName", "1.0"));

                            preferenceClass.setInt("download", getIntValue(snapshot, "download", 1));
                            preferenceClass.setInt("splashscreen", getIntValue(snapshot, "splashscreen", 1));

                            preferenceClass.setInt("rv_count", getIntValue(snapshot, "rv_count", 2));
                            preferenceClass.setInt("PremiumPostCount", getIntValue(snapshot, "PremiumPostCount", 2));

                            preferenceClass.setDataType("main_key", getStringValue(snapshot, "main_key", ""));
                            preferenceClass.setDecryptionType(getIntValue(snapshot, "decryptionType", 0));

                            save_token(true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Leaflet_MaterialDialogUtils.getInstance().errorDialog(Leaflet_SplashScreen.this, getResources().getString(R.string.something_went_wrong));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog(Leaflet_SplashScreen.this, getResources().getString(R.string.something_went_wrong));
                }
            });
        } else {
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(this, getResources().getString(R.string.internet_error));
        }
    }

    private void save_token(boolean update) {
        if (update) {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Calendar calender = Calendar.getInstance();
            String start_date = simpleDateFormat.format(calender.getTime());
            preferenceClass.setFirstDate(start_date);
        }

        if (preferenceClass.isFirstTimeLaunch()) {
            preferenceClass.setFirstTimeLaunch(false);
        }

        int updateType = preferenceClass.getInt("UpdateAvailable");
        String serverVersion = preferenceClass.getDataType("UpdateVersionName");
        boolean isNewVersion = !BuildConfig.VERSION_NAME.equals(serverVersion);

        if ((updateType == 1 || updateType == 2) && isNewVersion) {
            final boolean isForceUpdate = (updateType == 1); // 1 = Force Update, 2 = Optional Update

            dialog = new Dialog(Leaflet_SplashScreen.this);
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
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        startIntent();
                    }
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
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                }
            });

            if (!isFinishing()) {
                dialog.show();
            }

        } else {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> startIntent(), 2000);
        }

    }

    private void startIntent() {
        if (preferenceClass.getInt("splashscreen") == 1) {
            callStartActivity();
        } else {
            callMainActivity();
        }
    }

    public void callStartActivity() {
        Application application = getApplication();
        ((Leaflet_MyApplication) application).showAdIfAvailable(Leaflet_SplashScreen.this, () -> {
            Leaflet_MyApplication.isAdsSplash = false;
            Intent intent = new Intent(getApplicationContext(), Leaflet_PosterMainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void callMainActivity() {
        Leaflet_MyApplication.isAdsSplash = false;
        ((Leaflet_MyApplication) getApplicationContext()).sendRequest();

        Intent intent = new Intent(getApplicationContext(), Leaflet_PosterMainActivity.class);
        startActivity(intent);
        finish();
    }

    private String getUniqueId() {
        String uniqueId;
        uniqueId = preferenceClass.getDataType("unique_id", null);

        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
            preferenceClass.setDataType("unique_id", uniqueId);
        }

        return uniqueId;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x11) {
//            if (resultCode == RESULT_OK) {
            if (Leaflet_NetworkUtils.isNetworkAvailable(Leaflet_SplashScreen.this)) {
                startIntent();
//                    Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//                    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                            try {
//                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, SplashScreen.this, 0x11);
//                            } catch (IntentSender.SendIntentException e) {
//                                MaterialDialogUtils.getInstance().errorDialog(SplashScreen.this, "Make sure you are connected to internet !!");
//                                e.printStackTrace();
//                            }
//                        } else {
//                            startIntent();
//                        }
//                    });
            } else {
                Leaflet_MaterialDialogUtils.getInstance().errorDialog(Leaflet_SplashScreen.this, "Make sure you are connected to internet !!");
            }
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (appUpdateManager != null) {
            appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, 0x11);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });
            appUpdateManager.getAppUpdateInfo().addOnFailureListener(e -> startIntent());
        }*/
    }

    private String getStringValue(DataSnapshot snapshot, String key, String defaultValue) {
        if (snapshot != null && snapshot.hasChild(key) && snapshot.child(key).getValue() != null) {
            return snapshot.child(key).getValue().toString();
        }
        return defaultValue;
    }

    private int getIntValue(DataSnapshot snapshot, String key, int defaultValue) {
        if (snapshot != null && snapshot.hasChild(key) && snapshot.child(key).getValue() != null) {
            try {
                return Integer.parseInt(snapshot.child(key).getValue().toString());
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;
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