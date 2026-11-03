package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class Leaflet_NativeAdUtil {

    private final Context context;
    private final Leaflet_PreferenceClass preferenceClass;
    private final int width;
    private final int height;
    private NativeAdView adView;
    private NativeAd nativeAd;

    public Leaflet_NativeAdUtil(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.preferenceClass = new Leaflet_PreferenceClass(context);
    }

    public Leaflet_NativeAdUtil(Context context) {
        this.context = context;
        this.width = -1;
        this.height = -1;
        this.preferenceClass = new Leaflet_PreferenceClass(context);
    }

    public static void loadNativeAd(RelativeLayout nativeAdContainer, Activity context) {
        loadNativeAd(nativeAdContainer, context, "exit_dialog");
    }

    public static void loadNativeAd(RelativeLayout nativeAdContainer, Activity context, String screenType) {
        if (nativeAdContainer == null || context == null) return;
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
        String key = screenType != null ? screenType : "exit_dialog";
        
        boolean enabled = pref.isGridAdEnabled(key);
        if (!enabled || pref.getInt("show_native_exit", 1) == 0) {
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }
        
        if (nativeAdContainer.getLayoutParams() != null) {
            nativeAdContainer.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            nativeAdContainer.requestLayout();
        }
        
        String customAdId = pref.getGridNativeAdId(key);
        boolean showShimmerOnFail = pref.shouldShowNativeShimmerOnFail(key);

        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillAdmobNativeAd(nativeAdContainer, customAdId, showShimmerOnFail, null);
    }

    public static void loadRewardDialogNativeAd(RelativeLayout nativeAdContainer, Activity context, String itemType) {
        if (nativeAdContainer == null || context == null) return;
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
        
        boolean enabled = pref.isRewardDialogNativeAdEnabled(itemType);
        if (!enabled) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }
        
        if (nativeAdContainer.getLayoutParams() != null) {
            nativeAdContainer.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            nativeAdContainer.requestLayout();
        }

        String customAdId = pref.getRewardDialogNativeAdId(itemType);
        boolean showShimmerOnFail = pref.shouldShowNativeShimmerOnFail("reward_dialog");

        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillAdmobNativeAd(nativeAdContainer, customAdId, showShimmerOnFail, new OnNativeAdListener() {
            @Override
            public void onAdLoaded() {
                nativeAdContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailed() {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);
            }
        });
    }

    public static void loadOnboardingNativeAd(RelativeLayout nativeAdContainer, Activity context) {
        loadOnboardingNativeAd(nativeAdContainer, context, null);
    }

    public static void loadOnboardingNativeAd(RelativeLayout nativeAdContainer, Activity context, String customUnitId) {
        if (nativeAdContainer == null || context == null) return;
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
        if (pref.getInt("show_onboarding_native", 1) == 0) {
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }
        
        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillAdmobNativeAd(nativeAdContainer, customUnitId);
    }

    public interface OnNativeAdListener {
        void onAdLoaded();
        void onAdFailed();
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer) {
        fillAdmobNativeAd(nativeAdContainer, null, false, null);
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer, String customUnitId) {
        fillAdmobNativeAd(nativeAdContainer, customUnitId, false, null);
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer, String customUnitId, boolean showShimmerOnFail, OnNativeAdListener listener) {
        if ("loaded".equals(nativeAdContainer.getTag())) {
            // ALREADY LOADED! This prevents huge requests and low impressions when scrolling.
            return;
        }

        String nativeUnitId;
        if (customUnitId != null && !customUnitId.trim().isEmpty() && !customUnitId.equals("null") && !customUnitId.contains("xxxx")) {
            nativeUnitId = customUnitId;
        } else {
            nativeUnitId = preferenceClass.getAdsId("NativeUnitID");
        }
        
        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            nativeUnitId = "ca-app-pub-3940256099942544/2247696110";
            Log.d("AdMob_Native", "Debug Mode: Using Test Native ID");
        } else {
            Log.d("AdMob_Native", "Fetching Native Ad from Firebase (via Prefs): " + nativeUnitId);
        }

        if (nativeUnitId == null || nativeUnitId.trim().isEmpty() || nativeUnitId.equals("null")) {
            if (!showShimmerOnFail) {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);

            }
            if (listener != null) listener.onAdFailed();
            return;
        }

        nativeAdContainer.setTag("loaded"); // Set tag so it doesn't request again for this container
        
        // Add Shimmer Loader
        if (nativeAdContainer.getChildCount() == 0) {
            View shimmerView = LayoutInflater.from(context).inflate(R.layout.leaflet_native_shimmer, nativeAdContainer, false);
            nativeAdContainer.addView(shimmerView);
        }
        nativeAdContainer.setVisibility(View.VISIBLE);

        try {
            AdLoader.Builder builder = new AdLoader.Builder(context, nativeUnitId);

            builder.forNativeAd(nativeAd -> {
                Log.d("AdMob_Native", "Native ad loaded successfully!");
                if (this.nativeAd != null) {
                    this.nativeAd.destroy();
                }
                this.nativeAd = nativeAd;
                adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.leaflet_native_ad_layout, null);
                populateUnifiedNativeAdView(nativeAd, adView);
                nativeAdContainer.removeAllViews();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                );
                nativeAdContainer.addView(adView, lp);
                if (listener != null) listener.onAdLoaded();
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("AdMob_Native", "Native ad failed to load: " + loadAdError.getMessage() + " (Code: " + loadAdError.getCode() + ")");
                    nativeAdContainer.setTag(null);
                    if (!showShimmerOnFail) {
                        nativeAdContainer.removeAllViews();
                        nativeAdContainer.setVisibility(View.GONE);
                    } else {
                        nativeAdContainer.setVisibility(View.VISIBLE);
                    }
                    if (listener != null) {
                        listener.onAdFailed();
                    }
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Leaflet_AppOpenManager.setAdClickActive(true);
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("AdMob_Native", "Native ad exception: " + e.getMessage());
            if (!showShimmerOnFail) {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);
            }
            if (listener != null) listener.onAdFailed();
        }
    }

    public void populateUnifiedNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView) {

        RelativeLayout relativeLayout = unifiedNativeAdView.findViewById(R.id.parentLyt);

        /*if (width != -1 && height != -1) {
            relativeLayout.getLayoutParams().width = width;
            relativeLayout.getLayoutParams().height = 300;
            relativeLayout.invalidate();
        }*/

        MediaView mediaView = unifiedNativeAdView.findViewById(R.id.ad_media);
        unifiedNativeAdView.setMediaView(mediaView);

        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));

        ImageView imageView = null;
        try {
            int imgId = unifiedNativeAdView.getResources().getIdentifier("unified_image_view", "id", unifiedNativeAdView.getContext().getPackageName());
            if (imgId != 0) {
                imageView = unifiedNativeAdView.findViewById(imgId);
            }
        } catch (Exception ignored) {}

        populateNativeAdView(unifiedNativeAd, unifiedNativeAdView, mediaView, imageView);
    }

    private void populateNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView, MediaView mediaView, ImageView imageView) {
        int i = 0;
       /* MediaContent mediaContent = unifiedNativeAd.getMediaContent();
        if (mediaContent != null) {
            boolean hasVideo = mediaContent.getVideoController().hasVideoContent();
            if (hasVideo) {

                unifiedNativeAdView.setMediaView(mediaView);
                imageView.setVisibility(View.GONE);
            } else {
                unifiedNativeAdView.setImageView(imageView);
                mediaView.setVisibility(View.GONE);
                List<NativeAd.Image> images = unifiedNativeAd.getImages();
                if (images.size() > 0) {
                    while (true) {
                        if (i >= images.size()) {
                            break;
                        }
                        NativeAd.Image image = images.get(i);
                        if (image != null) {
                            Drawable drawable = image.getDrawable();
                            imageView.setImageDrawable(drawable);
                            break;
                        }
                        i++;
                    }
                }
            }
        } else {
            unifiedNativeAdView.setImageView(imageView);
            mediaView.setVisibility(View.GONE);
            List<NativeAd.Image> images = unifiedNativeAd.getImages();
            if (images.size() > 0) {
                while (true) {
                    if (i >= images.size()) {
                        break;
                    }
                    NativeAd.Image image = images.get(i);
                    if (image != null) {
                        Drawable drawable = image.getDrawable();
                        imageView.setImageDrawable(drawable);
                        break;
                    }
                    i++;
                }
            }
        }*/

        TextView headlineView = (TextView) unifiedNativeAdView.getHeadlineView();
        if (headlineView != null) {
            headlineView.setText(unifiedNativeAd.getHeadline());
        }

        View bodyView = unifiedNativeAdView.getBodyView();
        String bodyText = unifiedNativeAd.getBody();
        if (bodyText == null || bodyText.trim().isEmpty()) {
            bodyText = unifiedNativeAd.getAdvertiser();
        }
        if (bodyText == null || bodyText.trim().isEmpty()) {
            if (bodyView != null) {
                bodyView.setVisibility(View.GONE);
            }
        } else {
            if (bodyView != null) {
                bodyView.setVisibility(View.VISIBLE);
                ((TextView) bodyView).setText(bodyText);
            }
        }

        Button callToActionView = (Button) unifiedNativeAdView.getCallToActionView();
        if (callToActionView != null) {
            String cta = unifiedNativeAd.getCallToAction();
            if (cta != null && !cta.trim().isEmpty()) {
                callToActionView.setText(cta.toUpperCase());
            } else {
                callToActionView.setText("INSTALL");
            }
        }

        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }
}
