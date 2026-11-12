package com.online.flyer.design.postermaker.Leaflet_activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

public class Leaflet_OnboardingStep2Activity extends AppCompatActivity {

    private boolean isTransitioning = false;

    private ImageView btnBack;
    private RelativeLayout nativeAdContainer;
    private TextView btnContinue;

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

        setContentView(R.layout.leaflet_activity_onboarding_step2);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initViews();
        setupListeners();

        // Setup Ads
        String customInterId = preferenceClass.getOnboardingInterstitialAdId();
        interstitialAdManager = new Leaflet_InterstitialAdManager(this, customInterId);
        if (preferenceClass.isMasterOnboardingInterstitialEnabled() && preferenceClass.isOnboardingInterstitialEnabled(2)) {
            interstitialAdManager.fetchAdMobAd();
        }

        loadNativeAd();
        animateStep2Entrance();
    }

    private void initViews() {
        View rootLayout = findViewById(R.id.root_onboarding_step2);
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

        boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(2);
        if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
            interstitialAdManager.showInterstitialAd(Leaflet_OnboardingStep2Activity.this, this::navigateToStep3);
        } else {
            navigateToStep3();
        }
    }

    private void navigateToStep3() {
        Intent intent = new Intent(this, Leaflet_OnboardingStep3Activity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.leaflet_slide_in_right, R.anim.leaflet_slide_out_left);
    }

    @Override
    public void onBackPressed() {
        if (isTransitioning || Leaflet_ClickUtils.isFastDoubleClick(600)) return;
        super.onBackPressed();
        overridePendingTransition(R.anim.leaflet_slide_in_left, R.anim.leaflet_slide_out_right);
    }

    private void loadNativeAd() {
        if (nativeAdContainer == null) return;
        boolean isNativeEnabled = preferenceClass.isOnboardingNativeAdEnabled(2);
        if (!isNativeEnabled) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            nativeAdContainer.setTag(null);
        } else {
            int customHeightDp = preferenceClass.getOnboardingNativeAdHeight(2);
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
            String customId = preferenceClass.getOnboardingNativeAdId(2);
            Leaflet_NativeAdUtil.loadOnboardingNativeAd(nativeAdContainer, this, customId);
        }
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
        setTabStyle(tabToolBg, false);
        setTabStyle(tabToolText, false);
        setTabStyle(tabToolStickers, false);
        setTabStyle(tabToolGlow, false);

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
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4DF59E0B"));
            } else if (index == 2) {
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4D0066FF"));
            } else {
                viewCanvasBgOverlay.setBackgroundColor(Color.parseColor("#4DEC4899"));
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

        cardEditPoster.setScaleX(0.85f);
        cardEditPoster.setScaleY(0.85f);
        cardEditPoster.setAlpha(0f);
        cardEditPoster.setTranslationY(16 * density);

        resetCalloutBadge(badgeCalloutBg);
        resetCalloutBadge(badgeCalloutDiscount);
        resetCalloutBadge(badgeCalloutFonts);
        resetCalloutBadge(badgeCalloutStickers);
        resetCalloutBadge(badgeCalloutShubh);
        resetCalloutBadge(badgeCalloutGlow);

        if (layoutSubOptionsTray != null) {
            layoutSubOptionsTray.setAlpha(0f);
            layoutSubOptionsTray.setTranslationY(12 * density);
        }
        if (layoutMainToolsBar != null) {
            layoutMainToolsBar.setAlpha(0f);
            layoutMainToolsBar.setTranslationY(16 * density);
        }

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

    private void startStep2LiveEditingSimulation() {
        stopStep2Timeline();
        setupStep2InteractiveListeners();

        // 1. Tool Text: Select "Fashion Sale" headline
        step2TimelineHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            switchToolTab("text");
            selectSubText(3);
        }, 600);

        // 2. Tool Stickers: Select "50% OFF" sticker
        step2TimelineHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            switchToolTab("stickers");
            selectSubSticker(1);
        }, 2500);

        // 3. Tool BG: Select Blue Glam background tint
        step2TimelineHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            switchToolTab("bg");
            selectSubBg(2);
        }, 4400);

        // 4. Tool Glow: Select Magic Glow effect
        step2TimelineHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            switchToolTab("glow");
            selectSubGlow(2);
        }, 6200);

        // 5. Final reveal: hide selection handles, pop "✨ Edited" badge
        step2TimelineHandler.postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
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
            if (isFinishing() || isDestroyed()) return;
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

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "onboarding_step2");
        isTransitioning = false;
        if (btnContinue != null) btnContinue.setEnabled(true);
        startScreen2ContinuousFloating();
        startStep2LiveEditingSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopStep2Animations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStep2Animations();
    }
}
