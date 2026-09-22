package com.online.flyer.design.postermaker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.facebook.ads.AudienceNetworkAds;
import com.onesignal.OneSignal;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
//import com.online.flyer.design.postermaker.brandUtils.MailER_Connectivity;
//import com.online.flyer.design.postermaker.brandUtils.MailER_Constant;
//import com.online.flyer.design.postermaker.brandUtils.MailER_PrefManager;
//import com.online.flyer.design.postermaker.brandUtils.MailER_Util;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
//import com.google.android.play.core.review.ReviewInfo;
//import com.google.android.play.core.review.ReviewManager;
//import com.google.android.play.core.review.ReviewManagerFactory;
//import com.google.android.play.core.tasks.Task;
//import com.google.firebase.appcheck.AppCheckToken;
//import com.google.firebase.appcheck.FirebaseAppCheck;
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Leaflet_MyApplication extends android.app.Application {


    //    public static AppOpenManager appOpenAdManager;
//    MailER_Connectivity connectivity;

//    MailER_PrefManager prefManager;
    public static Context context;

    public static Leaflet_MyApplication myApplication;

/*
    public static void showInterstitialAdWithOutCount(Activity activity, InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        ((MyApplication) activity.getApplication()).getInterstitialAdManager().showInterstitialAd(activity, onAdLoadInterface);
    }
*/


    public static boolean isShowingAppOpen = true, isAdsSplash = true;
    public Leaflet_AppOpenManager appOpenManager;
    private Leaflet_InterstitialAdManager interstitialAdManager;
    public static Leaflet_MyApplication mInstance;

    public static void showInterstitialAd(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public static void showInterstitialAdWithOutCount(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public static void showEditInterstitialAd(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (onAdLoadInterface != null) {
            onAdLoadInterface.onAdClose();
        }
    }

    public Leaflet_InterstitialAdManager getInterstitialAdManager() {
        if (interstitialAdManager == null) {
            interstitialAdManager = new Leaflet_InterstitialAdManager(this);
        }
        return interstitialAdManager;
    }

    public void loadInterstitialAd() {
        // Interstitial ads disabled
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


        myApplication = this;

//        connectivity = new MailER_Connectivity(this);
//        prefManager = new MailER_PrefManager(this);

        context = this;
//        if (connectivity.isConnected()) {
//            Config.IS_CONNECTED = true;
//        } else {
//            Config.IS_CONNECTED = false;
//        }


//        FirebaseApp.initializeApp(/*context=*/ this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());
//        firebaseAppCheck.addAppCheckListener(new FirebaseAppCheck.AppCheckListener() {
//            @Override
//            public void onAppCheckTokenChanged(@NonNull AppCheckToken token) {
//                MailER_Util.showLog("Token: " + token.getToken() + " " + token.getExpireTimeMillis());
//            }
//        });
 /*       MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });*/
        MobileAds.initialize(this, initializationStatus -> Log.d(" AD", "MobileAds initialized"));

        appOpenManager = new Leaflet_AppOpenManager(this);
        AudienceNetworkAds.initialize(this);

        // OneSignal Initialization
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("35b6d659-eeda-4729-b2c0-ebef7ac69e3e");
        OneSignal.promptForPushNotifications();
    }

    public static synchronized Leaflet_MyApplication getInstance() {
        Leaflet_MyApplication myApp;
        synchronized (Leaflet_MyApplication.class) {
            myApp = mInstance;
        }
        return myApp;
    }


//    public static MailER_PrefManager prefManager() {
//        return new MailER_PrefManager(mInstance);
//    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    public void showAdIfAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenManager.showAdIfSplashAvailable(activity, onShowAdCompleteListener);
    }

    public void showAdIfHomeAvailable(@NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public void sendRequest() {
        appOpenManager.sendRequest();
    }

    public boolean isAdAvailable() {
        return appOpenManager.isAdAvailable();
    }

//    public void appReview(Activity activity) {
//        ReviewManager reviewManager = ReviewManagerFactory.create(activity);
//        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
//        request.addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                ReviewInfo reviewInfo = task.getResult();
//                Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
//                flow.addOnCompleteListener(task1 -> {
//
//                });
//            }
//        });
//    }

    public String GetMainPath() {
        String folderName = getString(R.string.app_name);
        String sPath = "";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            sPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            File dir = new File(sPath);
            if (dir.exists()) {
                sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + folderName;
                dir = new File(sPath);
                dir.mkdirs();
            } else {
                sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + folderName;
                dir = new File(sPath);
                dir.mkdirs();
            }
        } else {
            sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + folderName;
            File dir = new File(sPath);
            if (!dir.exists()) {
                dir.mkdirs();
                if (!dir.exists()) {
                    sPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
                    dir = new File(sPath);
                    if (dir.exists()) {
                        sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + folderName;
                        dir = new File(sPath);
                        dir.mkdirs();
                    } else {
                        sPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + folderName;
                        dir = new File(sPath);
                        dir.mkdirs();
                    }
                }
            }
        }
        return sPath;
    }


    public static int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public static Display getDefaultDisplay() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display;
    }

    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        point.y = display.getHeight();

        return point.y;
    }

    public static int getColumnWidth(int column, float grid_padding) {
        Resources r = context.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, grid_padding, r.getDisplayMetrics());
        return (int) ((getScreenWidth() - ((column + 1) * padding)) / column);
    }

//    public static void ShowOpenAds() {
//        try {
//            if (prefManager().getBoolean(MailER_Constant.OPEN_AD_ENABLE) && prefManager().getBoolean(MailER_Constant.ADS_ENABLE) && !MailER_Constant.IS_SUBSCRIBED) {
//                myApplication.appOpenManager = new MailER_AppOpenManager(myApplication);
//            }
//        } catch (Exception e) {
//            MailER_Util.showErrorLog(e.getMessage(), e);
//        }
//    }


}