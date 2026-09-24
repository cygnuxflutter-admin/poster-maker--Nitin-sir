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
        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillAdmobNativeAd(nativeAdContainer);
    }

    public void fillAdmobNativeAd(final RelativeLayout nativeAdContainer) {
        String nativeUnitId = preferenceClass.getAdsId("NativeUnitID");
        
        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            nativeUnitId = "ca-app-pub-3940256099942544/2247696110";
            Log.d("AdMob_Native", "Debug Mode: Using Test Native ID");
        } else {
            Log.d("AdMob_Native", "Fetching Native Ad from Firebase (via Prefs): " + nativeUnitId);
        }

        if (nativeUnitId == null || nativeUnitId.trim().isEmpty() || nativeUnitId.equals("null")) {
            return;
        }

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
                nativeAdContainer.addView(adView);
                nativeAdContainer.setBackgroundColor(Color.parseColor("#151515"));
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("AdMob_Native", "Native ad failed to load: " + loadAdError.getMessage() + " (Code: " + loadAdError.getCode() + ")");
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("AdMob_Native", "Native ad exception: " + e.getMessage());
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

        ImageView imageView = unifiedNativeAdView.findViewById(R.id.unified_image_view);

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
        if (unifiedNativeAd.getBody() == null) {
            if (bodyView != null) {
                bodyView.setVisibility(View.INVISIBLE);
            }
        } else {
            if (bodyView != null) {
                bodyView.setVisibility(View.VISIBLE);
                ((TextView) bodyView).setText(unifiedNativeAd.getBody());
            }
        }

        Button callToActionView = (Button) unifiedNativeAdView.getCallToActionView();
        if (callToActionView != null) {
            callToActionView.setText(unifiedNativeAd.getCallToAction());
        }

        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }
}
