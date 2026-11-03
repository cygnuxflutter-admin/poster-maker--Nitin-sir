package com.online.flyer.design.postermaker.Leaflet_activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.graphics.Path;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.online.flyer.design.postermaker.BuildConfig;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import java.util.ArrayList;
import java.util.List;

public class Leaflet_LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";

    private ProgressBar progressLoading;
    private TextView tvProgressPercent;
    private TextView tvLoadingSubtitle;
    private RelativeLayout flNativeAdContainer;
    private View shimmerLoadingAd;

    private CardView cardPosterContainer;
    private ImageView imgPosterActive;
    private TextView tvPosterTag;
    private View viewCanvasSheet;
    private ImageView imgSparkle1;
    private ImageView imgSparkle2;
    private ImageView imgSparkle3;
    private ImageView imgDrawingPencil;
    private View viewSketchHeader;
    private View viewSketchArtBox;
    private View layoutSketchLines;

    private Leaflet_PreferenceClass preferenceClass;
    private ValueAnimator progressAnimator;
    private ObjectAnimator canvasBreatheAnimator;
    private ObjectAnimator sparkleAnimator1;
    private ObjectAnimator sparkleAnimator2;
    private ObjectAnimator sparkleAnimator3;
    private NativeAd loadedNativeAd;

    private final Handler posterFlipHandler = new Handler(Looper.getMainLooper());
    private Runnable posterFlipRunnable;
    private int currentPosterIndex = 0;
    private final List<PosterShowcaseItem> posterItems = new ArrayList<>();

    private boolean isNavigating = false;

    private static class PosterShowcaseItem {
        final int drawableRes;
        final String tag;
        final String subtitle;

        PosterShowcaseItem(int drawableRes, String tag, String subtitle) {
            this.drawableRes = drawableRes;
            this.tag = tag;
            this.subtitle = subtitle;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Clean light status bar matching white theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.leaflet_activity_loading);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initPosterItems();
        initViews();
        startAmbientAnimations();
        startBlankCanvasCreationLoop();
        loadNativeAdIfEnabled();
        startProgressCountdown();
    }

    private void initPosterItems() {
        posterItems.add(new PosterShowcaseItem(R.drawable.onboard_p5_diwali, "✨ FESTIVAL", "Curating 10,000+ Festive Templates..."));
        posterItems.add(new PosterShowcaseItem(R.drawable.onboard_p6_template, "🔥 50% MEGA SALE", "Generating High-Converting Business Ads..."));
        posterItems.add(new PosterShowcaseItem(R.drawable.onboard_p4_restaurant, "🍔 FOOD & CAFE", "Setting up Restaurant & Menu Flyers..."));
        posterItems.add(new PosterShowcaseItem(R.drawable.onboard_p2_birthday, "🎉 BIRTHDAY BASH", "Customizing Birthday & Party Invitations..."));
        posterItems.add(new PosterShowcaseItem(R.drawable.onboard_p1_creative, "💼 BRAND & BIZ", "Calibrating Creative Studio & Tools..."));
    }

    private void initViews() {
        progressLoading = findViewById(R.id.progress_loading);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvLoadingSubtitle = findViewById(R.id.tv_loading_subtitle);
        flNativeAdContainer = findViewById(R.id.fl_native_ad_container);
        shimmerLoadingAd = findViewById(R.id.shimmer_loading_ad);

        cardPosterContainer = findViewById(R.id.card_poster_container);
        imgPosterActive = findViewById(R.id.img_poster_active);
        tvPosterTag = findViewById(R.id.tv_poster_tag);
        viewCanvasSheet = findViewById(R.id.view_canvas_sheet);
        imgSparkle1 = findViewById(R.id.img_sparkle_1);
        imgSparkle2 = findViewById(R.id.img_sparkle_2);
        imgSparkle3 = findViewById(R.id.img_sparkle_3);
        imgDrawingPencil = findViewById(R.id.img_drawing_pencil);

        viewSketchHeader = findViewById(R.id.sketch_header);
        viewSketchArtBox = findViewById(R.id.sketch_art_box);
        layoutSketchLines = findViewById(R.id.sketch_lines_layout);

        // Adjust camera distance for crisp 3D card flips without clipping
        float distance = getResources().getDisplayMetrics().density * 8000;
        cardPosterContainer.setCameraDistance(distance);

        // Initial state: Pure Blank Canvas
        imgPosterActive.setAlpha(0f);
        tvPosterTag.setText("✏️ BLANK CANVAS");
        if (!posterItems.isEmpty()) {
            tvLoadingSubtitle.setText("Crafting Canvas: " + posterItems.get(0).subtitle);
        }
    }

    /**
     * Ambient animations: Canvas breathing loop & twinkling sparkles
     */
    private void startAmbientAnimations() {
        // 1. Subtle canvas guide breathing loop
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.03f, 1.0f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.03f, 1.0f);
        canvasBreatheAnimator = ObjectAnimator.ofPropertyValuesHolder(viewCanvasSheet, pvhScaleX, pvhScaleY);
        canvasBreatheAnimator.setDuration(2400);
        canvasBreatheAnimator.setRepeatCount(ValueAnimator.INFINITE);
        canvasBreatheAnimator.setRepeatMode(ValueAnimator.REVERSE);
        canvasBreatheAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        canvasBreatheAnimator.start();

        // 2. Twinkling Sparkles
        PropertyValuesHolder spScaleX1 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1.3f);
        PropertyValuesHolder spScaleY1 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1.3f);
        PropertyValuesHolder spAlpha1 = PropertyValuesHolder.ofFloat(View.ALPHA, 0.3f, 1.0f);
        sparkleAnimator1 = ObjectAnimator.ofPropertyValuesHolder(imgSparkle1, spScaleX1, spScaleY1, spAlpha1);
        sparkleAnimator1.setDuration(900);
        sparkleAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        sparkleAnimator1.setRepeatMode(ValueAnimator.REVERSE);
        sparkleAnimator1.start();

        PropertyValuesHolder spScaleX2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.25f, 0.6f);
        PropertyValuesHolder spScaleY2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.25f, 0.6f);
        PropertyValuesHolder spAlpha2 = PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f, 0.25f);
        sparkleAnimator2 = ObjectAnimator.ofPropertyValuesHolder(imgSparkle2, spScaleX2, spScaleY2, spAlpha2);
        sparkleAnimator2.setDuration(1200);
        sparkleAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        sparkleAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        sparkleAnimator2.start();

        PropertyValuesHolder spScaleX3 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.6f, 1.15f);
        PropertyValuesHolder spScaleY3 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.6f, 1.15f);
        PropertyValuesHolder spAlpha3 = PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 0.9f);
        sparkleAnimator3 = ObjectAnimator.ofPropertyValuesHolder(imgSparkle3, spScaleX3, spScaleY3, spAlpha3);
        sparkleAnimator3.setDuration(1400);
        sparkleAnimator3.setRepeatCount(ValueAnimator.INFINITE);
        sparkleAnimator3.setRepeatMode(ValueAnimator.REVERSE);
        sparkleAnimator3.start();
    }

    /**
     * 3D Poster Card Flip Showcase: switches through trending templates every 2 seconds
     */
    /**
     * Blank Canvas Creation Loop:
     * 1. Starts with a pure, empty Blank Canvas.
     * 2. Pencil sweeps across drawing title, graphics frame & body lines on the blank canvas.
     * 3. Once sketched, the poster magically comes alive with full vibrant colors!
     * 4. 3D Flips to clear canvas back to fresh Blank Canvas and repeats for next design.
     */
    private void startBlankCanvasCreationLoop() {
        if (posterItems.isEmpty() || imgDrawingPencil == null) return;

        float density = getResources().getDisplayMetrics().density;
        imgDrawingPencil.setPivotX(9f * density);
        imgDrawingPencil.setPivotY(43f * density);

        runBlankCanvasCreationCycle();
    }

    private void runBlankCanvasCreationCycle() {
        if (isFinishing() || isDestroyed()) return;

        final PosterShowcaseItem currentItem = posterItems.get(currentPosterIndex);
        float density = getResources().getDisplayMetrics().density;

        // Phase 1 (0ms): Pristine Blank Canvas
        imgPosterActive.setAlpha(0f);
        if (viewSketchHeader != null) {
            viewSketchHeader.setAlpha(0f);
            viewSketchHeader.setScaleX(0.1f);
        }
        if (viewSketchArtBox != null) {
            viewSketchArtBox.setAlpha(0f);
            viewSketchArtBox.setScaleX(0.1f);
            viewSketchArtBox.setScaleY(0.1f);
        }
        if (layoutSketchLines != null) {
            layoutSketchLines.setAlpha(0f);
            layoutSketchLines.setScaleX(0.1f);
        }

        tvPosterTag.setText("✏️ BLANK CANVAS");
        tvLoadingSubtitle.setText("Drafting on Canvas...");

        // Pencil moves to starting point at top-left of the blank canvas
        imgDrawingPencil.animate()
                .translationX(18f * density)
                .translationY(22f * density)
                .rotation(-28f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(220)
                .start();

        // Phase 2 (280ms): Pencil draws the top header across the blank canvas
        posterFlipHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            imgDrawingPencil.animate()
                    .translationX(78f * density)
                    .translationY(26f * density)
                    .rotation(-18f)
                    .setDuration(450)
                    .start();

            if (viewSketchHeader != null) {
                viewSketchHeader.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .setDuration(450)
                        .start();
            }
        }, 280);

        // Phase 3 (780ms): Pencil curves into center, drawing the graphic/photo frame
        posterFlipHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            imgDrawingPencil.animate()
                    .translationX(55f * density)
                    .translationY(75f * density)
                    .rotation(-32f)
                    .setDuration(550)
                    .start();

            if (viewSketchArtBox != null) {
                viewSketchArtBox.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(550)
                        .setInterpolator(new OvershootInterpolator(1.4f))
                        .start();
            }
        }, 780);

        // Phase 4 (1380ms): Pencil sweeps across bottom, sketching text lines
        posterFlipHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            imgDrawingPencil.animate()
                    .translationX(80f * density)
                    .translationY(148f * density)
                    .rotation(-18f)
                    .setDuration(450)
                    .start();

            if (layoutSketchLines != null) {
                layoutSketchLines.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .setDuration(450)
                        .start();
            }
        }, 1380);

        // Phase 5 (1900ms): The sketched poster magically comes alive with vibrant color!
        posterFlipHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;

            // Lift pencil up gently
            imgDrawingPencil.animate()
                    .translationX(96f * density)
                    .translationY(132f * density)
                    .scaleX(1.15f)
                    .scaleY(1.15f)
                    .rotation(-14f)
                    .setDuration(300)
                    .start();

            // Swap poster image and fade in smoothly
            imgPosterActive.setImageResource(currentItem.drawableRes);
            imgPosterActive.animate()
                    .alpha(1f)
                    .setDuration(350)
                    .start();

            // Joyful pop for the card container and category tag
            cardPosterContainer.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.8f))
                    .withEndAction(() -> cardPosterContainer.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180).start())
                    .start();

            tvPosterTag.setText(currentItem.tag);
            tvPosterTag.setScaleX(0.7f);
            tvPosterTag.setScaleY(0.7f);
            tvPosterTag.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(260)
                    .setInterpolator(new OvershootInterpolator(2.0f))
                    .start();

            tvLoadingSubtitle.setText(currentItem.subtitle);
        }, 1900);

        // Phase 6 (3100ms): 3D Flip transition back to a fresh Blank Canvas for next design
        posterFlipHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;

            cardPosterContainer.animate()
                    .rotationY(90f)
                    .setDuration(200)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (isFinishing() || isDestroyed()) return;

                            currentPosterIndex = (currentPosterIndex + 1) % posterItems.size();

                            // Reset views to clean Blank Canvas
                            imgPosterActive.setAlpha(0f);
                            if (viewSketchHeader != null) viewSketchHeader.setAlpha(0f);
                            if (viewSketchArtBox != null) viewSketchArtBox.setAlpha(0f);
                            if (layoutSketchLines != null) layoutSketchLines.setAlpha(0f);
                            tvPosterTag.setText("✏️ BLANK CANVAS");

                            // Flip in to reveal the fresh blank canvas
                            cardPosterContainer.setRotationY(-90f);
                            cardPosterContainer.animate()
                                    .rotationY(0f)
                                    .setDuration(220)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setListener(null)
                                    .withEndAction(() -> {
                                        // Run next creation cycle on blank canvas
                                        runBlankCanvasCreationCycle();
                                    })
                                    .start();
                        }
                    })
                    .start();
        }, 3100);
    }

    /**
     * Smooth 0% -> 100% progress animation driven by timer_seconds from Remote Config
     */
    private void startProgressCountdown() {
        int timerSeconds = preferenceClass.getLoadingScreenTimerSeconds();
        if (timerSeconds <= 0) {
            timerSeconds = 10;
        }

        long totalDurationMs = timerSeconds * 1000L;

        progressAnimator = ValueAnimator.ofInt(0, 100);
        progressAnimator.setDuration(totalDurationMs);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            if (isFinishing() || isDestroyed()) return;

            int progress = (int) animation.getAnimatedValue();
            progressLoading.setProgress(progress);
            tvProgressPercent.setText(progress + "%");

            if (progress >= 95) {
                tvLoadingSubtitle.setText("Your Design Studio is Ready!");
            }
        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                navigateToHome();
            }
        });

        progressAnimator.start();
    }

    /**
     * Load Bottom Native Ad based on Remote Config json settings
     */
    private void loadNativeAdIfEnabled() {
        if (!preferenceClass.isLoadingNativeAdEnabled()) {
            flNativeAdContainer.setVisibility(View.GONE);
            return;
        }

        // Apply custom container height if defined in Remote Config
        int customHeightDp = preferenceClass.getLoadingNativeAdHeight();
        if (customHeightDp > 0) {
            int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, customHeightDp, getResources().getDisplayMetrics());
            ViewGroup.LayoutParams lp = flNativeAdContainer.getLayoutParams();
            if (lp != null) {
                lp.height = heightPx;
                flNativeAdContainer.setLayoutParams(lp);
            }
        }

        String adUnitId = preferenceClass.getLoadingNativeAdId();
        if (BuildConfig.DEBUG) {
            adUnitId = "ca-app-pub-3940256099942544/2247696110"; // Test Native Ad Unit
            Log.d(TAG, "Debug Mode: Using Test Native ID");
        }

        if (adUnitId == null || adUnitId.trim().isEmpty() || adUnitId.equals("null")) {
            flNativeAdContainer.setVisibility(View.GONE);
            return;
        }

        try {
            AdLoader.Builder builder = new AdLoader.Builder(this, adUnitId);

            builder.forNativeAd(nativeAd -> {
                if (isFinishing() || isDestroyed()) {
                    nativeAd.destroy();
                    return;
                }

                if (loadedNativeAd != null) {
                    loadedNativeAd.destroy();
                }
                loadedNativeAd = nativeAd;

                NativeAdView adView = (NativeAdView) LayoutInflater.from(this).inflate(R.layout.layout_loading_native_ad, null);
                populateNativeAd(nativeAd, adView);

                flNativeAdContainer.removeAllViews();
                flNativeAdContainer.addView(adView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                flNativeAdContainer.setVisibility(View.VISIBLE);
            });

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
            builder.withNativeAdOptions(adOptions);

            builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.e(TAG, "Native ad failed to load: " + loadAdError.getMessage());
                    flNativeAdContainer.removeAllViews();
                    flNativeAdContainer.setVisibility(View.GONE);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Leaflet_AppOpenManager.setAdClickActive(true);
                }
            });

            AdLoader adLoader = builder.build();
            adLoader.loadAd(new AdRequest.Builder().build());

        } catch (Exception e) {
            Log.e(TAG, "Error loading native ad: " + e.getMessage());
            flNativeAdContainer.setVisibility(View.GONE);
        }
    }

    private void populateNativeAd(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        TextView headlineView = adView.findViewById(R.id.ad_headline);
        adView.setHeadlineView(headlineView);
        if (headlineView != null) {
            headlineView.setText(nativeAd.getHeadline());
        }

        TextView bodyView = adView.findViewById(R.id.ad_body);
        adView.setBodyView(bodyView);
        String bodyText = nativeAd.getBody();
        if (bodyText == null || bodyText.trim().isEmpty()) {
            bodyText = nativeAd.getAdvertiser();
        }
        if (bodyText != null && !bodyText.trim().isEmpty() && bodyView != null) {
            bodyView.setText(bodyText);
            bodyView.setVisibility(View.VISIBLE);
        } else if (bodyView != null) {
            bodyView.setVisibility(View.GONE);
        }

        Button ctaView = adView.findViewById(R.id.ad_call_to_action);
        adView.setCallToActionView(ctaView);
        if (ctaView != null) {
            String cta = nativeAd.getCallToAction();
            if (cta != null && !cta.trim().isEmpty()) {
                ctaView.setText(cta.toUpperCase());
            } else {
                ctaView.setText("INSTALL NOW");
            }
        }

        adView.setNativeAd(nativeAd);
    }

    private void navigateToHome() {
        if (isNavigating) return;
        isNavigating = true;

        Intent intent = new Intent(Leaflet_LoadingActivity.this, Leaflet_PosterMainActivity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent accidental exit during studio crafting
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (posterFlipHandler != null && posterFlipRunnable != null) {
            posterFlipHandler.removeCallbacks(posterFlipRunnable);
        }
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        if (canvasBreatheAnimator != null) {
            canvasBreatheAnimator.cancel();
        }
        if (sparkleAnimator1 != null) {
            sparkleAnimator1.cancel();
        }
        if (sparkleAnimator2 != null) {
            sparkleAnimator2.cancel();
        }
        if (sparkleAnimator3 != null) {
            sparkleAnimator3.cancel();
        }
        if (posterFlipHandler != null) {
            posterFlipHandler.removeCallbacksAndMessages(null);
        }
        if (loadedNativeAd != null) {
            loadedNativeAd.destroy();
        }
    }
}
