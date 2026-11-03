package com.online.flyer.design.postermaker.Leaflet_activities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

public class Leaflet_OnboardingActivity extends AppCompatActivity {

    private int currentStep = 1;
    private boolean isTransitioning = false;

    private ImageView btnBack;
    private View progressStep1, progressStep2, progressStep3;
    private LinearLayout layoutStep1, layoutStep2, layoutStep3;
    private RelativeLayout nativeAdContainer;
    private TextView btnContinue;

    // Step 1 Views
    private TextView tvTitleStep1, tvSubtitleStep1;
    private View glowBg;
    private CardView cardPosterLeft, cardPosterCenter, cardPosterRight;
    private ImageView ivPosterLeft, ivPosterCenter, ivPosterRight;
    private TextView badgeFestivals, badgeBusiness, badgeBirthday, badgeSale;
    private TextView[] step1Badges;

    // Step 1 Animators & Synced Categories
    private final int[] step1PosterDrawables = new int[]{
            R.drawable.onboard_p5_diwali,
            R.drawable.onboard_p1_creative,
            R.drawable.onboard_p2_birthday,
            R.drawable.onboard_p6_fashion_edit
    };
    private final String[] step1Titles = new String[]{
            "Festive Greetings & Celebrations",
            "Promote Your Business & Brand",
            "Birthday & Party Invitations",
            "Mega Sales & Promo Offers"
    };
    private final String[] step1Subtitles = new String[]{
            "Diwali, New Year & 100+ Indian Festival Posters",
            "Corporate Flyers, Ads & Marketing Post Designs",
            "Custom Wishes, Cake Photo Cards & Event Invites",
            "High-Converting 50% Off & Mega Discount Posts"
    };
    private int currentPosterIndex = 0;
    private final Handler carouselHandler = new Handler(Looper.getMainLooper());
    private Runnable carouselRunnable;
    private ValueAnimator floatAnimCenter;
    private ValueAnimator floatAnimLeft;
    private ValueAnimator floatAnimRight;

    // Step 2 Views (Interactive Live Customization - 2-Level Blue Theme)
    private RelativeLayout editStage;
    private View glowBgStep2;
    private CardView cardEditPoster;
    private ImageView ivEditPoster;
    private View viewCanvasBgOverlay;
    private RelativeLayout layoutHeadingContainer;
    private TextView tvPosterHeading;
    private RelativeLayout selectionBox;
    private TextView ivPosterSticker;
    private TextView badgeFinalReveal;
    // 6 Floating Callout Badges
    private TextView badgeCalloutBg, badgeCalloutDiscount, badgeCalloutFonts;
    private TextView badgeCalloutStickers, badgeCalloutShubh, badgeCalloutGlow;
    // 2-Level Bottom Tool Bar
    private LinearLayout layoutSubOptionsTray;
    private LinearLayout traySubBg, traySubText, traySubStickers, traySubGlow;
    private TextView subBg1, subBg2, subBg3;
    private TextView subText1, subText2, subText3;
    private TextView subSticker1, subSticker2, subSticker3;
    private TextView subGlow1, subGlow2, subGlow3;
    private LinearLayout layoutMainToolsBar;
    private TextView tabToolBg, tabToolText, tabToolStickers, tabToolGlow;
    private ImageView ivHandPointer;
    private ValueAnimator floatAnimStep2;
    private final Handler step2TimelineHandler = new Handler(Looper.getMainLooper());

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
        setContentView(R.layout.leaflet_activity_onboarding);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initViews();
        setupListeners();
        int startStep = getIntent().getIntExtra("start_step", 1);
        updateStepUI(startStep);

        // Setup Ads with dynamic JSON config
        String customInterId = preferenceClass.getOnboardingInterstitialAdId();
        interstitialAdManager = new Leaflet_InterstitialAdManager(this, customInterId);
        // Only preload interstitial if master switch and at least one step has it enabled in Remote Config JSON
        if (preferenceClass.isMasterOnboardingInterstitialEnabled() &&
                (preferenceClass.isOnboardingInterstitialEnabled(1) ||
                 preferenceClass.isOnboardingInterstitialEnabled(2) ||
                 preferenceClass.isOnboardingInterstitialEnabled(3))) {
            interstitialAdManager.fetchAdMobAd();
        }

        updateNativeAdForStep(startStep);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        progressStep1 = findViewById(R.id.progress_step_1);
        progressStep2 = findViewById(R.id.progress_step_2);
        progressStep3 = findViewById(R.id.progress_step_3);

        layoutStep1 = findViewById(R.id.layout_step_1);
        layoutStep2 = findViewById(R.id.layout_step_2);
        layoutStep3 = findViewById(R.id.layout_step_3);

        nativeAdContainer = findViewById(R.id.native_ad_container);
        btnContinue = findViewById(R.id.btn_continue);

        // Step 1
        tvTitleStep1 = findViewById(R.id.tv_title_step1);
        tvSubtitleStep1 = findViewById(R.id.tv_subtitle_step1);
        glowBg = findViewById(R.id.glow_bg);
        cardPosterLeft = findViewById(R.id.card_poster_left);
        cardPosterCenter = findViewById(R.id.card_poster_center);
        cardPosterRight = findViewById(R.id.card_poster_right);
        ivPosterLeft = findViewById(R.id.iv_poster_left);
        ivPosterCenter = findViewById(R.id.iv_poster_center);
        ivPosterRight = findViewById(R.id.iv_poster_right);

        badgeFestivals = findViewById(R.id.badge_festivals);
        badgeBusiness = findViewById(R.id.badge_business);
        badgeBirthday = findViewById(R.id.badge_birthday);
        badgeSale = findViewById(R.id.badge_sale);
        step1Badges = new TextView[]{badgeFestivals, badgeBusiness, badgeBirthday, badgeSale};
        setupStep1BadgeListeners();

        // Step 2 Views & 2-Level Toolbar in Blue Theme
        editStage = findViewById(R.id.edit_stage);
        glowBgStep2 = findViewById(R.id.glow_bg_step2);
        cardEditPoster = findViewById(R.id.card_edit_poster);
        ivEditPoster = findViewById(R.id.iv_edit_poster);
        viewCanvasBgOverlay = findViewById(R.id.view_canvas_bg_overlay);
        layoutHeadingContainer = findViewById(R.id.layout_heading_container);
        tvPosterHeading = findViewById(R.id.tv_poster_heading);
        selectionBox = findViewById(R.id.selection_box);
        ivPosterSticker = findViewById(R.id.iv_poster_sticker);
        badgeFinalReveal = findViewById(R.id.badge_final_reveal);

        badgeCalloutBg = findViewById(R.id.badge_callout_bg);
        badgeCalloutDiscount = findViewById(R.id.badge_callout_discount);
        badgeCalloutFonts = findViewById(R.id.badge_callout_fonts);
        badgeCalloutStickers = findViewById(R.id.badge_callout_stickers);
        badgeCalloutShubh = findViewById(R.id.badge_callout_shubh);
        badgeCalloutGlow = findViewById(R.id.badge_callout_glow);

        layoutSubOptionsTray = findViewById(R.id.layout_sub_options_tray);
        traySubBg = findViewById(R.id.tray_sub_bg);
        traySubText = findViewById(R.id.tray_sub_text);
        traySubStickers = findViewById(R.id.tray_sub_stickers);
        traySubGlow = findViewById(R.id.tray_sub_glow);

        subBg1 = findViewById(R.id.sub_bg_1);
        subBg2 = findViewById(R.id.sub_bg_2);
        subBg3 = findViewById(R.id.sub_bg_3);

        subText1 = findViewById(R.id.sub_text_1);
        subText2 = findViewById(R.id.sub_text_2);
        subText3 = findViewById(R.id.sub_text_3);

        subSticker1 = findViewById(R.id.sub_sticker_1);
        subSticker2 = findViewById(R.id.sub_sticker_2);
        subSticker3 = findViewById(R.id.sub_sticker_3);

        subGlow1 = findViewById(R.id.sub_glow_1);
        subGlow2 = findViewById(R.id.sub_glow_2);
        subGlow3 = findViewById(R.id.sub_glow_3);

        layoutMainToolsBar = findViewById(R.id.layout_main_tools_bar);
        tabToolBg = findViewById(R.id.tab_tool_bg);
        tabToolText = findViewById(R.id.tab_tool_text);
        tabToolStickers = findViewById(R.id.tab_tool_stickers);
        tabToolGlow = findViewById(R.id.tab_tool_glow);

        ivHandPointer = findViewById(R.id.iv_hand_pointer);

        setupStep2InteractiveListeners();

        // Step 3 Views
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
        btnBack.setOnClickListener(v -> onBackPressed());
        btnContinue.setOnClickListener(v -> onContinueClicked());
    }

    private void onContinueClicked() {
        if (isTransitioning) return;

        if (currentStep == 1) {
            boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(1);
            if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
                interstitialAdManager.showInterstitialAd(Leaflet_OnboardingActivity.this, () -> {
                    if (interstitialAdManager != null && preferenceClass.isOnboardingInterstitialEnabled(2)) {
                        interstitialAdManager.fetchAdMobAd();
                    }
                    goToStep(2);
                });
            } else {
                // Only pre-fetch next step if next step actually has interstitial enabled in Remote Config
                if (interstitialAdManager != null && preferenceClass.isOnboardingInterstitialEnabled(2)) {
                    interstitialAdManager.fetchAdMobAd();
                }
                goToStep(2);
            }
        } else if (currentStep == 2) {
            boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(2);
            if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
                interstitialAdManager.showInterstitialAd(Leaflet_OnboardingActivity.this, () -> {
                    if (interstitialAdManager != null && preferenceClass.isOnboardingInterstitialEnabled(3)) {
                        interstitialAdManager.fetchAdMobAd();
                    }
                    goToStep(3);
                });
            } else {
                if (interstitialAdManager != null && preferenceClass.isOnboardingInterstitialEnabled(3)) {
                    interstitialAdManager.fetchAdMobAd();
                }
                goToStep(3);
            }
        } else if (currentStep == 3) {
            boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(3);
            if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
                interstitialAdManager.showInterstitialAd(Leaflet_OnboardingActivity.this, this::finishOnboarding);
            } else {
                finishOnboarding();
            }
        }
    }

    private void goToStep(int targetStep) {
        if (isTransitioning) return;
        int prevStep = currentStep;
        if (prevStep == targetStep) return;
        currentStep = targetStep;

        updateStepIndicators(targetStep);
        updateNativeAdForStep(targetStep);

        LinearLayout outgoing = getLayoutForStep(prevStep);
        LinearLayout incoming = getLayoutForStep(targetStep);

        boolean isForward = targetStep > prevStep;

        // Animate back button visibility
        if (targetStep == 1) {
            btnBack.animate().alpha(0f).setDuration(160)
                    .withEndAction(() -> btnBack.setVisibility(View.GONE)).start();
        } else {
            if (btnBack.getVisibility() != View.VISIBLE) {
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setAlpha(0f);
            }
            btnBack.animate().alpha(1f).setDuration(220).start();
        }

        // Update continue button text
        btnContinue.setText(targetStep == 3 ? "Get Started  →" : "Continue  →");

        // Stop background animations of outgoing step
        if (prevStep == 1) stopStep1Animations();
        else if (prevStep == 2) stopStep2Animations();
        else if (prevStep == 3) stopStep3Animations();

        if (outgoing != null && incoming != null) {
            isTransitioning = true;
            float screenWidth = getResources().getDisplayMetrics().widthPixels;
            float outTargetX = isForward ? -screenWidth * 0.45f : screenWidth * 0.45f;
            float inStartX = isForward ? screenWidth * 0.45f : -screenWidth * 0.45f;

            // Prepare incoming layout
            incoming.setVisibility(View.VISIBLE);
            incoming.setTranslationX(inStartX);
            incoming.setAlpha(0f);

            // Animate outgoing: slide out & fade
            outgoing.animate()
                    .translationX(outTargetX)
                    .alpha(0f)
                    .setDuration(260)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> {
                        outgoing.setVisibility(View.GONE);
                        outgoing.setTranslationX(0f);
                        outgoing.setAlpha(1f);
                    })
                    .start();

            // Animate incoming: slide in & fade
            incoming.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator(1.4f))
                    .withEndAction(() -> {
                        isTransitioning = false;
                        triggerStepEntrance(targetStep);
                    })
                    .start();
        } else {
            triggerStepEntrance(targetStep);
        }
    }

    private void updateStepUI(int step) {
        currentStep = step;
        float density = getResources().getDisplayMetrics().density;

        setIndicatorStateInstant(progressStep1, step == 1, density);
        setIndicatorStateInstant(progressStep2, step == 2, density);
        setIndicatorStateInstant(progressStep3, step == 3, density);

        btnBack.setVisibility(step == 1 ? View.GONE : View.VISIBLE);
        btnContinue.setText(step == 3 ? "Get Started  →" : "Continue  →");

        layoutStep1.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        layoutStep2.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        layoutStep3.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        layoutStep1.setTranslationX(0f);
        layoutStep1.setAlpha(1f);
        layoutStep2.setTranslationX(0f);
        layoutStep2.setAlpha(1f);
        layoutStep3.setTranslationX(0f);
        layoutStep3.setAlpha(1f);

        triggerStepEntrance(step);
        updateNativeAdForStep(step);
    }

    private void updateStepIndicators(int targetStep) {
        animateStepIndicator(progressStep1, targetStep == 1);
        animateStepIndicator(progressStep2, targetStep == 2);
        animateStepIndicator(progressStep3, targetStep == 3);
    }

    private void animateStepIndicator(View dot, boolean toActive) {
        if (dot == null) return;
        float density = getResources().getDisplayMetrics().density;
        int targetWidth = (int) ((toActive ? 26 : 6) * density);
        int currentWidth = dot.getWidth() > 0 ? dot.getWidth() : (int) ((toActive ? 6 : 26) * density);

        ValueAnimator widthAnim = ValueAnimator.ofInt(currentWidth, targetWidth);
        widthAnim.setDuration(280);
        widthAnim.setInterpolator(new OvershootInterpolator(toActive ? 1.4f : 0.8f));
        widthAnim.addUpdateListener(anim -> {
            ViewGroup.LayoutParams lp = dot.getLayoutParams();
            if (lp != null) {
                lp.width = (int) anim.getAnimatedValue();
                dot.setLayoutParams(lp);
            }
        });
        widthAnim.start();

        if (toActive) {
            dot.setBackgroundResource(R.drawable.bg_onboarding_pill_active);
        } else {
            dot.setBackgroundResource(R.drawable.bg_onboarding_dot_inactive);
        }
    }

    private void setIndicatorStateInstant(View dot, boolean isActive, float density) {
        if (dot == null) return;
        ViewGroup.LayoutParams lp = dot.getLayoutParams();
        if (lp != null) {
            lp.width = (int) ((isActive ? 26 : 6) * density);
            dot.setLayoutParams(lp);
        }
        dot.setBackgroundResource(isActive ? R.drawable.bg_onboarding_pill_active : R.drawable.bg_onboarding_dot_inactive);
    }

    private LinearLayout getLayoutForStep(int step) {
        if (step == 1) return layoutStep1;
        if (step == 2) return layoutStep2;
        if (step == 3) return layoutStep3;
        return null;
    }

    private void triggerStepEntrance(int step) {
        if (step == 1) {
            animateStep1Entrance();
        } else if (step == 2) {
            animateStep2Entrance();
        } else if (step == 3) {
            animateStep3Entrance();
            setupStep3InteractiveListeners();
        }
    }

    // ==========================================
    // STEP 1 ANIMATIONS (3D Fan-Out & Carousel)
    // ==========================================
    private void animateStep1Entrance() {
        if (cardPosterCenter == null) return;

        float density = getResources().getDisplayMetrics().density;
        final float leftTargetX = -62 * density;
        final float rightTargetX = 62 * density;

        cardPosterCenter.setScaleX(0.7f);
        cardPosterCenter.setScaleY(0.7f);
        cardPosterCenter.setAlpha(0f);
        cardPosterCenter.setTranslationY(20 * density);

        cardPosterLeft.setScaleX(0.6f);
        cardPosterLeft.setScaleY(0.6f);
        cardPosterLeft.setAlpha(0f);
        cardPosterLeft.setTranslationX(0f);
        cardPosterLeft.setTranslationY(20 * density);
        cardPosterLeft.setRotation(0f);

        cardPosterRight.setScaleX(0.6f);
        cardPosterRight.setScaleY(0.6f);
        cardPosterRight.setAlpha(0f);
        cardPosterRight.setTranslationX(0f);
        cardPosterRight.setTranslationY(20 * density);
        cardPosterRight.setRotation(0f);

        if (glowBg != null) {
            glowBg.setAlpha(0f);
            glowBg.setScaleX(0.5f);
            glowBg.setScaleY(0.5f);
            glowBg.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(600).start();
        }

        resetBadge(badgeFestivals);
        resetBadge(badgeBusiness);
        resetBadge(badgeBirthday);
        resetBadge(badgeSale);

        cardPosterCenter.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(550)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        cardPosterLeft.animate()
                .alpha(1f)
                .scaleX(0.92f)
                .scaleY(0.92f)
                .translationX(leftTargetX)
                .translationY(6 * density)
                .rotation(-11f)
                .setDuration(650)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .start();

        cardPosterRight.animate()
                .alpha(1f)
                .scaleX(0.92f)
                .scaleY(0.92f)
                .translationX(rightTargetX)
                .translationY(6 * density)
                .rotation(11f)
                .setDuration(650)
                .setInterpolator(new OvershootInterpolator(1.2f))
                .withEndAction(this::startFloatingAnimationStep1)
                .start();

        applyStep1Category(0, false);
        animateBadgePop(badgeFestivals, 250);
        animateBadgePop(badgeBusiness, 350);
        animateBadgePop(badgeBirthday, 450);
        animateBadgePop(badgeSale, 550);
    }

    private void setupStep1BadgeListeners() {
        if (badgeFestivals != null) badgeFestivals.setOnClickListener(v -> selectStep1Category(0));
        if (badgeBusiness != null) badgeBusiness.setOnClickListener(v -> selectStep1Category(1));
        if (badgeBirthday != null) badgeBirthday.setOnClickListener(v -> selectStep1Category(2));
        if (badgeSale != null) badgeSale.setOnClickListener(v -> selectStep1Category(3));
    }

    private void selectStep1Category(int index) {
        if (currentStep != 1 || isFinishing() || isDestroyed()) return;
        stopPosterCarousel();
        applyStep1Category(index, true);
        startPosterCarousel();
    }

    private void applyStep1Category(int targetIndex, boolean isAnimated) {
        if (currentStep != 1 || isFinishing() || isDestroyed()) return;

        currentPosterIndex = targetIndex;
        float density = getResources().getDisplayMetrics().density;

        // 1. Synchronize Category Badges (Active highlight with spring pop)
        if (step1Badges != null) {
            for (int i = 0; i < step1Badges.length; i++) {
                TextView badge = step1Badges[i];
                if (badge == null) continue;
                if (i == targetIndex) {
                    badge.setBackgroundResource(R.drawable.bg_onboarding_badge_active);
                    badge.setTextColor(0xFFFFFFFF);
                    if (isAnimated) {
                        badge.animate()
                                .scaleX(1.14f)
                                .scaleY(1.14f)
                                .setDuration(300)
                                .setInterpolator(new OvershootInterpolator(1.8f))
                                .start();
                    } else {
                        badge.setScaleX(1.14f);
                        badge.setScaleY(1.14f);
                    }
                } else {
                    badge.setBackgroundResource(R.drawable.bg_onboarding_badge);
                    badge.setTextColor(0xFF111827);
                    if (isAnimated) {
                        badge.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(260)
                                .start();
                    } else {
                        badge.setScaleX(1.0f);
                        badge.setScaleY(1.0f);
                    }
                }
            }
        }

        // 2. Animate Title and Subtitle with Smooth Slide & Crossfade
        if (tvTitleStep1 != null && tvSubtitleStep1 != null) {
            if (isAnimated) {
                // Slide up & fade out
                tvTitleStep1.animate()
                        .alpha(0f)
                        .translationY(-12 * density)
                        .setDuration(160)
                        .withEndAction(() -> {
                            if (isFinishing() || isDestroyed()) return;
                            tvTitleStep1.setText(step1Titles[targetIndex]);
                            tvTitleStep1.setTranslationY(12 * density);
                            tvTitleStep1.animate()
                                    .alpha(1f)
                                    .translationY(0f)
                                    .setDuration(260)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .start();
                        })
                        .start();

                tvSubtitleStep1.animate()
                        .alpha(0f)
                        .translationY(-8 * density)
                        .setDuration(160)
                        .withEndAction(() -> {
                            if (isFinishing() || isDestroyed()) return;
                            tvSubtitleStep1.setText(step1Subtitles[targetIndex]);
                            tvSubtitleStep1.setTranslationY(8 * density);
                            tvSubtitleStep1.animate()
                                    .alpha(1f)
                                    .translationY(0f)
                                    .setDuration(280)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .start();
                        })
                        .start();
            } else {
                tvTitleStep1.setText(step1Titles[targetIndex]);
                tvSubtitleStep1.setText(step1Subtitles[targetIndex]);
            }
        }

        // 3. Card Transition (Center Card Zoom + Left/Right Cards Slide & Tilt)
        if (ivPosterCenter != null) {
            if (isAnimated) {
                ivPosterCenter.animate()
                        .alpha(0.25f)
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(180)
                        .withEndAction(() -> {
                            if (isFinishing() || isDestroyed()) return;
                            ivPosterCenter.setImageResource(step1PosterDrawables[targetIndex]);

                            int prevIdx = (targetIndex + step1PosterDrawables.length - 1) % step1PosterDrawables.length;
                            int nextIdx = (targetIndex + 1) % step1PosterDrawables.length;
                            if (ivPosterLeft != null) ivPosterLeft.setImageResource(step1PosterDrawables[prevIdx]);
                            if (ivPosterRight != null) ivPosterRight.setImageResource(step1PosterDrawables[nextIdx]);

                            ivPosterCenter.animate()
                                    .alpha(1f)
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(320)
                                    .setInterpolator(new OvershootInterpolator(1.2f))
                                    .start();

                            // Subtle card tilt twitch for 3D realism
                            if (cardPosterLeft != null) {
                                cardPosterLeft.animate().rotation(-13f).setDuration(150)
                                        .withEndAction(() -> cardPosterLeft.animate().rotation(-11f).setDuration(200).start())
                                        .start();
                            }
                            if (cardPosterRight != null) {
                                cardPosterRight.animate().rotation(13f).setDuration(150)
                                        .withEndAction(() -> cardPosterRight.animate().rotation(11f).setDuration(200).start())
                                        .start();
                            }
                        })
                        .start();

                // Ambient glow pulse
                if (glowBg != null) {
                    glowBg.animate()
                            .scaleX(1.08f)
                            .scaleY(1.08f)
                            .setDuration(180)
                            .withEndAction(() -> glowBg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).start())
                            .start();
                }
            } else {
                ivPosterCenter.setImageResource(step1PosterDrawables[targetIndex]);
                int prevIdx = (targetIndex + step1PosterDrawables.length - 1) % step1PosterDrawables.length;
                int nextIdx = (targetIndex + 1) % step1PosterDrawables.length;
                if (ivPosterLeft != null) ivPosterLeft.setImageResource(step1PosterDrawables[prevIdx]);
                if (ivPosterRight != null) ivPosterRight.setImageResource(step1PosterDrawables[nextIdx]);
            }
        }
    }

    private void startFloatingAnimationStep1() {
        if (isFinishing() || isDestroyed()) return;

        float density = getResources().getDisplayMetrics().density;
        stopFloatingAnimationStep1();

        floatAnimCenter = ValueAnimator.ofFloat(0, -6 * density);
        floatAnimCenter.setDuration(2200);
        floatAnimCenter.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimCenter.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimCenter.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimCenter.addUpdateListener(animation -> {
            if (cardPosterCenter != null) {
                cardPosterCenter.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        floatAnimCenter.start();

        float baseLeftY = 6 * density;
        floatAnimLeft = ValueAnimator.ofFloat(baseLeftY - 5 * density, baseLeftY + 5 * density);
        floatAnimLeft.setDuration(2600);
        floatAnimLeft.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimLeft.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimLeft.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimLeft.addUpdateListener(animation -> {
            if (cardPosterLeft != null) {
                cardPosterLeft.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        floatAnimLeft.start();

        float baseRightY = 6 * density;
        floatAnimRight = ValueAnimator.ofFloat(baseRightY + 5 * density, baseRightY - 5 * density);
        floatAnimRight.setDuration(2800);
        floatAnimRight.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimRight.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimRight.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimRight.addUpdateListener(animation -> {
            if (cardPosterRight != null) {
                cardPosterRight.setTranslationY((float) animation.getAnimatedValue());
            }
        });
        floatAnimRight.start();

        startPosterCarousel();
    }

    private void stopFloatingAnimationStep1() {
        if (floatAnimCenter != null) {
            floatAnimCenter.cancel();
            floatAnimCenter = null;
        }
        if (floatAnimLeft != null) {
            floatAnimLeft.cancel();
            floatAnimLeft = null;
        }
        if (floatAnimRight != null) {
            floatAnimRight.cancel();
            floatAnimRight = null;
        }
    }

    private void startPosterCarousel() {
        stopPosterCarousel();
        carouselRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentStep != 1 || isFinishing() || isDestroyed()) return;

                int nextIndex = (currentPosterIndex + 1) % step1PosterDrawables.length;
                applyStep1Category(nextIndex, true);

                carouselHandler.postDelayed(this, 3000);
            }
        };
        carouselHandler.postDelayed(carouselRunnable, 3000);
    }

    private void stopPosterCarousel() {
        if (carouselRunnable != null) {
            carouselHandler.removeCallbacks(carouselRunnable);
            carouselRunnable = null;
        }
    }

    private void stopStep1Animations() {
        stopFloatingAnimationStep1();
        stopPosterCarousel();
    }

    // ==========================================
    // STEP 2 ANIMATIONS: 2-Level Blue Toolbar, Hand Pointer & Live Poster Customization
    // ==========================================
    private void setupStep2InteractiveListeners() {
        // Main Tool Tabs
        if (tabToolBg != null) {
            tabToolBg.setOnClickListener(v -> {
                switchToolTab("bg");
                selectSubBg(2);
            });
        }
        if (tabToolText != null) {
            tabToolText.setOnClickListener(v -> {
                switchToolTab("text");
                selectSubText(3);
            });
        }
        if (tabToolStickers != null) {
            tabToolStickers.setOnClickListener(v -> {
                switchToolTab("stickers");
                selectSubSticker(1);
            });
        }
        if (tabToolGlow != null) {
            tabToolGlow.setOnClickListener(v -> {
                switchToolTab("glow");
                selectSubGlow(2);
            });
        }

        // Sub-options: Text Headlines
        if (subText1 != null) subText1.setOnClickListener(v -> selectSubText(1));
        if (subText2 != null) subText2.setOnClickListener(v -> selectSubText(2));
        if (subText3 != null) subText3.setOnClickListener(v -> selectSubText(3));

        // Sub-options: Stickers
        if (subSticker1 != null) subSticker1.setOnClickListener(v -> selectSubSticker(1));
        if (subSticker2 != null) subSticker2.setOnClickListener(v -> selectSubSticker(2));
        if (subSticker3 != null) subSticker3.setOnClickListener(v -> selectSubSticker(3));

        // Sub-options: BG Colors
        if (subBg1 != null) subBg1.setOnClickListener(v -> selectSubBg(1));
        if (subBg2 != null) subBg2.setOnClickListener(v -> selectSubBg(2));
        if (subBg3 != null) subBg3.setOnClickListener(v -> selectSubBg(3));

        // Sub-options: Glow / Effects
        if (subGlow1 != null) subGlow1.setOnClickListener(v -> selectSubGlow(1));
        if (subGlow2 != null) subGlow2.setOnClickListener(v -> selectSubGlow(2));
        if (subGlow3 != null) subGlow3.setOnClickListener(v -> selectSubGlow(3));
    }

    private void switchToolTab(String tool) {
        // Reset all tabs to inactive
        setTabStyle(tabToolBg, false);
        setTabStyle(tabToolText, false);
        setTabStyle(tabToolStickers, false);
        setTabStyle(tabToolGlow, false);

        // Hide all sub-trays
        if (traySubBg != null) traySubBg.setVisibility(View.GONE);
        if (traySubText != null) traySubText.setVisibility(View.GONE);
        if (traySubStickers != null) traySubStickers.setVisibility(View.GONE);
        if (traySubGlow != null) traySubGlow.setVisibility(View.GONE);

        if ("bg".equals(tool)) {
            setTabStyle(tabToolBg, true);
            if (traySubBg != null) traySubBg.setVisibility(View.VISIBLE);
        } else if ("text".equals(tool)) {
            setTabStyle(tabToolText, true);
            if (traySubText != null) traySubText.setVisibility(View.VISIBLE);
        } else if ("stickers".equals(tool)) {
            setTabStyle(tabToolStickers, true);
            if (traySubStickers != null) traySubStickers.setVisibility(View.VISIBLE);
        } else if ("glow".equals(tool)) {
            setTabStyle(tabToolGlow, true);
            if (traySubGlow != null) traySubGlow.setVisibility(View.VISIBLE);
        }

        if (layoutSubOptionsTray != null) {
            layoutSubOptionsTray.setScaleX(0.92f);
            layoutSubOptionsTray.setScaleY(0.92f);
            layoutSubOptionsTray.animate().scaleX(1f).scaleY(1f).setDuration(180)
                    .setInterpolator(new OvershootInterpolator(1.4f)).start();
        }
    }

    private void setTabStyle(TextView tab, boolean active) {
        if (tab == null) return;
        if (active) {
            tab.setBackgroundResource(R.drawable.bg_tool_pill_active);
            tab.setTextColor(Color.WHITE);
        } else {
            tab.setBackgroundResource(R.drawable.bg_tool_pill_inactive);
            tab.setTextColor(Color.parseColor("#4B5563"));
        }
    }

    private void setSubOptionPill(TextView pill, boolean selected) {
        if (pill == null) return;
        if (selected) {
            pill.setBackgroundResource(R.drawable.bg_sub_option_active);
            pill.setTextColor(Color.WHITE);
            pill.animate().scaleX(1.08f).scaleY(1.08f).setDuration(120)
                    .withEndAction(() -> pill.animate().scaleX(1f).scaleY(1f).setDuration(120).start()).start();
        } else {
            pill.setBackgroundResource(R.drawable.bg_sub_option_inactive);
            pill.setTextColor(Color.parseColor("#374151"));
        }
    }

    private void selectSubText(int index) {
        setSubOptionPill(subText1, index == 1);
        setSubOptionPill(subText2, index == 2);
        setSubOptionPill(subText3, index == 3);

        if (selectionBox != null) {
            selectionBox.setAlpha(1f);
        }

        if (tvPosterHeading != null) {
            tvPosterHeading.animate().scaleX(0.6f).scaleY(0.6f).alpha(0.3f).setDuration(140)
                    .withEndAction(() -> {
                        if (index == 1) {
                            tvPosterHeading.setText("FESTIVE\nCOLLECTION");
                            tvPosterHeading.setTextColor(Color.parseColor("#831843"));
                        } else if (index == 2) {
                            tvPosterHeading.setText("MEGA\nSALE");
                            tvPosterHeading.setTextColor(Color.parseColor("#B45309"));
                        } else {
                            tvPosterHeading.setText("FASHION\nSALE");
                            tvPosterHeading.setTextColor(Color.parseColor("#2D150B"));
                        }
                        tvPosterHeading.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(220)
                                .setInterpolator(new OvershootInterpolator(1.8f)).start();
                    }).start();
        }
        animateCalloutBadgePop(badgeCalloutFonts, 100);
    }

    private void selectSubSticker(int index) {
        setSubOptionPill(subSticker1, index == 1);
        setSubOptionPill(subSticker2, index == 2);
        setSubOptionPill(subSticker3, index == 3);

        if (selectionBox != null) {
            selectionBox.animate().alpha(0f).setDuration(180).start();
        }

        if (ivPosterSticker != null) {
            if (index == 1) {
                ivPosterSticker.setText("🔥 50% OFF");
                ivPosterSticker.setBackgroundResource(R.drawable.bg_sticker_sale);
            } else if (index == 2) {
                ivPosterSticker.setText("🌟 NEW");
                ivPosterSticker.setBackgroundResource(R.drawable.bg_floating_badge_orange);
            } else {
                ivPosterSticker.setText("🎉 SPECIAL");
                ivPosterSticker.setBackgroundResource(R.drawable.bg_floating_badge_purple);
            }
            ivPosterSticker.setAlpha(1f);
            ivPosterSticker.setScaleX(0.2f);
            ivPosterSticker.setScaleY(0.2f);
            ivPosterSticker.animate().scaleX(1f).scaleY(1f).setDuration(300)
                    .setInterpolator(new OvershootInterpolator(2.0f)).start();
        }
        animateCalloutBadgePop(badgeCalloutStickers, 100);
        if (index == 1) {
            animateCalloutBadgePop(badgeCalloutDiscount, 220);
        }
    }

    private void selectSubBg(int index) {
        setSubOptionPill(subBg1, index == 1);
        setSubOptionPill(subBg2, index == 2);
        setSubOptionPill(subBg3, index == 3);

        if (viewCanvasBgOverlay != null) {
            if (index == 1) {
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4DF59E0B")); // Gold tint
            } else if (index == 2) {
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4D0066FF")); // Blue Glam tint
            } else {
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4DEC4899")); // Rose tint
            }
            viewCanvasBgOverlay.setAlpha(0f);
            viewCanvasBgOverlay.animate().alpha(1f).setDuration(300).start();
        }
        animateCalloutBadgePop(badgeCalloutBg, 100);
    }

    private void selectSubGlow(int index) {
        setSubOptionPill(subGlow1, index == 1);
        setSubOptionPill(subGlow2, index == 2);
        setSubOptionPill(subGlow3, index == 3);

        if (ivEditPoster != null) {
            if (index == 1) {
                ColorMatrix cm = new ColorMatrix();
                cm.setScale(1.08f, 1.05f, 1.15f, 1f);
                ivEditPoster.setColorFilter(new ColorMatrixColorFilter(cm));
            } else if (index == 2) {
                ColorMatrix cm = new ColorMatrix();
                cm.setScale(1.15f, 1.08f, 0.95f, 1f);
                ivEditPoster.setColorFilter(new ColorMatrixColorFilter(cm));
            } else {
                ivEditPoster.clearColorFilter();
            }
        }
        if (glowBgStep2 != null) {
            glowBgStep2.animate().scaleX(1.15f).scaleY(1.15f).alpha(0.9f).setDuration(240)
                    .withEndAction(() -> glowBgStep2.animate().scaleX(1f).scaleY(1f).alpha(0.6f).setDuration(240).start())
                    .start();
        }
        animateCalloutBadgePop(badgeCalloutGlow, 100);
    }

    private void animateStep2Entrance() {
        if (cardEditPoster == null) return;

        float density = getResources().getDisplayMetrics().density;
        stopStep2Animations();
        resetStep2CanvasElements();

        // Initial Card Setup
        cardEditPoster.setScaleX(0.85f);
        cardEditPoster.setScaleY(0.85f);
        cardEditPoster.setAlpha(0f);
        cardEditPoster.setTranslationY(16 * density);

        // Reset 6 Badges
        resetCalloutBadge(badgeCalloutBg);
        resetCalloutBadge(badgeCalloutDiscount);
        resetCalloutBadge(badgeCalloutFonts);
        resetCalloutBadge(badgeCalloutStickers);
        resetCalloutBadge(badgeCalloutShubh);
        resetCalloutBadge(badgeCalloutGlow);

        // Reset Toolbars
        if (layoutSubOptionsTray != null) {
            layoutSubOptionsTray.setAlpha(0f);
            layoutSubOptionsTray.setTranslationY(12 * density);
        }
        if (layoutMainToolsBar != null) {
            layoutMainToolsBar.setAlpha(0f);
            layoutMainToolsBar.setTranslationY(16 * density);
        }

        // 1. Poster Card Entrance
        cardEditPoster.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(520)
                .setInterpolator(new OvershootInterpolator(1.3f))
                .withEndAction(() -> {
                    startScreen2ContinuousFloating();
                    startStep2LiveEditingSimulation();
                })
                .start();

        // 2. Floating badges start hidden; they pop in sequentially as each tool is activated

        // 3. Animate Toolbars
        if (layoutMainToolsBar != null) {
            layoutMainToolsBar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(250)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }
        if (layoutSubOptionsTray != null) {
            layoutSubOptionsTray.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(320)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }
    }

    private void resetCalloutBadge(View badge) {
        if (badge != null) {
            badge.setAlpha(0f);
            badge.setScaleX(0.3f);
            badge.setScaleY(0.3f);
        }
    }

    private void animateCalloutBadgePop(View badge, long delay) {
        if (badge != null) {
            badge.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(delay)
                    .setDuration(420)
                    .setInterpolator(new OvershootInterpolator(1.6f))
                    .start();
        }
    }

    private void resetStep2CanvasElements() {
        if (tvPosterHeading != null) {
            tvPosterHeading.setText("NEW\nCOLLECTION");
            tvPosterHeading.setTextColor(Color.parseColor("#2D150B"));
            tvPosterHeading.setScaleX(1f);
            tvPosterHeading.setScaleY(1f);
            tvPosterHeading.setAlpha(1f);
        }
        if (selectionBox != null) {
            selectionBox.setAlpha(0f);
        }
        if (ivPosterSticker != null) {
            ivPosterSticker.setAlpha(0f);
            ivPosterSticker.setScaleX(0f);
            ivPosterSticker.setScaleY(0f);
        }
        if (viewCanvasBgOverlay != null) {
            viewCanvasBgOverlay.setAlpha(0f);
        }
        if (ivEditPoster != null) {
            ivEditPoster.clearColorFilter();
        }
        if (badgeFinalReveal != null) {
            badgeFinalReveal.setAlpha(0f);
            badgeFinalReveal.setScaleX(0f);
            badgeFinalReveal.setScaleY(0f);
        }
        resetCalloutBadge(badgeCalloutBg);
        resetCalloutBadge(badgeCalloutDiscount);
        resetCalloutBadge(badgeCalloutFonts);
        resetCalloutBadge(badgeCalloutStickers);
        resetCalloutBadge(badgeCalloutShubh);
        resetCalloutBadge(badgeCalloutGlow);
        switchToolTab("text");
        setSubOptionPill(subText1, false);
        setSubOptionPill(subText2, false);
        setSubOptionPill(subText3, false);
    }

    private void startScreen2ContinuousFloating() {
        if (isFinishing() || isDestroyed()) return;

        float density = getResources().getDisplayMetrics().density;
        stopFloatingAnimationStep2();

        floatAnimStep2 = ValueAnimator.ofFloat(0, -6 * density);
        floatAnimStep2.setDuration(2200);
        floatAnimStep2.setRepeatMode(ValueAnimator.REVERSE);
        floatAnimStep2.setRepeatCount(ValueAnimator.INFINITE);
        floatAnimStep2.setInterpolator(new AccelerateDecelerateInterpolator());
        floatAnimStep2.addUpdateListener(anim -> {
            if (cardEditPoster != null) {
                cardEditPoster.setTranslationY((float) anim.getAnimatedValue());
            }
        });
        floatAnimStep2.start();
    }

    // --- Automated Hand Pointer & Editing Simulation ---
    private void startStep2LiveEditingSimulation() {
        stopStep2Timeline();
        setupStep2InteractiveListeners();

        // 1. Tool Text: Select "Fashion Sale" headline (pops text selection box + Fonts badge)
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            switchToolTab("text");
            selectSubText(3);
        }, 600);

        // 2. Tool Stickers: Select "50% OFF" sticker (drops sticker on poster + pops 50% OFF & Stickers badge)
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            switchToolTab("stickers");
            selectSubSticker(1);
        }, 2500);

        // 3. Tool BG: Select Blue Glam background tint (pops Change BG badge)
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            switchToolTab("bg");
            selectSubBg(2);
        }, 4400);

        // 4. Tool Glow: Select Magic Glow effect (pops Magic Glow badge)
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            switchToolTab("glow");
            selectSubGlow(2);
        }, 6200);

        // 5. Final reveal: hide selection handles, pop "✨ Edited" badge
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            if (selectionBox != null) {
                selectionBox.animate().alpha(0f).setDuration(250).start();
            }
            if (badgeFinalReveal != null) {
                badgeFinalReveal.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(350)
                        .setInterpolator(new OvershootInterpolator(1.8f)).start();
            }
        }, 7800);

        // 6. Reset and loop smoothly
        step2TimelineHandler.postDelayed(() -> {
            if (currentStep != 2) return;
            resetStep2CanvasElements();
            startStep2LiveEditingSimulation();
        }, 10000);
    }

    private void stopFloatingAnimationStep2() {
        if (floatAnimStep2 != null) {
            floatAnimStep2.cancel();
            floatAnimStep2 = null;
        }
    }

    private void stopStep2Timeline() {
        if (step2TimelineHandler != null) {
            step2TimelineHandler.removeCallbacksAndMessages(null);
        }
    }

    private void stopStep2Animations() {
        stopFloatingAnimationStep2();
        stopStep2Timeline();
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

    // 1. Social node & bubble bounce
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

    // Phase 1: Screen Entrance
    private void animateStep3Entrance() {
        if (cardSharePoster == null) return;

        float density = getResources().getDisplayMetrics().density;

        // Stop any running animations
        stopStep3Animations();

        // 1. Reset initial states
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

        // --- Animate In ---
        // 1. Poster Card Zoom & Overshoot
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

        // 2. Badges Pop-up
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

        // 3. WhatsApp Slide-in (from left) + Bubble Pop
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

        // 4. Instagram Slide-in (from right, 120ms stagger) + Bubble Pop
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

        // 5. Sparkles Reveal
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

        // 6. Quick Share Bar Slide Up
        if (llQuickShareBar != null) {
            llQuickShareBar.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(380)
                    .setDuration(450)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }

        // 7. Pulse Continue Button
        startButtonPulseAnimation();
    }

    // --- Phase 2: Continuous Floating & Idle ---
    private void startScreen3ContinuousFloating() {
        if (isFinishing() || isDestroyed()) return;

        float density = getResources().getDisplayMetrics().density;
        stopFloatingAnimationStep3();

        // 1. Poster Levitation
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

        // 2. WhatsApp Node Floating (-5dp)
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

        // 3. Instagram Node Floating (+5dp Opposite Asymmetric Direction)
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

        // 4. Sparkle Gold 360° Rotation
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

        // 5. Sparkle Purple Counter-Rotation 360°
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

    // --- Phase 3: Auto Share Cycle (Loop every 2.4s) ---
    private void startScreen3ShareCycle() {
        stopScreen3ShareCycle();
        shareCycleRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentStep != 3 || isFinishing() || isDestroyed()) return;
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

    // ==========================================
    // UTILITY & LIFECYCLE
    // ==========================================
    private void resetBadge(View badge) {
        if (badge != null) {
            badge.setAlpha(0f);
            badge.setScaleX(0.4f);
            badge.setScaleY(0.4f);
        }
    }

    private void animateBadgePop(View badge, long delay) {
        if (badge != null) {
            badge.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(delay)
                    .setDuration(400)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .start();
        }
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
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (isTransitioning) return;
        if (currentStep == 3) {
            goToStep(2);
        } else if (currentStep == 2) {
            goToStep(1);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentStep == 2) {
            stopStep2Animations();
        } else if (currentStep == 3) {
            stopStep3Animations();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentStep == 2) {
            startScreen2ContinuousFloating();
            startStep2LiveEditingSimulation();
        } else if (currentStep == 3) {
            startScreen3ContinuousFloating();
            startScreen3ShareCycle();
            startButtonPulseAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStep1Animations();
        stopStep2Animations();
        stopStep3Animations();
    }

    private void updateNativeAdForStep(int step) {
        if (nativeAdContainer == null) return;
        boolean isNativeEnabled = preferenceClass.isOnboardingNativeAdEnabled(step);
        if (!isNativeEnabled) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            nativeAdContainer.setTag(null);
        } else {
            int customHeightDp = preferenceClass.getOnboardingNativeAdHeight(step);
            if (customHeightDp > 0) {
                int heightPx = (int) android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, customHeightDp, getResources().getDisplayMetrics());
                android.view.ViewGroup.LayoutParams lp = nativeAdContainer.getLayoutParams();
                if (lp != null) {
                    lp.height = heightPx;
                    nativeAdContainer.setLayoutParams(lp);
                }
            }
            nativeAdContainer.setVisibility(View.VISIBLE);
            String customId = preferenceClass.getOnboardingNativeAdId(step);
            Leaflet_NativeAdUtil.loadOnboardingNativeAd(nativeAdContainer, this, customId);
        }
    }
}
