package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class Leaflet_LoadAds {

    public static void loadAdmobBannerAd(Activity activity, RelativeLayout mainLayout) {
        mainLayout.removeAllViews();
        String bannerAdunitID = new Leaflet_PreferenceClass(activity).getAdsId("BannerAdunitID");
        android.util.Log.d("AdMob_Banner", "Loading banner with ID: " + bannerAdunitID);
        if (bannerAdunitID != null && !bannerAdunitID.trim().isEmpty() && !bannerAdunitID.equals("null")) {
            AdView adView = new AdView(activity);
            AdSize adSize = getAdSize(activity);
            adView.setAdSize(adSize);
            adView.setAdUnitId(bannerAdunitID);

            AdRequest adRequest = new AdRequest.Builder().build();

            try {
                adView.loadAd(adRequest);
            } catch (Exception e) {
                android.util.Log.e("AdMob_Banner", "Banner exception: " + e.getMessage());
                loadADXBannerAd(activity, mainLayout);
            }

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    android.util.Log.d("AdMob_Banner", "Banner ad loaded successfully!");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    android.util.Log.e("AdMob_Banner", "Banner ad failed to load: " + loadAdError.getMessage() + " (Code: " + loadAdError.getCode() + ")");
                    loadADXBannerAd(activity, mainLayout);
                }
            });

            RelativeLayout.LayoutParams bannerParameters =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mainLayout.addView(adView, bannerParameters);
        } else {
            loadADXBannerAd(activity, mainLayout);
        }
    }

    private static void loadADXBannerAd(Activity activity, RelativeLayout mainLayout) {
        mainLayout.removeAllViews();
        String AdxBannerAdunitID = new Leaflet_PreferenceClass(activity).getAdsId("AdxBannerAdunitID");
        if (AdxBannerAdunitID != null && !AdxBannerAdunitID.trim().isEmpty() && !AdxBannerAdunitID.equals("null")) {
            AdView adView = new AdView(activity);
            AdSize adSize = getAdSize(activity);
            adView.setAdSize(adSize);
            adView.setAdUnitId(AdxBannerAdunitID);

            AdRequest adRequest = new AdRequest.Builder().build();
            try {
                adView.loadAd(adRequest);
            } catch (Exception e) {
                loadFBBannerAd(activity, mainLayout);
            }

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    loadFBBannerAd(activity, mainLayout);
                }
            });

            RelativeLayout.LayoutParams bannerParameters =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mainLayout.addView(adView, bannerParameters);
        } else {
            loadFBBannerAd(activity, mainLayout);
        }
    }

    private static void loadFBBannerAd(Activity activity, RelativeLayout mainLayout) {
        mainLayout.removeAllViews();
        String fbBannerAdunitID = new Leaflet_PreferenceClass(activity).getAdsId("fbBannerAdunitID");
        if (fbBannerAdunitID != null && !fbBannerAdunitID.trim().isEmpty() && !fbBannerAdunitID.equals("null")) {
            com.facebook.ads.AdView fbBannerView = new com.facebook.ads.AdView(activity, fbBannerAdunitID, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            mainLayout.addView(fbBannerView);

            mainLayout.setGravity(Gravity.BOTTOM);

            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                }

                @Override
                public void onAdLoaded(Ad ad) {
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            try {
                fbBannerView.loadAd(fbBannerView.buildLoadAdConfig().withAdListener(adListener).build());
            } catch (Exception e) {
            }
        }
    }

    private static AdSize getAdSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

}
