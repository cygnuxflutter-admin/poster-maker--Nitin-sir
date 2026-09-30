package com.online.flyer.design.postermaker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
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
    
    public Activity currentActivity;
    private android.app.Dialog noInternetDialog;
    private boolean wasDisconnected = false;

    public static int interstitialClickCount = 0;

    public static void showInterstitialAd(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (activity != null && activity.getApplication() instanceof Leaflet_MyApplication) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(activity);
            final int targetCount = pref.getAdsStatus("interstitialAdStatus") <= 0 ? 1 : pref.getAdsStatus("interstitialAdStatus");
            
            
            interstitialClickCount++;
            
            Leaflet_InterstitialAdManager adManager = ((Leaflet_MyApplication) activity.getApplication()).getInterstitialAdManager();

            if (targetCount > 1 && interstitialClickCount % targetCount == targetCount - 1) {
                adManager.fetchAdMobAd();
            }
            
            if (interstitialClickCount % targetCount == 0) {
                Leaflet_InterstitialAdManager.OnAdLoadInterface wrappedListener = new Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                    @Override
                    public void onAdClose() {
                        if (targetCount == 1) {
                            adManager.fetchAdMobAd();
                        }
                        if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
                    }
                };
                adManager.showInterstitialAd(activity, wrappedListener);
            } else {
                if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
            }
        } else {
            if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
        }
    }

    public static void showInterstitialAdWithOutCount(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (activity != null && activity.getApplication() instanceof Leaflet_MyApplication) {
            ((Leaflet_MyApplication) activity.getApplication()).getInterstitialAdManager().showInterstitialAd(activity, onAdLoadInterface);
        } else {
            if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
        }
    }

    public static int editInterstitialClickCount = 0;

    public static void showEditInterstitialAd(Activity activity, Leaflet_InterstitialAdManager.OnAdLoadInterface onAdLoadInterface) {
        if (activity != null && activity.getApplication() instanceof Leaflet_MyApplication) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(activity);
            final int targetCount = pref.getAdsStatus("EditScreenAdCount") <= 0 ? 1 : pref.getAdsStatus("EditScreenAdCount");
            
            
            editInterstitialClickCount++;
            
            Leaflet_InterstitialAdManager adManager = ((Leaflet_MyApplication) activity.getApplication()).getInterstitialAdManager();

            if (targetCount > 1 && editInterstitialClickCount % targetCount == targetCount - 1) {
                adManager.fetchAdMobAd();
            }
            
            if (editInterstitialClickCount % targetCount == 0) {
                Leaflet_InterstitialAdManager.OnAdLoadInterface wrappedListener = new Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                    @Override
                    public void onAdClose() {
                        if (targetCount == 1) {
                            adManager.fetchAdMobAd();
                        }
                        if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
                    }
                };
                adManager.showInterstitialAd(activity, wrappedListener);
            } else {
                if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
            }
        } else {
            if (onAdLoadInterface != null) onAdLoadInterface.onAdClose();
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
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
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

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
                try {
                    if (activity.getClass().getName().contains("UCropActivity")) {
                        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(activity.getWindow().getDecorView(), (v, insets) -> {
                            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                            return insets;
                        });
                    }
                    
                    View topLy = activity.findViewById(R.id.top_ly);
                    if (topLy == null) topLy = activity.findViewById(R.id.header);
                    
                    if (topLy != null) {
                        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(topLy, (v, insets) -> {
                            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                            int originalPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());
                            if (v.getId() == R.id.header) {
                                originalPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, activity.getResources().getDisplayMetrics());
                            }
                            v.setPadding(v.getPaddingLeft(), systemBars.top + originalPaddingTop, v.getPaddingRight(), v.getPaddingBottom());
                            return insets;
                        });
                        androidx.core.view.ViewCompat.requestApplyInsets(topLy);
                    }
                } catch (Exception e) {
                    Log.e("Leaflet_MyApplication", "Error in onActivityCreated callback", e);
                }
            }
            @Override public void onActivityStarted(@NonNull Activity activity) {}
            @Override public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = activity;
                checkAndShowDialog();
            }
            @Override public void onActivityPaused(@NonNull Activity activity) {
                if (currentActivity == activity) currentActivity = null;
                if (noInternetDialog != null && noInternetDialog.isShowing()) {
                    noInternetDialog.dismiss();
                }
            }
            @Override public void onActivityStopped(@NonNull Activity activity) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}
            @Override public void onActivityDestroyed(@NonNull Activity activity) {}
        });

        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && cm != null) {
            cm.registerDefaultNetworkCallback(new android.net.ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull android.net.Network network) {
                    if (wasDisconnected) {
                        wasDisconnected = false;
                        if (currentActivity != null) {
                            currentActivity.runOnUiThread(() -> {
                                if (noInternetDialog != null && noInternetDialog.isShowing()) {
                                    noInternetDialog.dismiss();
                                }
                                if (currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity ||
                                    currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_TemplateSelectionActivity ||
                                    currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_BackgroundSelectionActivity ||
                                    currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_SplashScreen) {
                                    currentActivity.recreate();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onLost(@NonNull android.net.Network network) {
                    wasDisconnected = true;
                    if (currentActivity != null) {
                        currentActivity.runOnUiThread(() -> checkAndShowDialog());
                    }
                }
            });
        }
    }

    private void checkAndShowDialog() {
        if (currentActivity == null) return;
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (cm != null) {
            android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        if (!isConnected) {
            wasDisconnected = true;
            if (noInternetDialog == null || !noInternetDialog.isShowing()) {
                showNoInternetDialog(currentActivity);
            }
        }
    }

    private void showNoInternetDialog(Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        noInternetDialog = new android.app.Dialog(activity, 16974126);
        noInternetDialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        noInternetDialog.setContentView(R.layout.leaflet_error_dialog);
        noInternetDialog.setCancelable(false);
        
        android.widget.TextView tvTitle = noInternetDialog.findViewById(R.id.title);
        android.widget.TextView tvDesc = noInternetDialog.findViewById(R.id.description);
        android.widget.TextView btnOk = noInternetDialog.findViewById(R.id.btn_ok);
        
        if (tvTitle != null) tvTitle.setText("No Internet");
        if (tvDesc != null) tvDesc.setText("Please turn on your internet connection to continue.");
        if (btnOk != null) {
            btnOk.setText("Retry");
            btnOk.setOnClickListener(v -> {
                android.net.ConnectivityManager cm = (android.net.ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean connected = false;
                if (cm != null) {
                    android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    connected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                }
                if (connected) {
                    noInternetDialog.dismiss();
                    wasDisconnected = false;
                    if (currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity ||
                        currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_TemplateSelectionActivity ||
                        currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_BackgroundSelectionActivity ||
                        currentActivity instanceof com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_SplashScreen) {
                        currentActivity.recreate();
                    }
                } else {
                    android.widget.Toast.makeText(activity, "Still no internet!", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        try {
            noInternetDialog.show();
        } catch (Exception ignored) {}
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
