package com.online.flyer.design.postermaker.Leaflet_activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

public class Leaflet_OnboardingStep3Activity extends AppCompatActivity {

    private boolean isTransitioning = false;

    private ImageView btnBack;
    private RelativeLayout nativeAdContainer;
    private TextView btnContinue;

    // Step 3 Views (1-Click Sharing Mockup & Viral Simulation)
    private View glowBgStep3;
    private CardView cardSharePoster;
    private ImageView ivSharePoster;
    private TextView badgeSaved, badgeUltraHd;
    private View cardShareWhatsapp, cardShareInstagram;
    private View bubbleWhatsappSent, bubbleInstagramLikes;
    private ImageView ivSparkleGold, ivSparklePurple;
    private LinearLayout llQuickShareBar;
    private View btnShareWhatsapp, btnShareInstagram, btnShareFacebook, btnShareDownload;

    // Step 3 Animators & Loop Handlers
    private ValueAnimator floatAnimStep3Poster;
    private ValueAnimator floatAnimWhatsapp;
    private ValueAnimator floatAnimInstagram;
    private ValueAnimator rotateAnimSparkleGold;
    private ValueAnimator rotateAnimSparklePurple;
    private ValueAnimator pulseAnimBtn;
    private final Handler screen3Handler = new Handler(Looper.getMainLooper());
    private Runnable shareCycleRunnable;
    private int shareCycleMode = 0;

    private Leaflet_PreferenceClass preferenceClass;
    private Leaflet_InterstitialAdManager interstitialAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = getWindow();
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(android.graphics.Color.WHITE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        setContentView(R.layout.leaflet_activity_onboarding_step3);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initViews();
        setupListeners();

        // Setup Ads
        String customInterId = preferenceClass.getOnboardingInterstitialAdId();
        interstitialAdManager = new Leaflet_InterstitialAdManager(this, customInterId);
        if (preferenceClass.isMasterOnboardingInterstitialEnabled() && preferenceClass.isOnboardingInterstitialEnabled(3)) {
            interstitialAdManager.fetchAdMobAd();
        }

        loadNativeAd();
        animateStep3Entrance();
        setupStep3InteractiveListeners();
    }

    private void initViews() {
        View rootLayout = findViewById(R.id.root_onboarding_step3);
        if (rootLayout != null) {
            int statusBarHeight = 0;
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            final int minTopPadding = statusBarHeight;

            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, windowInsets) -> {
                androidx.core.graphics.Insets insets = windowInsets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                int topInset = insets.top > 0 ? insets.top : Math.max(windowInsets.getSystemWindowInsetTop(), minTopPadding);
                int bottomInset = insets.bottom > 0 ? insets.bottom : windowInsets.getSystemWindowInsetBottom();
                v.setPadding(0, topInset, 0, bottomInset);
                return windowInsets;
            });
            androidx.core.view.ViewCompat.requestApplyInsets(rootLayout);
        }
        btnBack = findViewById(R.id.btn_back);
        nativeAdContainer = findViewById(R.id.native_ad_container);
        btnContinue = findViewById(R.id.btn_continue);

        glowBgStep3 = findViewById(R.id.glow_bg_step3);
        cardSharePoster = findViewById(R.id.card_share_poster);
        ivSharePoster = findViewById(R.id.iv_share_poster);
        badgeSaved = findViewById(R.id.badge_saved);
        badgeUltraHd = findViewById(R.id.badge_ultra_hd);

        cardShareWhatsapp = findViewById(R.id.card_share_whatsapp);
        cardShareInstagram = findViewById(R.id.card_share_instagram);
        bubbleWhatsappSent = findViewById(R.id.bubble_whatsapp_sent);
        bubbleInstagramLikes = findViewById(R.id.bubble_instagram_likes);

        ivSparkleGold = findViewById(R.id.iv_sparkle_gold);
        ivSparklePurple = findViewById(R.id.iv_sparkle_purple);

        llQuickShareBar = findViewById(R.id.ll_quick_share_bar);
        btnShareWhatsapp = findViewById(R.id.btn_share_whatsapp);
        btnShareInstagram = findViewById(R.id.btn_share_instagram);
        btnShareFacebook = findViewById(R.id.btn_share_facebook);
        btnShareDownload = findViewById(R.id.btn_share_download);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
        if (btnContinue != null) {
            btnContinue.setOnClickListener(v -> onContinueClicked());
        }
    }

    private void onContinueClicked() {
        if (isTransitioning || Leaflet_ClickUtils.isFastDoubleClick(900)) return;
        isTransitioning = true;
        if (btnContinue != null) btnContinue.setEnabled(false);

        boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(3);
        if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
            interstitialAdManager.showInterstitialAd(Leaflet_OnboardingStep3Activity.this, this::finishOnboarding);
        } else {
            finishOnboarding();
        }
    }

    @Override
    public void onBackPressed() {
        if (isTransitioning || Leaflet_ClickUtils.isFastDoubleClick(600)) return;
        super.onBackPressed();
        overridePendingTransition(R.anim.leaflet_slide_in_left, R.anim.leaflet_slide_out_right);
    }

    private void finishOnboarding() {
        preferenceClass.setBoolean("isOnboardingDone", true);

        try {
            com.google.firebase.analytics.FirebaseAnalytics analytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString("source", "onboarding");
            analytics.logEvent("onboarding_complete", bundle);
        } catch (Exception ignored) {
        }

        Intent intent;
        if (preferenceClass.isLoadingScreenEnabled() && preferenceClass.isShowLoadingAfterOnboarding()) {
            intent = new Intent(this, Leaflet_LoadingActivity.class);
        } else {
            intent = new Intent(this, Leaflet_PosterMainActivity.class);
        }
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    private void loadNativeAd() {
        if (nativeAdContainer == null) return;
        boolean isNativeEnabled = preferenceClass.isOnboardingNativeAdEnabled(3);
        if (!isNativeEnabled) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            nativeAdContainer.setTag(null);
        } else {
            int customHeightDp = preferenceClass.getOnboardingNativeAdHeight(3);
            ViewGroup.LayoutParams lp = nativeAdContainer.getLayoutParams();
            if (lp != null) {
                if (customHeightDp > 0) {
                    int heightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, customHeightDp, getResources().getDisplayMetrics());
                    lp.height = heightPx;
                } else {
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                nativeAdContainer.setLayoutParams(lp);
            }
            nativeAdContainer.setVisibility(View.VISIBLE);
            String customId = preferenceClass.getOnboardingNativeAdId(3);
            Leaflet_NativeAdUtil.loadOnboardingNativeAd(nativeAdContainer, this, customId);
        }
    }

    // ==========================================
    // STEP 3 ANIMATIONS (Viral Social Sharing Simulation)
    // ==========================================
    private void setupStep3InteractiveListeners() {
        if (btnShareWhatsapp != null) {
            btnShareWhatsapp.setOnClickListener(v -> {
                bounceShareBarItem(v);
                bounceSocialNode(cardShareWhatsapp, bubbleWhatsappSent);
                Toast.makeText(this, "Opening WhatsApp...", Toast.LENGTH_SHORT).show();
            });
        }
        if (btnShareInstagram != null) {
            btnShareInstagram.setOnClickListener(v -> {
                bounceShareBarItem(v);
                bounceSocialNode(cardShareInstagram, bubbleInstagramLikes);
                Toast.makeText(this, "Opening Instagram...", Toast.LENGTH_SHORT).show();
            });
        }
        if (btnShareFacebook != null) {
            btnShareFacebook.setOnClickListener(v -> {
                bounceShareBarItem(v);
                Toast.makeText(this, "Opening Facebook...", Toast.LENGTH_SHORT).show();
            });
        }
        if (btnShareDownload != null) {
            btnShareDownload.setOnClickListener(v -> {
                bounceShareBarItem(v);
                if (badgeSaved != null) {
                    badgeSaved.animate().scaleX(1.25f).scaleY(1.25f).setDuration(160)
                            .withEndAction(() -> badgeSaved.animate().scaleX(1f).scaleY(1f).setDuration(160).start()).start();
                }
                Toast.makeText(this, "Poster saved in Ultra HD!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void bounceShareBarItem(View v) {
        if (v == null) return;
        v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(120)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                        .setInterpolator(new OvershootInterpolator(1.8f)).start())
                .start();
    }

    private void bounceSocialNode(View card, View bubble) {
        if (card != null) {
            card.animate()
                    .scaleX(1.08f).scaleY(1.08f)
                    .setDuration(220)
                    .setInterpolator(new OvershootInterpolator(1.8f))
                    .withEndAction(() -> card.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                    .start();
        }
        if (bubble != null) {
            bubble.animate()
                    .scaleX(1.15f).scaleY(1.15f)
                    .setDuration(220)
                    .setInterpolator(new OvershootInterpolator(2.2f))
                    .withEndAction(() -> bubble.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                    .start();
        }
    }

    private void animateStep3Entrance() {
        if (cardSharePoster == null) return;

        float density = getResources().getDisplayMetrics().density;
        stopStep3Animations();

        cardSharePoster.setScaleX(0.82f);
        cardSharePoster.setScaleY(0.82f);
        cardSharePoster.setAlpha(0f);
        cardSharePoster.setTranslationY(16 * density);

        if (glowBgStep3 != null) {
            glowBgStep3.setAlpha(0f);
            glowBgStep3.setScaleX(0.6f);
            glowBgStep3.setScaleY(0.6f);
            glowBgStep3.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(600).start();
        }

        if (badgeSaved != null) {
            badgeSaved.setAlpha(0f);
            badgeSaved.setScaleX(0.3f);
            badgeSaved.setScaleY(0.3f);
        }
        if (badgeUltraHd != null) {
            badgeUltraHd.setAlpha(0f);
        }

        if (cardShareWhatsapp != null) {
            cardShareWhatsapp.setAlpha(0f);
            cardShareWhatsapp.setTranslationX(-50 * density);
        }
        if (bubbleWhatsappSent != null) {
            bubbleWhatsappSent.setAlpha(0f);
            bubbleWhatsappSent.setScaleX(0.2f);
            bubbleWhatsappSent.setScaleY(0.2f);
        }

        if (cardShareInstagram != null) {
            cardShareInstagram.setAlpha(0f);
            cardShareInstagram.setTranslationX(50 * density);
        }
        if (bubbleInstagramLikes != null) {
            bubbleInstagramLikes.setAlpha(0f);
            bubbleInstagramLikes.setScaleX(0.2f);
            bubbleInstagramLikes.setScaleY(0.2f);
        }

        if (ivSparkleGold != null) {
            ivSparkleGold.setAlpha(0f);
            ivSparkleGold.setScaleX(0.2f);
            ivSparkleGold.setScaleY(0.2f);
        }
        if (ivSparklePurple != null) {
            ivSparklePurple.setAlpha(0f);
            ivSparklePurple.setScaleX(0.2f);
            ivSparklePurple.setScaleY(0.2f);
        }

        if (llQuickShareBar != null) {
            llQuickShareBar.setAlpha(0f);
            llQuickShareBar.setTranslationY(24 * density);
        }

        cardSharePoster.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(550)
                .setInterpolator(new OvershootInterpolator(1.4f))
                .withEndAction(() -> {
                    startScreen3ContinuousFloating();
                    startScreen3ShareCycle();
                })
                .start();

        if (badgeSaved != null) {
            badgeSaved.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(320)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(1.8f))
                    .start();
        }
        if (badgeUltraHd != null) {
            badgeUltraHd.animate()
                    .alpha(1f)
                    .setStartDelay(400)
                    .setDuration(350)
                    .start();
        }

        if (cardShareWhatsapp != null) {
            cardShareWhatsapp.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setStartDelay(160)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        }
        if (bubbleWhatsappSent != null) {
            bubbleWhatsappSent.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(300)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(2.2f))
                    .start();
        }

        if (cardShareInstagram != null) {
            cardShareInstagram.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setStartDelay(280)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        }
        if (bubbleInstagramLikes != null) {
            bubbleInstagramLikes.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(420)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(2.2f))
                    .start();
        }

        if (ivSparkleGold != null) {
            ivSparkleGold.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(320)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.6f))
                    .start();
        }
        if (ivSparklePurple != null) {
            ivSparklePurple.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(440)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.6f))
                    .start();
        }

        if (llQuickShareBar != null) {
            llQuickShareBar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(380)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }

        startButtonPulseAnimation();
    }

    private void startScreen3ContinuousFloating() {
        if (isFinishing() || isDestroyed()) return;

        float density = getResources().getDisplayMetrics().density;
        stopFloatingAnimationStep3();

        floatAnimStep3Poster = ValueAnimator.ofFloat(0, -6 * density);
        floatAnimStep3Poster.setDuration(2200);
        floatAnimStep3Poster.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimStep3Poster.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimStep3Poster.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimStep3Poster.addUpdateListener(anim -> {
            if (cardSharePoster != null) {
                cardSharePoster.setTranslationY((float) anim.getAnimatedValue());
            }
        });
        floatAnimStep3Poster.start();

        floatAnimWhatsapp = ValueAnimator.ofFloat(0, -5 * density);
        floatAnimWhatsapp.setDuration(1900);
        floatAnimWhatsapp.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimWhatsapp.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimWhatsapp.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimWhatsapp.addUpdateListener(anim -> {
            if (cardShareWhatsapp != null) {
                cardShareWhatsapp.setTranslationY((float) anim.getAnimatedValue());
            }
        });
        floatAnimWhatsapp.start();

        floatAnimInstagram = ValueAnimator.ofFloat(0, 5 * density);
        floatAnimInstagram.setDuration(2300);
        floatAnimInstagram.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimInstagram.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimInstagram.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimInstagram.addUpdateListener(anim -> {
            if (cardShareInstagram != null) {
                cardShareInstagram.setTranslationY((float) anim.getAnimatedValue());
            }
        });
        floatAnimInstagram.start();

        rotateAnimSparkleGold = ValueAnimator.ofFloat(0f, 360f);
        rotateAnimSparkleGold.setDuration(4000);
        rotateAnimSparkleGold.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimSparkleGold.setInterpolator(new LinearInterpolator());
        rotateAnimSparkleGold.addUpdateListener(anim -> {
            if (ivSparkleGold != null) {
                ivSparkleGold.setRotation((float) anim.getAnimatedValue());
            }
        });
        rotateAnimSparkleGold.start();

        rotateAnimSparklePurple = ValueAnimator.ofFloat(360f, 0f);
        rotateAnimSparklePurple.setDuration(4800);
        rotateAnimSparklePurple.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimSparklePurple.setInterpolator(new LinearInterpolator());
        rotateAnimSparklePurple.addUpdateListener(anim -> {
            if (ivSparklePurple != null) {
                ivSparklePurple.setRotation((float) anim.getAnimatedValue());
            }
        });
        rotateAnimSparklePurple.start();
    }

    private void startScreen3ShareCycle() {
        stopScreen3ShareCycle();
        shareCycleRunnable = new Runnable() {
            @Override
            public void run() {
                if (isFinishing() || isDestroyed()) return;
                if (shareCycleMode == 0) {
                    bounceSocialNode(cardShareWhatsapp, bubbleWhatsappSent);
                    bounceShareBarItem(btnShareWhatsapp);
                    shareCycleMode = 1;
                } else {
                    bounceSocialNode(cardShareInstagram, bubbleInstagramLikes);
                    bounceShareBarItem(btnShareInstagram);
                    shareCycleMode = 0;
                }
                screen3Handler.postDelayed(this, 2400);
            }
        };
        screen3Handler.postDelayed(shareCycleRunnable, 2400);
    }

    private void stopScreen3ShareCycle() {
        if (screen3Handler != null && shareCycleRunnable != null) {
            screen3Handler.removeCallbacks(shareCycleRunnable);
        }
    }

    private void startButtonPulseAnimation() {
        if (btnContinue == null || isFinishing() || isDestroyed()) return;

        stopButtonPulseAnimation();
        btnContinue.setPivotX(btnContinue.getWidth() / 2f);
        btnContinue.setPivotY(btnContinue.getHeight() / 2f);

        pulseAnimBtn = ValueAnimator.ofFloat(1.0f, 1.025f);
        pulseAnimBtn.setDuration(950);
        pulseAnimBtn.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimBtn.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimBtn.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseAnimBtn.addUpdateListener(animation -> {
            if (btnContinue != null) {
                float val = (float) animation.getAnimatedValue();
                btnContinue.setScaleX(val);
                btnContinue.setScaleY(val);
            }
        });
        pulseAnimBtn.start();
    }

    private void stopFloatingAnimationStep3() {
        if (floatAnimStep3Poster != null) {
            floatAnimStep3Poster.cancel();
            floatAnimStep3Poster = null;
        }
        if (floatAnimWhatsapp != null) {
            floatAnimWhatsapp.cancel();
            floatAnimWhatsapp = null;
        }
        if (floatAnimInstagram != null) {
            floatAnimInstagram.cancel();
            floatAnimInstagram = null;
        }
        if (rotateAnimSparkleGold != null) {
            rotateAnimSparkleGold.cancel();
            rotateAnimSparkleGold = null;
        }
        if (rotateAnimSparklePurple != null) {
            rotateAnimSparklePurple.cancel();
            rotateAnimSparklePurple = null;
        }
    }

    private void stopButtonPulseAnimation() {
        if (pulseAnimBtn != null) {
            pulseAnimBtn.cancel();
            pulseAnimBtn = null;
            if (btnContinue != null) {
                btnContinue.setScaleX(1f);
                btnContinue.setScaleY(1f);
            }
        }
    }

    private void stopStep3Animations() {
        stopFloatingAnimationStep3();
        stopScreen3ShareCycle();
        stopButtonPulseAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "onboarding_step3");
        isTransitioning = false;
        if (btnContinue != null) btnContinue.setEnabled(true);
        startScreen3ContinuousFloating();
        startScreen3ShareCycle();
        startButtonPulseAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopStep3Animations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStep3Animations();
    }
}
