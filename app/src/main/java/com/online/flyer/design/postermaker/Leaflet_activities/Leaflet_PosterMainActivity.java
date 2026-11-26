package com.online.flyer.design.postermaker.Leaflet_activities;

import static com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil.loadNativeAd;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextSwitcher;
import android.util.TypedValue;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_HeroBannerAdapter;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_HomeCategoryAdapter;
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_PopularPosterAdapter;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HeroBanner;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HomeCategory;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_PopularPoster;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AdBlockerDetector;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ShareUtils;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;

public class Leaflet_PosterMainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)

    //    private CarouselView carouselView;
    private Leaflet_PreferenceClass preferenceClass;
    private int activityIndex = 0;
//    MailER_PrefManager prefManager;

//    MailER_UserViewModel userViewModel;
//    MailER_DialogMsg dialogMsg;

    final private int REQUEST_CAMERA_AND_STORAGE_PERMISSION = 100;

    // Hero Banner
    private ViewPager2 heroBannerViewPager;
    private LinearLayout bannerIndicatorLayout;
    private Leaflet_HeroBannerAdapter heroBannerAdapter;
    private List<Leaflet_HeroBanner> heroBannerList = new ArrayList<>();
    private Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_DELAY = 3000; // 3 seconds

    // Animated Search Bar
    private TextSwitcher tsSearchHints;
    private Handler searchHintHandler;
    private Runnable searchHintRunnable;
    private int currentSearchHintIndex = 0;
    private final String[] searchHints = {
            "Festival Posters \uD83E\uDE94",
            "Birthday Wishes \uD83C\uDF82",
            "Business Flyers \uD83D\uDCBC",
            "Sale & Offers \uD83D\uDD25",
            "Social Media Posts \uD83D\uDCF1"
    };

    // Home Browse by Category (Backend-driven with fallback defaults)
    private RecyclerView homeCategoriesRv;
    private Leaflet_HomeCategoryAdapter homeCategoryAdapter;
    private final List<Leaflet_HomeCategory> homeCategoryList = new ArrayList<>();
    private static final String KEY_CACHE_HOME_CATEGORIES = "home_categories_cache_json";

    // Popular Posters
    private RecyclerView popularPostersRv;
    private Leaflet_PopularPosterAdapter popularPosterAdapter;
    private List<Leaflet_PopularPoster> popularPosterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.parseColor("#F8F9FE"));
        }
        setContentView(R.layout.leaflet_activity_mainposter);

// 3-tier RelativeLayout automatically manages dynamic spacing when Banner Ad loads/fails

        preferenceClass = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(this);
        android.widget.RelativeLayout bannerContainer = findViewById(R.id.banner_ad_container_home);
        if (preferenceClass.isBannerAdEnabled("home")) {
            if (bannerContainer != null) {
                com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(this, bannerContainer, "home");
            }
        } else {
            if (bannerContainer != null) {
                bannerContainer.removeAllViews();
                bannerContainer.setVisibility(android.view.View.GONE);
            }
        }

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            });
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("35b6d659-eeda-4729-b2c0-ebef7ac69e3e");
        // OneSignal.promptForPushNotifications(); // Moved to OnboardingStep1

        // FCM Global Topic Subscription
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FCM", "Subscribed to all_users topic from MainActivity");
                        }
                    });
        } catch (Exception e) {
            Log.e("FCM", "Error subscribing to all_users topic", e);
        }

        // All initial permissions (Storage, Camera, Notifications) are requested on Onboarding Step 1

        handleNotificationIntent(getIntent());

        android.view.View btnNotification = findViewById(R.id.btn_notification);
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
                startActivity(new Intent(Leaflet_PosterMainActivity.this, Leaflet_NotificationActivity.class));
             });

        }

        // checkAccess() removed from onCreate so home screen launches cleanly without permission dialogs

//        prefManager = new MailER_PrefManager(this);
//        dialogMsg = new MailER_DialogMsg(this, false);
//        userViewModel = new ViewModelProvider(this).get(MailER_UserViewModel.class);


        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        preferenceClass = new Leaflet_PreferenceClass(this);

        // Preload templates & backgrounds in the background so opening them is instantaneous (0s loader)
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().preloadTemplatesSilently(this, preferenceClass);
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().preloadBackgroundsSilently(this, preferenceClass);

        findByID();
        setupAnimatedSearchBar();

        int homeVisits = preferenceClass.incrementHomeVisitCount();
        if ((homeVisits == 3 || homeVisits == 4) && !preferenceClass.isRateSubmitted() && preferenceClass.isRatingDialogEnabled()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_RatingManager.checkAndShowRatingDialog(Leaflet_PosterMainActivity.this, "home");
                }
            }, 1200);
        }

        findViewById(R.id.lay_poster).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logCategorySelected(this, "Create Poster", "0", "quick_action");
            activityIndex = 1;
            if (!checkPermission()) {
                try {
                    requestPermission();

//                    if (Build.MANUFACTURER.equals("TECNO MOBILE LIMITED") && Build.BRAND.equals("TECNO") ||
//                            Build.BRAND.equals("samsung") || Build.BRAND.equals("OPPO") ||
//                            Build.BRAND.equals("vivo")) {
                    // Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
//                        Log.e("#brand", Build.BRAND);
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }

                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            } else {
                nextActivity(Leaflet_BackgroundSelectionActivity.class);
            }
//            openActivity(MailER_BackgroundSelectionActivity.class);
        });
        findViewById(R.id.lay_template).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logCategorySelected(this, "Readymade Poster", "0", "quick_action");
            activityIndex = 2;
            if (!checkPermission()) {
                try {
                    requestPermission();

//                    if (Build.MANUFACTURER.equals("TECNO MOBILE LIMITED") && Build.BRAND.equals("TECNO") ||
//                            Build.BRAND.equals("samsung") || Build.BRAND.equals("OPPO") ||
//                            Build.BRAND.equals("vivo")) {
                    // Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
//                        Log.e("#brand", Build.BRAND);
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }

                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            } else {
//                nextActivity(MailER_HomeActivity.class);
                nextActivity(Leaflet_TemplateSelectionActivity.class);
            }
//            openActivity(MailER_HomeActivity.class);
        });

        // Quick Action Cards Spring Touch Animation
        applyTouchBounceEffect(findViewById(R.id.lay_poster));
        applyTouchBounceEffect(findViewById(R.id.lay_template));
        applyTouchBounceEffect(findViewById(R.id.lay_creation));
        applyTouchBounceEffect(findViewById(R.id.lay_design));
        applyTouchBounceEffect(findViewById(R.id.btn_notification));

        findViewById(R.id.lay_design).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_RatingManager.showRatingDialog(Leaflet_PosterMainActivity.this, "home_rate_us");
        });

        findViewById(R.id.lay_creation).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "my_creation", "quick_action");
            activityIndex = 4;
            if (!checkPermission()) {
                try {
                    requestPermission();
                    // Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            } else {
                nextActivity(Leaflet_MyCreationActivity.class);
            }
        });

        // Setup Browse by Category (Backend-driven with Default Fallback), Hero Banner, Popular Posters
        setupHomeCategories();
        animateCategoryEntrance();
        setupHeroBanner();
        setupPopularPosters();
    }


    
    private void startArrowNudgeAnimation(View arrow) {
        if (arrow == null) return;
        float density = getResources().getDisplayMetrics().density;
        float nudgeDistance = 5f * density;
        android.animation.ObjectAnimator nudge = android.animation.ObjectAnimator.ofFloat(arrow, "translationX", 0f, nudgeDistance, 0f);
        nudge.setDuration(1000);
        nudge.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        nudge.setRepeatCount(android.animation.ValueAnimator.INFINITE);
        nudge.setRepeatMode(android.animation.ValueAnimator.RESTART);
        nudge.start();
    }

    private void applyTouchBounceEffect(View view) {
        if (view == null) return;
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).setInterpolator(new android.view.animation.DecelerateInterpolator()).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).setInterpolator(new android.view.animation.OvershootInterpolator(2.5f)).start();
                    break;
            }
            return false;
        });
    }

    private void animateCategoryEntrance() {
        if (homeCategoriesRv == null) return;
        homeCategoriesRv.setAlpha(0f);
        homeCategoriesRv.setTranslationX(40f);
        homeCategoriesRv.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(350)
                .setInterpolator(new android.view.animation.DecelerateInterpolator(1.5f))
                .start();
    }

    // ===================== BROWSE BY CATEGORY (BACKEND + FALLBACK) =====================
    private void setupHomeCategories() {
        homeCategoriesRv = findViewById(R.id.home_categories_rv);
        if (homeCategoriesRv == null) return;

        homeCategoriesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        homeCategoryList.clear();
        homeCategoryList.addAll(buildDefaultHomeCategories());

        homeCategoryAdapter = new Leaflet_HomeCategoryAdapter(this, homeCategoryList);
        homeCategoryAdapter.setOnCategoryClickListener((category, position) -> handleHomeCategoryClick(category));
        homeCategoriesRv.setAdapter(homeCategoryAdapter);

        loadHomeCategoriesFromApi();
    }

    private List<Leaflet_HomeCategory> buildDefaultHomeCategories() {
        List<Leaflet_HomeCategory> defaults = new ArrayList<>();
        defaults.add(new Leaflet_HomeCategory("1", "Festival", "", "", "", "template_category", "", "Festival", "", R.drawable.bg_cat_tile_festival, R.drawable.ic_cat_diya_festival, false));
        defaults.add(new Leaflet_HomeCategory("2", "Wedding", "", "", "", "template_category", "", "Wedding", "", R.drawable.bg_cat_tile_wedding, R.drawable.ic_cat_rings_wedding, false));
        defaults.add(new Leaflet_HomeCategory("3", "Sale", "", "", "", "template_category", "", "Sale", "", R.drawable.bg_cat_tile_sale, R.drawable.ic_cat_tag_sale, false));
        defaults.add(new Leaflet_HomeCategory("4", "Business", "", "", "", "template_category", "", "Business", "", R.drawable.bg_cat_tile_business, R.drawable.ic_cat_bag_business, false));
        defaults.add(new Leaflet_HomeCategory("5", "Birthday", "", "", "", "template_category", "", "Birthday", "", R.drawable.bg_cat_tile_birthday, R.drawable.ic_cat_cake_birthday, false));
        defaults.add(new Leaflet_HomeCategory("6", "Food", "", "", "", "template_category", "", "Food", "", R.drawable.bg_cat_tile_food, R.drawable.ic_cat_burger_food, false));
        defaults.add(new Leaflet_HomeCategory("7", "Education", "", "", "", "template_category", "", "Education", "", R.drawable.bg_cat_tile_education, R.drawable.ic_cat_cap_education, false));
        defaults.add(new Leaflet_HomeCategory("8", "Party", "", "", "", "template_category", "", "Party", "", R.drawable.bg_cat_tile_event, R.drawable.ic_cat_party_event, false));
        defaults.add(createMoreHomeCategoryTile());
        return defaults;
    }

    private Leaflet_HomeCategory createMoreHomeCategoryTile() {
        return new Leaflet_HomeCategory("more", "More", "", "", "", "all_templates", "", "", "", R.drawable.bg_cat_tile_more, R.drawable.ic_cat_more_grid, true);
    }

    private void loadHomeCategoriesFromApi() {
        String cachedJson = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance()
                .getCache(this, KEY_CACHE_HOME_CATEGORIES);
        if (cachedJson != null && !cachedJson.trim().isEmpty()) {
            parseAndDisplayHomeCategories(cachedJson);
        }
        fetchHomeCategoriesFromApi();
    }

    private void parseAndDisplayHomeCategories(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (!jsonObject.has("data")) return;
            JSONArray dataArray = jsonObject.optJSONArray("data");
            if (dataArray == null || dataArray.length() == 0) return;

            List<Leaflet_HomeCategory> parsed = new ArrayList<>();
            boolean hasMoreTile = false;
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject obj = dataArray.optJSONObject(i);
                if (obj == null) continue;
                if (obj.has("is_active") && obj.optInt("is_active", 1) == 0) continue;

                String id = obj.optString("id", String.valueOf(i + 1));
                String name = obj.optString("name", obj.optString("cat_name", "")).trim();
                if (name.isEmpty()) continue;

                String iconUrl = obj.optString("icon_url", "").trim();
                String bgColor = obj.optString("bg_color", "").trim();
                String borderColor = obj.optString("border_color", "").trim();
                String actionType = obj.optString("action_type", "template_category").trim();
                String catId = obj.optString("cat_id", "").trim();
                String catName = obj.optString("cat_name", name).trim();
                String actionUrl = obj.optString("action_url", "").trim();

                boolean isMore = "all_templates".equalsIgnoreCase(actionType) && "more".equalsIgnoreCase(name);
                if (isMore) hasMoreTile = true;

                int localBg = resolveDefaultBgRes(name);
                int localIcon = resolveDefaultIconRes(name);

                parsed.add(new Leaflet_HomeCategory(id, name, iconUrl, bgColor, borderColor,
                        actionType, catId, catName, actionUrl, localBg, localIcon, isMore));
            }

            if (parsed.isEmpty()) return;
            if (!hasMoreTile) {
                parsed.add(createMoreHomeCategoryTile());
            }

            if (homeCategoryAdapter != null) {
                homeCategoryAdapter.updateData(parsed);
            }
            for (Leaflet_HomeCategory cat : parsed) {
                if (cat.getIconUrl() != null && !cat.getIconUrl().isEmpty()) {
                    try {
                        com.bumptech.glide.Glide.with(getApplicationContext())
                                .load(cat.getIconUrl())
                                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                                .preload();
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            android.util.Log.e("HomeCategories", "Error parsing home categories: " + e.getMessage());
        }
    }

    private void fetchHomeCategoriesFromApi() {
        String requestUrl = "https://cygnux.in/postermaker/api/v1/poster/home_categories";
        String key = preferenceClass.getDataType("field_0");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                if (response != null && response.contains("\"data\"")) {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance()
                            .saveCache(this, KEY_CACHE_HOME_CATEGORIES, response);
                    parseAndDisplayHomeCategories(response);
                }
            } catch (Exception e) {
                android.util.Log.e("HomeCategories", "Parse exception: " + e.getMessage());
            }
        }, error -> android.util.Log.d("HomeCategories", "Using default/cached categories (" + error.getMessage() + ")")) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("device", "1");
                if (key != null) postMap.put("key", key);
                postMap.put("app_id", "2");
                return postMap;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(20000, 1, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void handleHomeCategoryClick(Leaflet_HomeCategory category) {
        if (category == null || com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick()) {
            return;
        }

        if (category.isMoreTile()) {
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("show_all_categories", true);
                startActivity(intent);
            });
            return;
        }

        String actionType = category.getActionType() != null ? category.getActionType().trim().toLowerCase() : "template_category";
        String catId = category.getCatId() != null ? category.getCatId().trim() : "";
        String catName = (category.getCatName() != null && !category.getCatName().trim().isEmpty())
                ? category.getCatName().trim()
                : (category.getName() != null ? category.getName().trim() : "");
        String actionUrl = category.getActionUrl() != null ? category.getActionUrl().trim() : "";

        // Support hero-style "category/<id>" actionUrl fallback
        if (catId.isEmpty() && actionUrl.startsWith("category/")) {
            catId = actionUrl.replace("category/", "").trim();
        }

        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logCategorySelected(
                this, catName, catId, "home_category");

        final String finalCatId = catId;
        final String finalCatName = catName;

        if ("url".equals(actionType) || actionUrl.startsWith("http://") || actionUrl.startsWith("https://")) {
            if (!actionUrl.isEmpty()) {
                try {
                    com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.setExternalIntentActive(true);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(actionUrl)));
                } catch (Exception ignored) {}
            }
            return;
        }

        Leaflet_MyApplication.showInterstitialAd(this, () -> {
            Intent intent;
            if ("background_category".equals(actionType) || "all_backgrounds".equals(actionType)) {
                intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_BackgroundSelectionActivity.class);
                if ("all_backgrounds".equals(actionType)) {
                    intent.putExtra("show_all_categories", true);
                } else {
                    if (!finalCatId.isEmpty()) intent.putExtra("category_id", finalCatId);
                    if (!finalCatName.isEmpty()) intent.putExtra("category_name", finalCatName);
                }
            } else if ("create_custom".equals(actionType)) {
                intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_CreateCustomActivity.class);
            } else if ("my_creations".equals(actionType)) {
                intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_MyCreationActivity.class);
            } else if ("all_templates".equals(actionType)) {
                intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("show_all_categories", true);
            } else {
                intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                if (!finalCatId.isEmpty()) intent.putExtra("category_id", finalCatId);
                if (!finalCatName.isEmpty()) intent.putExtra("category_name", finalCatName);
            }
            startActivity(intent);
        });
    }

    private int resolveDefaultBgRes(String name) {
        if (name == null) return R.drawable.bg_cat_tile_festival;
        switch (name.trim().toLowerCase()) {
            case "wedding": return R.drawable.bg_cat_tile_wedding;
            case "sale": return R.drawable.bg_cat_tile_sale;
            case "business": return R.drawable.bg_cat_tile_business;
            case "birthday": return R.drawable.bg_cat_tile_birthday;
            case "food": return R.drawable.bg_cat_tile_food;
            case "education": return R.drawable.bg_cat_tile_education;
            case "party":
            case "event": return R.drawable.bg_cat_tile_event;
            case "more": return R.drawable.bg_cat_tile_more;
            default: return R.drawable.bg_cat_tile_festival;
        }
    }

    private int resolveDefaultIconRes(String name) {
        if (name == null) return R.drawable.ic_cat_diya_festival;
        switch (name.trim().toLowerCase()) {
            case "wedding": return R.drawable.ic_cat_rings_wedding;
            case "sale": return R.drawable.ic_cat_tag_sale;
            case "business": return R.drawable.ic_cat_bag_business;
            case "birthday": return R.drawable.ic_cat_cake_birthday;
            case "food": return R.drawable.ic_cat_burger_food;
            case "education": return R.drawable.ic_cat_cap_education;
            case "party":
            case "event": return R.drawable.ic_cat_party_event;
            case "more": return R.drawable.ic_cat_more_grid;
            default: return R.drawable.ic_cat_diya_festival;
        }
    }

    private void findByID() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_HOME);
    }

    // ===================== HERO BANNER SETUP =====================
    private void setupHeroBanner() {
        heroBannerViewPager = findViewById(R.id.hero_banner_viewpager);
        bannerIndicatorLayout = findViewById(R.id.banner_indicator_layout);

        com.facebook.shimmer.ShimmerFrameLayout heroBannerShimmer = findViewById(R.id.hero_banner_shimmer);
        View heroBannerHost = findViewById(R.id.hero_banner_host);

        if (heroBannerShimmer != null) {
            heroBannerShimmer.startShimmer();
            heroBannerShimmer.setVisibility(View.VISIBLE);
        }
        if (heroBannerHost != null) {
            heroBannerHost.setVisibility(View.GONE);
        }
        if (bannerIndicatorLayout != null) {
            bannerIndicatorLayout.setVisibility(View.GONE);
        }

        heroBannerAdapter = new Leaflet_HeroBannerAdapter(this, heroBannerList);
        heroBannerViewPager.setAdapter(heroBannerAdapter);

        // 3D Depth & Scale PageTransformer
        heroBannerViewPager.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            if (absPos <= 1) {
                float scale = 0.94f + (1 - absPos) * 0.06f;
                page.setScaleY(scale);
                page.setScaleX(scale);
                page.setAlpha(0.85f + (1 - absPos) * 0.15f);
            } else {
                page.setScaleY(0.94f);
                page.setScaleX(0.94f);
                page.setAlpha(0.85f);
            }
        });

        // Instant Load from Cache + silent background refresh
        loadHeroBannersFromApi();

        // Set starting position to middle of the fake infinite list
        if (heroBannerAdapter.getRealCount() > 0) {
            heroBannerViewPager.post(() -> {
                if (heroBannerViewPager != null && heroBannerAdapter != null && heroBannerAdapter.getRealCount() > 0) {
                    heroBannerViewPager.setCurrentItem(heroBannerAdapter.getStartPosition(), false);
                    setupBannerIndicators(heroBannerAdapter.getRealCount());
                    updateBannerIndicator(heroBannerAdapter.getStartPosition() % heroBannerAdapter.getRealCount());
                }
            });
        }

        // Page change callback for indicators
        heroBannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (heroBannerAdapter.getRealCount() > 0) {
                    updateBannerIndicator(position % heroBannerAdapter.getRealCount());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                // Reset auto-scroll timer when user interacts
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    stopAutoScroll();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    startAutoScroll();
                }
            }
        });

        // Banner click listener
        heroBannerAdapter.setOnBannerClickListener((banner, position) -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Log.d("HeroBanner", "Clicked banner: " + banner.getTitle() + " actionUrl: " + banner.getActionUrl());
            if (banner.getActionUrl() != null && banner.getActionUrl().startsWith("category/")) {
                String catId = banner.getActionUrl().replace("category/", "");
                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logCategorySelected(this, banner.getTitle(), catId, "hero_banner");
                Leaflet_MyApplication.showInterstitialAd(this, () -> {
                    Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                    intent.putExtra("category_id", catId);
                    startActivity(intent);
                });
            }
        });

        // Start auto-scroll
        startAutoScroll();
    }

    private void updateNotificationBadge() {
        android.view.View badge = findViewById(R.id.notification_badge);
        if (badge != null) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationDB db = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationDB(this);
            if (db.getUnreadCount() > 0) {
                badge.setVisibility(android.view.View.VISIBLE);
            } else {
                badge.setVisibility(android.view.View.GONE);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent != null && intent.hasExtra("launch_url")) {
            String launchUrl = intent.getStringExtra("launch_url");
            if (launchUrl != null && !launchUrl.trim().isEmpty()) {
                if (launchUrl.startsWith("http://") || launchUrl.startsWith("https://")) {
                    try {
                        com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.setExternalIntentActive(true);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launchUrl));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Log.e("Leaflet_PosterMainActivity", "Error handling notification launch url", e);
                    }
                }
            }
        }
    }

    private static final String KEY_CACHE_HERO_BANNERS = "hero_banners_cache_json";

    private void loadHeroBannersFromApi() {
        String cachedJson = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(this, KEY_CACHE_HERO_BANNERS);
        if (cachedJson != null && !cachedJson.trim().isEmpty()) {
            android.util.Log.d("HeroBanner", "Loading hero banners instantly from cache!");
            parseAndDisplayHeroBanners(cachedJson, true);
            fetchHeroBannersFromApi(true);
        } else {
            android.util.Log.d("HeroBanner", "No cached hero banners, fetching from API...");
            fetchHeroBannersFromApi(false);
        }
    }

    private void parseAndDisplayHeroBanners(String jsonString, boolean fromCache) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (dataArray.length() == 0) {
                    com.facebook.shimmer.ShimmerFrameLayout shimmer = findViewById(R.id.hero_banner_shimmer);
                    if (shimmer != null) shimmer.stopShimmer();
                    return;
                }
                heroBannerList.clear();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject bannerObj = dataArray.getJSONObject(i);
                    String bannerImage = bannerObj.optString("banner_image", "");
                    String actionUrl = bannerObj.optString("action_url", "");
                    heroBannerList.add(new Leaflet_HeroBanner(String.valueOf(i), bannerImage, "", actionUrl));
                }
                if (heroBannerAdapter != null) {
                    heroBannerAdapter.updateData(heroBannerList);
                    if (heroBannerAdapter.getRealCount() > 0) {
                        heroBannerViewPager.post(() -> {
                            if (heroBannerViewPager != null && heroBannerAdapter != null && heroBannerAdapter.getRealCount() > 0) {
                                heroBannerViewPager.setCurrentItem(heroBannerAdapter.getStartPosition(), false);
                                setupBannerIndicators(heroBannerAdapter.getRealCount());
                                updateBannerIndicator(heroBannerAdapter.getStartPosition() % heroBannerAdapter.getRealCount());
                            }
                        });
                    }
                }

                // Preload hero banner images into Glide disk cache
                for (Leaflet_HeroBanner banner : heroBannerList) {
                    if (banner.getImageUrl() != null && !banner.getImageUrl().trim().isEmpty()) {
                        try {
                            com.bumptech.glide.Glide.with(getApplicationContext())
                                    .load(banner.getImageUrl())
                                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                                    .preload();
                        } catch (Exception ignored) {}
                    }
                }
                com.facebook.shimmer.ShimmerFrameLayout shimmer = findViewById(R.id.hero_banner_shimmer);
                android.view.View host = findViewById(R.id.hero_banner_host);
                if (shimmer != null) {
                    shimmer.stopShimmer();
                    shimmer.setVisibility(android.view.View.GONE);
                }
                if (host != null) {
                    host.setVisibility(android.view.View.VISIBLE);
                }
                if (bannerIndicatorLayout != null && heroBannerAdapter != null && heroBannerAdapter.getRealCount() > 1) {
                    bannerIndicatorLayout.setVisibility(android.view.View.VISIBLE);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("HeroBanner", "Error parsing hero banners: " + e.getMessage());
        }
    }

    private void fetchHeroBannersFromApi(boolean isSilentRefresh) {
        String requestUrl = "https://cygnux.in/postermaker/api/v1/poster/hero";
        String key = preferenceClass.getDataType("field_0");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                if (response != null && response.contains("\"data\"")) {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().saveCache(this, KEY_CACHE_HERO_BANNERS, response);
                    parseAndDisplayHeroBanners(response, isSilentRefresh);
                }
            } catch (Exception e) {
                android.util.Log.e("HeroBanner", "Exception in hero banner API: " + e.getMessage());
            }
        }, error -> {
            android.util.Log.e("HeroBanner", "Error in hero banner API: " + error.getMessage());
            com.facebook.shimmer.ShimmerFrameLayout shimmer = findViewById(R.id.hero_banner_shimmer);
            if (shimmer != null) {
                shimmer.stopShimmer();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("device", "1");
                if (key != null) postMap.put("key", key);
                postMap.put("app_id", "2");
                return postMap;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(20000, 2, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void setupBannerIndicators(int count) {
        if (bannerIndicatorLayout == null) return;
        bannerIndicatorLayout.removeAllViews();
        if (count <= 1) return;

        int activeWidth = (int) (22 * getResources().getDisplayMetrics().density);
        int dotSize = (int) (6 * getResources().getDisplayMetrics().density);
        int margin = (int) (3 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (i == 0) ? activeWidth : dotSize,
                    dotSize
            );
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setImageResource((i == 0) ? R.drawable.bg_banner_indicator_active : R.drawable.bg_banner_indicator_inactive);
            bannerIndicatorLayout.addView(dot);
        }
    }

    private void updateBannerIndicator(int position) {
        if (bannerIndicatorLayout == null) return;
        int activeWidth = (int) (22 * getResources().getDisplayMetrics().density);
        int dotSize = (int) (6 * getResources().getDisplayMetrics().density);
        int margin = (int) (3 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < bannerIndicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) bannerIndicatorLayout.getChildAt(i);
            if (dot == null) continue;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
            if (params == null) continue;
            int targetWidth = (i == position) ? activeWidth : dotSize;
            int currentWidth = params.width;

            if (currentWidth != targetWidth) {
                android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofInt(currentWidth, targetWidth);
                anim.addUpdateListener(a -> {
                    params.width = (Integer) a.getAnimatedValue();
                    dot.setLayoutParams(params);
                });
                anim.setDuration(220);
                anim.setInterpolator(new android.view.animation.DecelerateInterpolator());
                anim.start();
            }
            dot.setImageResource((i == position) ? R.drawable.bg_banner_indicator_active : R.drawable.bg_banner_indicator_inactive);
        }
    }

    private void startAutoScroll() {
        stopAutoScroll();
        autoScrollRunnable = () -> {
            if (heroBannerViewPager != null && heroBannerList.size() > 0) {
                int currentItem = heroBannerViewPager.getCurrentItem();
                heroBannerViewPager.setCurrentItem(currentItem + 1, true);
                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }

    // ===================== POPULAR POSTERS SETUP =====================
    private static final String KEY_CACHE_POPULAR_POSTERS = "popular_posters_cache_json";

    private void setupPopularPosters() {
        popularPostersRv = findViewById(R.id.popular_posters_rv);
        com.facebook.shimmer.ShimmerFrameLayout popShimmer = findViewById(R.id.popular_shimmer);

        if (popShimmer != null) {
            popShimmer.startShimmer();
            popShimmer.setVisibility(View.VISIBLE);
        }
        if (popularPostersRv != null) {
            popularPostersRv.setVisibility(View.GONE);
        }

        // Horizontal layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularPostersRv.setLayoutManager(layoutManager);

        popularPosterAdapter = new Leaflet_PopularPosterAdapter(this, popularPosterList);
        popularPostersRv.setAdapter(popularPosterAdapter);

        // Call API to fetch popular posters from admin panel (with cache-first architecture)
        loadPopularPostersFromApi();

        // Poster click listener
        popularPosterAdapter.setOnPosterClickListener((poster, position) -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Log.d("PopularPoster", "Clicked poster: " + poster.getCategory() + " cat_id: " + poster.getId());
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logTemplateSelected(this, poster.getId(), poster.getCategory(), true, "popular_poster");
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("category_id", poster.getId());
                startActivity(intent);
            });
        });

        // See All click with Touch Bounce and Arrow Nudge Animation
        View popularSeeAll = findViewById(R.id.popular_see_all);
        if (popularSeeAll != null) {
            applyTouchBounceEffect(popularSeeAll);
            popularSeeAll.setOnClickListener(v -> {
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
                Leaflet_MyApplication.showInterstitialAd(this, () -> {
                    startActivity(new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class));
                });
            });
        }

        View arrowView = findViewById(R.id.iv_popular_see_all_arrow);
        if (arrowView != null) {
            startArrowNudgeAnimation(arrowView);
        }
    }

    private void loadPopularPostersFromApi() {
        String cachedJson = com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().getCache(this, KEY_CACHE_POPULAR_POSTERS);
        if (cachedJson != null && !cachedJson.trim().isEmpty()) {
            android.util.Log.d("PopularPoster", "Loading popular posters instantly from cache!");
            parseAndDisplayPopularPosters(cachedJson, true);
            fetchPopularPostersFromApi(true);
        } else {
            android.util.Log.d("PopularPoster", "No cached popular posters, fetching from API...");
            fetchPopularPostersFromApi(false);
        }
    }

    private void parseAndDisplayPopularPosters(String jsonString, boolean fromCache) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has("data")) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (dataArray.length() == 0) {
                    com.facebook.shimmer.ShimmerFrameLayout popShimmer = findViewById(R.id.popular_shimmer);
                    if (popShimmer != null) popShimmer.stopShimmer();
                    return;
                }
                popularPosterList.clear();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject posterObj = dataArray.getJSONObject(i);
                    String bannerImage = posterObj.optString("banner_image", "");
                    String catName = posterObj.optString("cat_name", "");
                    String catId = posterObj.optString("cat_id", "");
                    popularPosterList.add(new Leaflet_PopularPoster(catId, "", catName, bannerImage));
                }
                if (popularPosterAdapter != null) {
                    popularPosterAdapter.updateData(popularPosterList);
                }

                // Preload popular poster images into Glide disk cache
                for (Leaflet_PopularPoster poster : popularPosterList) {
                    if (poster.getImageUrl() != null && !poster.getImageUrl().trim().isEmpty()) {
                        try {
                            com.bumptech.glide.Glide.with(getApplicationContext())
                                    .load(poster.getImageUrl())
                                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                                    .preload();
                        } catch (Exception ignored) {}
                    }
                }

                com.facebook.shimmer.ShimmerFrameLayout popShimmer = findViewById(R.id.popular_shimmer);
                android.view.View popRv = findViewById(R.id.popular_posters_rv);
                if (popShimmer != null) {
                    popShimmer.stopShimmer();
                    popShimmer.setVisibility(android.view.View.GONE);
                }
                if (popRv != null) {
                    popRv.setVisibility(android.view.View.VISIBLE);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("PopularPoster", "Error parsing popular posters: " + e.getMessage());
        }
    }

    private void fetchPopularPostersFromApi(boolean isSilentRefresh) {
        String requestUrl = "https://cygnux.in/postermaker/api/v1/poster/trending";
        String key = preferenceClass.getDataType("field_0");
        android.util.Log.d("CygnuxAPI", "--> URL: " + requestUrl + " | Params: device=1, app_id=2, key=" + key);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                android.util.Log.d("CygnuxAPI", "<-- Popular Posters Response: " + response);
                if (response != null && response.contains("\"data\"")) {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_DataCacheManager.getInstance().saveCache(this, KEY_CACHE_POPULAR_POSTERS, response);
                    parseAndDisplayPopularPosters(response, isSilentRefresh);
                }
            } catch (Exception e) {
                android.util.Log.e("CygnuxAPI", "<-- Popular Posters Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            android.util.Log.e("CygnuxAPI", "<-- Popular Posters Error: " + error.getMessage());
            com.facebook.shimmer.ShimmerFrameLayout popShimmer = findViewById(R.id.popular_shimmer);
            if (popShimmer != null) {
                popShimmer.stopShimmer();
                popShimmer.setVisibility(android.view.View.GONE);
            }
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("device", "1");
                if (key != null) postMap.put("key", key);
                postMap.put("app_id", "2");
                return postMap;
            }
        };
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(20000, 2, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void setupAnimatedSearchBar() {
        View cardSearchBar = findViewById(R.id.card_search_bar);
        tsSearchHints = findViewById(R.id.ts_search_hints);
        if (tsSearchHints != null) {
            tsSearchHints.setFactory(() -> {
                TextView tv = new TextView(this);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
                tv.setTextColor(Color.parseColor("#6B7280"));
                tv.setMaxLines(1);
                tv.setEllipsize(TextUtils.TruncateAt.END);
                return tv;
            });
            tsSearchHints.setInAnimation(this, R.anim.spawner_hint_slide_in_bottom);
            tsSearchHints.setOutAnimation(this, R.anim.spawner_hint_slide_out_top);
            tsSearchHints.setCurrentText(searchHints[0]);
        }

        if (cardSearchBar != null) {
            cardSearchBar.setOnClickListener(v -> {
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
                Intent intent = new Intent(this, Leaflet_SearchActivity.class);
                startActivity(intent);
            });
        }

        applyTouchBounceEffect(cardSearchBar);
        View searchFilter = findViewById(R.id.iv_search_filter);
        if (searchFilter != null) {
            applyTouchBounceEffect(searchFilter);
        }

        searchHintHandler = new Handler(Looper.getMainLooper());
        searchHintRunnable = new Runnable() {
            @Override
            public void run() {
                if (tsSearchHints != null && !isFinishing() && !isDestroyed()) {
                    currentSearchHintIndex = (currentSearchHintIndex + 1) % searchHints.length;
                    tsSearchHints.setText(searchHints[currentSearchHintIndex]);
                    searchHintHandler.postDelayed(this, 2800);
                }
            }
        };
        searchHintHandler.postDelayed(searchHintRunnable, 2800);
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "home");
        if (preferenceClass != null && preferenceClass.isAdBlockerDetectionEnabled()) {
            Leaflet_AdBlockerDetector.detect(this, isBlocked -> {
                if (isBlocked && !isFinishing() && !isDestroyed()) {
                    Leaflet_AdBlockerDetector.showAdBlockerDialog(this, null);
                }
            });
        }
        startAutoScroll();
        com.facebook.shimmer.ShimmerFrameLayout popShimmer = findViewById(R.id.popular_shimmer);
        if (popShimmer != null && popShimmer.getVisibility() == View.VISIBLE) {
            popShimmer.startShimmer();
        }
        updateNotificationBadge();
        if (searchHintHandler != null && searchHintRunnable != null) {
            searchHintHandler.removeCallbacks(searchHintRunnable);
            searchHintHandler.postDelayed(searchHintRunnable, 2800);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
        if (searchHintHandler != null && searchHintRunnable != null) {
            searchHintHandler.removeCallbacks(searchHintRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchHintHandler != null && searchHintRunnable != null) {
            searchHintHandler.removeCallbacks(searchHintRunnable);
        }
    }

//    private void openActivity(Class<? extends Activity> activity) {
//        if (MailER_NetworkUtils.isNetworkAvailable(this)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if ((checkSelfPermission("android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {
//                    Dexter.withContext(getApplicationContext())
//                            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE)
//                            .withListener(new MultiplePermissionsListener() {
//                                @Override
//                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                                        nextActivity(activity);
//                                    } else {
//                                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                                            MailER_MaterialDialogUtils.getInstance().PermissionDialog(MailER_PosterMainActivity.this);
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                                    permissionToken.continuePermissionRequest();
//                                }
//                            })
//                            .withErrorListener(dexterError -> {
//                            })
//                            .check();
//                } else {
//                    nextActivity(activity);
//                }
//            } else {
//                nextActivity(activity);
//            }
//        } else {
//            Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void nextActivity(Class<? extends Activity> activity) {
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick()) return;
        Leaflet_MyApplication.showInterstitialAd(this, () -> startIntent1(activity));
    }

    private void startIntent1(Class<? extends Activity> activity) {
        if (activityIndex == 1) {
            Intent intent = new Intent(Leaflet_PosterMainActivity.this, activity);
            intent.putExtra("mode", "bg");
            startActivity(intent);
            return;
        }
        startActivity(new Intent(Leaflet_PosterMainActivity.this, activity));
    }

    @Override
    public void onBackPressed() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackPress(this, "home", "exit_dialog_shown");
        Leaflet_MaterialDialogUtils.getInstance().exitAlertDialog(Leaflet_PosterMainActivity.this, "Exit Alert", "Do you really want to exit?", materialDialog -> {
            materialDialog.dismiss();
            Leaflet_PosterMainActivity.this.finish();
            System.exit(0);
        });
    }


    private void checkAccess() {

        if (Build.VERSION.SDK_INT > 21 && !checkPermission()) {
            requestPermission();
        } else {

        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA

            }, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA

            }, 1);
        }

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (result1 == 0 && result2 == 0) {
                return true;
            }
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (result == 0 && result1 == 0 && result2 == 0) {
                return true;
            }
        }

        return false;
    }


   /* public boolean checkSelfPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA}, REQUEST_CAMERA_AND_STORAGE_PERMISSION);
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // You can request the permission here or show an explanation why the permission is needed
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA_AND_STORAGE_PERMISSION);
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_AND_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    // You can now use the camera and storage
//                    Toast.makeText(activity, "permission", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission is denied
                    // You can disable the camera and storage features or show a message to the user

                }
                break;
        }
    }
*/



 /*   private void initData() {
        userViewModel.getAppInfo().observe(this, listResource -> {

            if (listResource != null) {

                Util.showLog("Got Data: " + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB
                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            try {
                                prefManager.setString(Constant.SPLASH_SCREEN_ADS, listResource.data.splash_screen_ads);
                                prefManager.setString(Constant.PRIVACY_POLICY, listResource.data.privacyPolicy);
                                prefManager.setString(Constant.TERM_CONDITION, listResource.data.termsCondition);
                                prefManager.setString(Constant.REFUND_POLICY, listResource.data.refundPolicy);

                                prefManager.setString(Constant.PRIVACY_POLICY_LINK, listResource.data.privacyPolicy);


                                prefManager.setBoolean(Constant.ADS_ENABLE, listResource.data.adsEnabled.equals(Config.ONE) ? true : false);

                                prefManager.setString(Constant.AD_NETWORK, listResource.data.ad_network);

                                prefManager.setString(Constant.PUBLISHER_ID, listResource.data.publisher_id);

                                prefManager.setString(Constant.BANNER_AD_ID, listResource.data.banner_ad_id);
                                prefManager.setBoolean(Constant.BANNER_AD_ENABLE, listResource.data.banner_ad.equals(Config.ONE) ? true : false);

                                prefManager.setString(Constant.INTERSTITIAL_AD_ID, listResource.data.interstitial_ad_id);
                                prefManager.setBoolean(Constant.INTERSTITIAL_AD_ENABLE, listResource.data.interstitial_ad.equals(Config.ONE) ? true : false);
                                prefManager.setInt(Constant.INTERSTITIAL_AD_CLICK, Integer.parseInt(listResource.data.interstitial_ad_click));

                                prefManager.setString(Constant.NATIVE_AD_ID, listResource.data.native_ad_id);
                                prefManager.setBoolean(Constant.NATIVE_AD_ENABLE, listResource.data.native_ad.equals(Config.ONE) ? true : false);

                                prefManager.setString(Constant.OPEN_AD_ID, listResource.data.open_ad_id);
                                prefManager.setBoolean(Constant.OPEN_AD_ENABLE, listResource.data.open_ad.equals(Config.ONE) ? true : false);

                                prefManager.setBoolean(Constant.PRODUCT_ENABLE, listResource.data.productEnable.equals(Config.ONE) ? true : false);
                                prefManager.setBoolean(Constant.REFER_SYSTEM_ENABLE, listResource.data.referralSystemEnable.equals(Config.ONE) ? true : false);

                                prefManager.setString(Constant.RAZORPAY_KEY_ID, listResource.data.razorpayKeyId);
                                prefManager.setString(Constant.RAZORPAY_KEY_SECRET, listResource.data.razorpayKeySecret);

                                prefManager.setString(Constant.CASHFREE_KEY_ID, listResource.data.cashfreeKeyId);
                                prefManager.setString(Constant.CASHFREE_SECRET_ID, listResource.data.cashfreeKeySecret);

                                prefManager.setString(Constant.OFFLINE_DETAIL, listResource.data.offlinePaymentDetails);

                                prefManager.setString(Constant.PAYTM_ID, listResource.data.paytmMerchantId);
                                prefManager.setString(Constant.PAYTM_KEY, listResource.data.paytmMerchantKey);

                                prefManager.setString(Constant.STRIPE_KEY, listResource.data.stripePublishableKey);
                                prefManager.setString(Constant.STRIPE_SECRET_KEY, listResource.data.stripeSecretKey);

                                prefManager.setString(Constant.RazorPay, listResource.data.razorpayEnable);
                                prefManager.setString(Constant.CashFree, listResource.data.cashfreeEnable);
                                prefManager.setString(Constant.Offline, listResource.data.offlineEnable);
                                prefManager.setString(Constant.Paytm, listResource.data.paytmEnable);
                                prefManager.setString(Constant.Stripe, listResource.data.stripeEnable);

                                prefManager.setString(Constant.REFER_SUBS_POINT, listResource.data.subscriptionPoint);
                                prefManager.setString(Constant.REFER_LOGIN_POINT, listResource.data.registerPoint);
                                prefManager.setString(Constant.WITHDRAW_POINT, listResource.data.withdrawalLimit);

                                prefManager.setString(Constant.DIGITAL_ENABLE, listResource.data.digitalOcean);
                                prefManager.setString(Constant.DIGITAL_END_URL, listResource.data.digitalOceanEndpoint);

                                prefManager.setString(Constant.CURRENCY, listResource.data.currency);

                                prefManager.setString(Constant.CASHFREE_TYPE, listResource.data.cashfreeType);
                                prefManager.setBoolean(Constant.REWARD_AD_ENABLE, listResource.data.rewardedAdsEnable.equals(Config.ONE) ? true : false);
                                prefManager.setString(Constant.REWARD_AD_ID, listResource.data.rewardedAdsId);
                                prefManager.setString(Constant.REWARD_AD_LIMIT, listResource.data.dailyLimitRewarded);

                                prefManager.setBoolean(Constant.WHATSAPP_AUTH_ENABLE, listResource.data.whatsappAuthEnable.equals(Config.ONE) ? true : false);

                                if (prefManager.getBoolean(Constant.ADS_ENABLE)) {
                                    initializeAds();

                                    Util.TodayRewardAvl(this);

                                    if (prefManager.getInt(Constant.CURRENT_REWARD) >= Integer.valueOf(prefManager.getString(Constant.REWARD_AD_LIMIT))) {
                                        prefManager.setBoolean(Constant.AVL_REWARD, false);
                                    } else {
                                        prefManager.setBoolean(Constant.AVL_REWARD, true);
                                    }

                                }

//                                checkVersionNo(listResource.data.appVersion);

                                Config.whatsAvailable = listResource.data.whatsappContactEnable.equals(Config.ONE) ? true : false;
                                Config.whatsappNumber = listResource.data.whatsappNumber;
                                Config.offerItem = listResource.data.offerItem;

                            } catch (NullPointerException ne) {
                                Util.showErrorLog("Null Pointer Exception.", ne);
                            } catch (Exception e) {
                                Util.showErrorLog("Error in getting notification flag data.", e);
                            }

                            userViewModel.setLoadingState(false);

                        }

                        break;
                    case ERROR:
                        // Error State
                        dialogMsg.showErrorDialog(getString(R.string.click_try_again), getString(R.string.try_again));
                        dialogMsg.show();

                        dialogMsg.okBtn.setOnClickListener(v -> {
                            dialogMsg.cancel();
                            getData();
                        });

                        userViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        userViewModel.setLoadingState(false);

                        break;
                }

            } else {

                // Init Object or Empty Data
                Util.showLog("Empty Data");

            }

        });

    }

    public void getData() {
        if (Config.IS_CONNECTED) {
            Util.showLog("Internet connected");

            FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                boolean updated = task.getResult();
                                Util.showLog("Config params updated: " + updated);

                            } else {
                                Log.e("TAG", "onComplete: " + task.isSuccessful());
                            }
                            Util.showLog("API_KEY: " + mFirebaseRemoteConfig.getString("apiKey"));
                            prefManager.setString(Constant.api_key, mFirebaseRemoteConfig.getString("apiKey"));

                            Config.API_KEY = prefManager.getString(Constant.api_key);
                            prefManager.setString("FIRST", "TRUE");
                            userViewModel.setAppInfo("new");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Util.showErrorLog("Firebase", e);
                            Log.e("#OnFail", e.getMessage());
                            //  gotoMainActivity();
                            prefManager.setBoolean(Constant.IS_LOGIN, true);
                            startActivity(new Intent(MainActivity.this, BusinessActivity.class));
                        }
                    });

        } else {
            Util.showLog("Internet is not connected");
            prefManager.setBoolean(Constant.IS_LOGIN, true);
            startActivity(new Intent(MainActivity.this, BusinessActivity.class));
            //   gotoMainActivity();
        }
    }

    private void initializeAds() {
        switch (prefManager.getString(Constant.AD_NETWORK)) {
            case Constant.ADMOB:
                break;
            case Constant.UNITY:
                break;
            case Constant.FACEBOOK:
                break;
        }
    }*/


}
