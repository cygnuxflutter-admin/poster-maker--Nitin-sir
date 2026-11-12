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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.List;
import java.util.ArrayList;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

public class Leaflet_OnboardingStep1Activity extends AppCompatActivity {

    private boolean isTransitioning = false;

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

        setContentView(R.layout.leaflet_activity_onboarding_step1);

        preferenceClass = new Leaflet_PreferenceClass(this);

        initViews();
        setupListeners();

        // Setup Ads
        String customInterId = preferenceClass.getOnboardingInterstitialAdId();
        interstitialAdManager = new Leaflet_InterstitialAdManager(this, customInterId);
        if (preferenceClass.isMasterOnboardingInterstitialEnabled() && preferenceClass.isOnboardingInterstitialEnabled(1)) {
            interstitialAdManager.fetchAdMobAd();
        }

        loadNativeAd();
        animateStep1Entrance();
        requestAllAppPermissions();
    }

    private void initViews() {
        View rootLayout = findViewById(R.id.root_onboarding_step1);
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
        nativeAdContainer = findViewById(R.id.native_ad_container);
        btnContinue = findViewById(R.id.btn_continue);

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
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> onContinueClicked());
    }

    private void onContinueClicked() {
        if (isTransitioning || Leaflet_ClickUtils.isFastDoubleClick(900)) return;
        isTransitioning = true;
        if (btnContinue != null) btnContinue.setEnabled(false);

        boolean showInter = preferenceClass.isOnboardingInterstitialEnabled(1);
        if (showInter && interstitialAdManager != null && interstitialAdManager.isAdmobAdAvailable()) {
            interstitialAdManager.showInterstitialAd(Leaflet_OnboardingStep1Activity.this, this::navigateToStep2);
        } else {
            navigateToStep2();
        }
    }

    private void navigateToStep2() {
        Intent intent = new Intent(this, Leaflet_OnboardingStep2Activity.class);
        if (getIntent() != null && getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.leaflet_slide_in_right, R.anim.leaflet_slide_out_left);
    }

    private void loadNativeAd() {
        if (nativeAdContainer == null) return;
        boolean isNativeEnabled = preferenceClass.isOnboardingNativeAdEnabled(1);
        if (!isNativeEnabled) {
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            nativeAdContainer.setTag(null);
        } else {
            int customHeightDp = preferenceClass.getOnboardingNativeAdHeight(1);
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
            String customId = preferenceClass.getOnboardingNativeAdId(1);
            Leaflet_NativeAdUtil.loadOnboardingNativeAd(nativeAdContainer, this, customId);
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
        if (isFinishing() || isDestroyed()) return;
        stopPosterCarousel();
        applyStep1Category(index, true);
        startPosterCarousel();
    }

    private void applyStep1Category(int targetIndex, boolean isAnimated) {
        if (isFinishing() || isDestroyed()) return;

        currentPosterIndex = targetIndex;
        float density = getResources().getDisplayMetrics().density;

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

        if (tvTitleStep1 != null && tvSubtitleStep1 != null) {
            if (isAnimated) {
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
                if (isFinishing() || isDestroyed()) return;

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

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "onboarding_step1");
        isTransitioning = false;
        if (btnContinue != null) btnContinue.setEnabled(true);
        startFloatingAnimationStep1();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopStep1Animations();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStep1Animations();
    }

    private void requestAllAppPermissions() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isFinishing() || isDestroyed()) return;
            try {
                com.onesignal.OneSignal.promptForPushNotifications();
            } catch (Exception ignored) {}

            List<String> permissionsNeeded = new ArrayList<>();

            // 1. Storage & Photos
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(android.Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }

            // 2. Camera
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(android.Manifest.permission.CAMERA);
            }

            // 3. Notifications (Android 13+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(android.Manifest.permission.POST_NOTIFICATIONS);
                }
            }

            if (!permissionsNeeded.isEmpty()) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        permissionsNeeded.toArray(new String[0]), 101);
            }
        }, 600);
    }

}
