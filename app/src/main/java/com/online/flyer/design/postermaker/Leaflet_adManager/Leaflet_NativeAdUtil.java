package com.online.flyer.design.postermaker.Leaflet_adManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
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

        // Specific checks for dialog types
        if ("image_gallery_dialog".equalsIgnoreCase(key) || "image_dialog".equalsIgnoreCase(key)) {
            if (pref.getInt("show_image_dialog_native", pref.getInt("show_native_image_dialog", 1)) == 0) {
                nativeAdContainer.setVisibility(View.GONE);
                return;
            }
        } else if ("save_dialog".equalsIgnoreCase(key)) {
            if (pref.getInt("show_save_dialog_native", pref.getInt("show_native_save_dialog", 1)) == 0) {
                nativeAdContainer.setVisibility(View.GONE);
                return;
            }
        } else if ("leave_dialog".equalsIgnoreCase(key)) {
            if (pref.getInt("show_leave_dialog_native", pref.getInt("show_native_leave_dialog", 1)) == 0) {
                nativeAdContainer.setVisibility(View.GONE);
                return;
            }
        } else if ("exit_dialog".equalsIgnoreCase(key) || "exit".equalsIgnoreCase(key) || "app_exit_dialog".equalsIgnoreCase(key)) {
            if (pref.getInt("show_native_exit", 1) == 0) {
                nativeAdContainer.setVisibility(View.GONE);
                return;
            }
        }

        boolean enabled = pref.isGridAdEnabled(key);
        if (!enabled) {
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }
        
        if (nativeAdContainer.getLayoutParams() != null) {
            nativeAdContainer.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            nativeAdContainer.requestLayout();
        }
        
        String customAdId = pref.getGridNativeAdId(key);
        boolean isDialog = "exit_dialog".equals(key) || "app_exit_dialog".equals(key) || "reward_dialog".equals(key)
                || "image_gallery_dialog".equals(key) || "save_dialog".equals(key) || "leave_dialog".equals(key);
        boolean showShimmerOnFail = isDialog ? pref.shouldShowDialogShimmerOnFail(key) : pref.shouldShowNativeShimmerOnFail(key);

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
        boolean showShimmerOnFail = pref.shouldShowDialogShimmerOnFail("reward_dialog");

        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillAdmobNativeAd(nativeAdContainer, customAdId, showShimmerOnFail, new OnNativeAdListener() {
            @Override
            public void onAdLoaded() {
                nativeAdContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailed() {
                if (!showShimmerOnFail) {
                    nativeAdContainer.removeAllViews();
                    nativeAdContainer.setVisibility(View.GONE);
                } else {
                    nativeAdContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static void loadOnboardingNativeAd(RelativeLayout nativeAdContainer, Activity context) {
        loadOnboardingNativeAd(nativeAdContainer, context, null);
    }

    public static void loadOnboardingNativeAd(RelativeLayout nativeAdContainer, Activity context, String customUnitId) {
        loadOnboardingNativeAd(nativeAdContainer, context, customUnitId, -1);
    }

    public static void loadOnboardingNativeAd(RelativeLayout nativeAdContainer, Activity context, String customUnitId, int adHeightDp) {
        if (nativeAdContainer == null || context == null) return;
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
        if (pref.getInt("show_onboarding_native", 1) == 0) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            return;
        }

        boolean showShimmerOnFail = pref.shouldShowNativeShimmerOnFail("onboarding");
        nativeAdContainer.setVisibility(View.VISIBLE);
        Leaflet_NativeAdUtil util = new Leaflet_NativeAdUtil(context);
        util.fillResponsiveOnboardingNativeAd(nativeAdContainer, customUnitId, showShimmerOnFail, adHeightDp, null);
    }

    public void fillResponsiveOnboardingNativeAd(final RelativeLayout nativeAdContainer, String customUnitId, boolean showShimmerOnFail, int adHeightDp, OnNativeAdListener listener) {
        if ("loaded".equals(nativeAdContainer.getTag())) {
            return;
        }

        String nativeUnitId;
        if (customUnitId != null && !customUnitId.trim().isEmpty()) {
            if ("null".equals(customUnitId.trim()) || "none".equalsIgnoreCase(customUnitId.trim())) {
                Log.d("AdMob_Native", "fillResponsiveOnboardingNativeAd: Native Ad unit ID is null. Skipping request.");
                if (!showShimmerOnFail) {
                    nativeAdContainer.removeAllViews();
                    nativeAdContainer.setVisibility(View.GONE);
                }
                if (listener != null) listener.onAdFailed();
                return;
            }
            nativeUnitId = customUnitId.trim();
        } else {
            nativeUnitId = preferenceClass.getAdsId("NativeUnitID");
        }

        if (nativeUnitId == null || nativeUnitId.trim().isEmpty() || nativeUnitId.equals("null") || nativeUnitId.contains("xxxx")) {
            Log.d("AdMob_Native", "fillResponsiveOnboardingNativeAd: Native Ad unit ID is null/empty. Skipping request.");
            if (!showShimmerOnFail) {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);
            }
            if (listener != null) listener.onAdFailed();
            return;
        }

        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            nativeUnitId = "ca-app-pub-3940256099942544/2247696110";
        }

        nativeAdContainer.setTag("loaded");

        // Determine if compact layout is needed based on responsive height
        final boolean isCompact = (adHeightDp > 0 && adHeightDp < 160);

        // Add Responsive Shimmer Loader
        if (nativeAdContainer.getChildCount() == 0) {
            int shimmerLayoutRes = isCompact ? R.layout.layout_preview_native_shimmer : R.layout.leaflet_native_shimmer;
            View shimmerView = LayoutInflater.from(context).inflate(shimmerLayoutRes, nativeAdContainer, false);
            nativeAdContainer.addView(shimmerView);
        }
        nativeAdContainer.setVisibility(View.VISIBLE);

        final String finalNativeUnitId1 = nativeUnitId;
        try {
            AdLoader.Builder builder = new AdLoader.Builder(context, finalNativeUnitId1);

            builder.forNativeAd(nativeAd -> {
                Log.d("AdMob_Native", "Responsive Onboarding Native ad loaded successfully! (isCompact=" + isCompact + ")");
                if (this.nativeAd != null) {
                    this.nativeAd.destroy();
                }
                this.nativeAd = nativeAd;
                nativeAd.setOnPaidEventListener(adValue -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logAdRevenue(
                            context, adValue, finalNativeUnitId1, "native"
                    );
                });

                nativeAdContainer.removeAllViews();

                if (isCompact) {
                    NativeAdView compactView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.layout_preview_native_ad, nativeAdContainer, false);
                    populateCompactNativeAdView(nativeAd, compactView);
                    nativeAdContainer.addView(compactView);
                } else {
                    adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.leaflet_native_ad_layout, null);
                    populateUnifiedNativeAdView(nativeAd, adView, adHeightDp);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    nativeAdContainer.addView(adView, lp);
                }

                if (listener != null) listener.onAdLoaded();
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e("AdMob_Native", "Onboarding Native ad failed: " + loadAdError.getMessage());
                    nativeAdContainer.setTag(null);
                    if (!showShimmerOnFail) {
                        nativeAdContainer.removeAllViews();
                        nativeAdContainer.setVisibility(View.GONE);
                    }
                    if (listener != null) listener.onAdFailed();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Leaflet_AppOpenManager.setAdClickActive(true);
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("AdMob_Native", "Onboarding Native ad exception: " + e.getMessage());
            if (!showShimmerOnFail) {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);
            }
            if (listener != null) listener.onAdFailed();
        }
    }

    private void populateCompactNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getBodyView() != null) {
            String body = nativeAd.getBody();
            if (body == null || body.trim().isEmpty()) {
                body = nativeAd.getAdvertiser();
            }
            if (body == null || body.trim().isEmpty()) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(body);
            }
        }

        if (adView.getCallToActionView() != null) {
            String cta = nativeAd.getCallToAction();
            if (cta == null || cta.trim().isEmpty()) {
                ((Button) adView.getCallToActionView()).setText("INSTALL");
            } else {
                ((Button) adView.getCallToActionView()).setText(cta.toUpperCase());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        if (mediaView != null) {
            mediaView.setVisibility(View.GONE);
        }

        adView.setNativeAd(nativeAd);
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
        if (customUnitId != null && !customUnitId.trim().isEmpty()) {
            if ("null".equals(customUnitId.trim()) || "none".equalsIgnoreCase(customUnitId.trim())) {
                Log.d("AdMob_Native", "fillAdmobNativeAd: Native Ad unit ID is explicitly null. Skipping request.");
                if (!showShimmerOnFail) {
                    nativeAdContainer.removeAllViews();
                    nativeAdContainer.setVisibility(View.GONE);
                } else {
                    nativeAdContainer.setVisibility(View.VISIBLE);
                    if (nativeAdContainer.getChildCount() == 0) {
                        View shimmerView = LayoutInflater.from(context).inflate(R.layout.leaflet_native_shimmer, nativeAdContainer, false);
                        nativeAdContainer.addView(shimmerView);
                    }
                }
                if (listener != null) listener.onAdFailed();
                return;
            }
            nativeUnitId = customUnitId.trim();
        } else {
            nativeUnitId = preferenceClass.getAdsId("NativeUnitID");
        }

        if (nativeUnitId == null || nativeUnitId.trim().isEmpty() || nativeUnitId.equals("null") || nativeUnitId.contains("xxxx")) {
            Log.d("AdMob_Native", "fillAdmobNativeAd: Native Ad unit ID is null or empty. Skipping request.");
            if (!showShimmerOnFail) {
                nativeAdContainer.removeAllViews();
                nativeAdContainer.setVisibility(View.GONE);
            } else {
                nativeAdContainer.setVisibility(View.VISIBLE);
                if (nativeAdContainer.getChildCount() == 0) {
                    View shimmerView = LayoutInflater.from(context).inflate(R.layout.leaflet_native_shimmer, nativeAdContainer, false);
                    nativeAdContainer.addView(shimmerView);
                }
            }
            if (listener != null) listener.onAdFailed();
            return;
        }
        
        if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            nativeUnitId = "ca-app-pub-3940256099942544/2247696110";
            Log.d("AdMob_Native", "Debug Mode: Using Test Native ID");
        } else {
            Log.d("AdMob_Native", "Fetching Native Ad from Firebase (via Prefs): " + nativeUnitId);
        }

        nativeAdContainer.setTag("loaded"); // Set tag so it doesn't request again for this container
        
        // Add Shimmer Loader
        if (nativeAdContainer.getChildCount() == 0) {
            View shimmerView = LayoutInflater.from(context).inflate(R.layout.leaflet_native_shimmer, nativeAdContainer, false);
            nativeAdContainer.addView(shimmerView);
        }
        nativeAdContainer.setVisibility(View.VISIBLE);

        final String finalNativeUnitId2 = nativeUnitId;
        try {
            AdLoader.Builder builder = new AdLoader.Builder(context, finalNativeUnitId2);

            builder.forNativeAd(nativeAd -> {
                Log.d("AdMob_Native", "Native ad loaded successfully!");
                if (this.nativeAd != null) {
                    this.nativeAd.destroy();
                }
                this.nativeAd = nativeAd;
                nativeAd.setOnPaidEventListener(adValue -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logAdRevenue(
                            context, adValue, finalNativeUnitId2, "native"
                    );
                });
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
                        if (nativeAdContainer.getChildCount() == 0) {
                            View shimmerView = LayoutInflater.from(context).inflate(R.layout.leaflet_native_shimmer, nativeAdContainer, false);
                            nativeAdContainer.addView(shimmerView);
                        }
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

    public void populateUnifiedNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView, int adHeightDp) {
        populateUnifiedNativeAdView(unifiedNativeAd, unifiedNativeAdView);
        if (adHeightDp > 0 && context != null) {
            MediaView mediaView = unifiedNativeAdView.findViewById(R.id.ad_media);
            View bodyView = unifiedNativeAdView.findViewById(R.id.ad_body);
            View ctaView = unifiedNativeAdView.findViewById(R.id.ad_call_to_action);
            View headlineView = unifiedNativeAdView.findViewById(R.id.ad_headline);
            float density = context.getResources().getDisplayMetrics().density;

            if (adHeightDp < 240) {
                // In medium heights (e.g. 185dp or 220dp), hide body text to avoid clipping CTA button
                if (bodyView != null) {
                    bodyView.setVisibility(View.GONE);
                }
            }

            if (mediaView != null) {
                // Squeeze MediaView so that Headline + CTA + Margins fit perfectly within adHeightDp
                int reservedSpaceDp = (adHeightDp < 200) ? 78 : ((adHeightDp < 240) ? 88 : 120);
                int mediaHeightDp = Math.max(65, adHeightDp - reservedSpaceDp);
                ViewGroup.LayoutParams mlp = mediaView.getLayoutParams();
                if (mlp != null) {
                    mlp.height = (int) (mediaHeightDp * density);
                    mediaView.setLayoutParams(mlp);
                }
            }

            if (ctaView != null && adHeightDp < 240) {
                ViewGroup.LayoutParams ctaLp = ctaView.getLayoutParams();
                if (ctaLp != null) {
                    ctaLp.height = (int) (36 * density);
                    if (ctaLp instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) ctaLp;
                        mlp.topMargin = (int) (4 * density);
                        mlp.bottomMargin = (int) (6 * density);
                    }
                    ctaView.setLayoutParams(ctaLp);
                }
            }

            if (headlineView != null && adHeightDp < 240) {
                ViewGroup.LayoutParams hLp = headlineView.getLayoutParams();
                if (hLp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) hLp;
                    mlp.topMargin = (int) (4 * density);
                    headlineView.setLayoutParams(mlp);
                }
                if (headlineView instanceof TextView && adHeightDp < 200) {
                    ((TextView) headlineView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                }
            }
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
