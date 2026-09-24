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
import com.online.flyer.design.postermaker.Leaflet_adapter.Leaflet_PopularPosterAdapter;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_HeroBanner;
import com.online.flyer.design.postermaker.Leaflet_models.Leaflet_PopularPoster;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
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

    // Popular Posters
    private RecyclerView popularPostersRv;
    private Leaflet_PopularPosterAdapter popularPosterAdapter;
    private List<Leaflet_PopularPoster> popularPosterList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.leaflet_activity_mainposter);

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
        OneSignal.promptForPushNotifications();

        checkAccess();

//        prefManager = new MailER_PrefManager(this);
//        dialogMsg = new MailER_DialogMsg(this, false);
//        userViewModel = new ViewModelProvider(this).get(MailER_UserViewModel.class);


        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        preferenceClass = new Leaflet_PreferenceClass(this);

        findByID();

        findViewById(R.id.lay_poster).setOnClickListener(v -> {
            activityIndex = 1;
            if (!checkPermission()) {
                try {
                    requestPermission();

//                    if (Build.MANUFACTURER.equals("TECNO MOBILE LIMITED") && Build.BRAND.equals("TECNO") ||
//                            Build.BRAND.equals("samsung") || Build.BRAND.equals("OPPO") ||
//                            Build.BRAND.equals("vivo")) {
                    Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
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
            activityIndex = 2;
            if (!checkPermission()) {
                try {
                    requestPermission();

//                    if (Build.MANUFACTURER.equals("TECNO MOBILE LIMITED") && Build.BRAND.equals("TECNO") ||
//                            Build.BRAND.equals("samsung") || Build.BRAND.equals("OPPO") ||
//                            Build.BRAND.equals("vivo")) {
                    Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
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

        findViewById(R.id.lay_design).setOnClickListener(v -> {
            Leaflet_ShareUtils.rateUs(Leaflet_PosterMainActivity.this);
        });

        findViewById(R.id.lay_creation).setOnClickListener(v -> {
            activityIndex = 4;
            if (!checkPermission()) {
                try {
                    requestPermission();
                    Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(Leaflet_PosterMainActivity.this);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            } else {
                nextActivity(Leaflet_MyCreationActivity.class);
            }
        });

        // Category click listener
        android.view.View.OnClickListener catClickListener = v -> {
            String catName = "";
            int id = v.getId();
            if (id == R.id.cat_festival) catName = "Festival";
            else if (id == R.id.cat_business) catName = "Business";
            else if (id == R.id.cat_social) catName = "Social";
            else if (id == R.id.cat_birthday) catName = "Birthday";
            else if (id == R.id.cat_event) catName = "Event";

            String finalCatName = catName;
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("category_name", finalCatName);
                startActivity(intent);
            });
        };

        findViewById(R.id.cat_festival).setOnClickListener(catClickListener);
        findViewById(R.id.cat_business).setOnClickListener(catClickListener);
        findViewById(R.id.cat_social).setOnClickListener(catClickListener);
        findViewById(R.id.cat_birthday).setOnClickListener(catClickListener);
        findViewById(R.id.cat_event).setOnClickListener(catClickListener);

        // Setup Hero Banner and Popular Posters
        setupHeroBanner();
        setupPopularPosters();
        
        // Category See All click listener
        findViewById(R.id.category_see_all).setOnClickListener(v -> {
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("show_all_categories", true);
                startActivity(intent);
            });
        });
    }


    private void findByID() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_HOME);
    }

    // ===================== HERO BANNER SETUP =====================
    private void setupHeroBanner() {
        heroBannerViewPager = findViewById(R.id.hero_banner_viewpager);
        bannerIndicatorLayout = findViewById(R.id.banner_indicator_layout);

        // Call API to fetch banners from admin panel
        loadHeroBannersFromApi();

        heroBannerAdapter = new Leaflet_HeroBannerAdapter(this, heroBannerList);
        heroBannerViewPager.setAdapter(heroBannerAdapter);

        // Set starting position to middle of the fake infinite list
        if (heroBannerAdapter.getRealCount() > 0) {
            heroBannerViewPager.setCurrentItem(heroBannerAdapter.getStartPosition(), false);
            // Setup dot indicators
            setupBannerIndicators(heroBannerAdapter.getRealCount());
            updateBannerIndicator(heroBannerAdapter.getStartPosition() % heroBannerAdapter.getRealCount());
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
            Log.d("HeroBanner", "Clicked banner: " + banner.getTitle() + " actionUrl: " + banner.getActionUrl());
            if (banner.getActionUrl() != null && banner.getActionUrl().startsWith("category/")) {
                String catId = banner.getActionUrl().replace("category/", "");
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

    private void loadHeroBannersFromApi() {
        String requestUrl = "https://cygnux.in/postermaker/api/v1/poster/hero";
        String key = preferenceClass.getDataType("field_0");
        android.util.Log.d("CygnuxAPI", "--> URL: " + requestUrl + " | Params: device=1, app_id=2, key=" + key);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                android.util.Log.d("CygnuxAPI", "<-- Hero Banners Response: " + response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
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
                            heroBannerViewPager.setCurrentItem(heroBannerAdapter.getStartPosition(), false);
                            setupBannerIndicators(heroBannerAdapter.getRealCount());
                            updateBannerIndicator(heroBannerAdapter.getStartPosition() % heroBannerAdapter.getRealCount());
                        }
                    }
                } else {
                    android.util.Log.e("CygnuxAPI", "<-- No data field in hero response");
                }
            } catch (Exception e) {
                android.util.Log.e("CygnuxAPI", "<-- Hero Banners Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            android.util.Log.e("CygnuxAPI", "<-- Hero Banners Error: " + error.getMessage());
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void setupBannerIndicators(int count) {
        bannerIndicatorLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(R.drawable.bg_banner_indicator_inactive);
            bannerIndicatorLayout.addView(dot);
        }
    }

    private void updateBannerIndicator(int position) {
        for (int i = 0; i < bannerIndicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) bannerIndicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.bg_banner_indicator_active);
            } else {
                dot.setImageResource(R.drawable.bg_banner_indicator_inactive);
            }
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
    private void setupPopularPosters() {
        popularPostersRv = findViewById(R.id.popular_posters_rv);

        // Horizontal layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        popularPostersRv.setLayoutManager(layoutManager);

        // TODO: Replace with API call to fetch popular posters from admin panel
        // Call API to fetch popular posters from admin panel
        loadPopularPostersFromApi();

        popularPosterAdapter = new Leaflet_PopularPosterAdapter(this, popularPosterList);
        popularPostersRv.setAdapter(popularPosterAdapter);

        // Poster click listener
        popularPosterAdapter.setOnPosterClickListener((poster, position) -> {
            Log.d("PopularPoster", "Clicked poster: " + poster.getCategory() + " cat_id: " + poster.getId());
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                Intent intent = new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class);
                intent.putExtra("category_id", poster.getId());
                startActivity(intent);
            });
        });

        // See All click
        findViewById(R.id.popular_see_all).setOnClickListener(v -> {
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                startActivity(new Intent(Leaflet_PosterMainActivity.this, Leaflet_TemplateSelectionActivity.class));
            });
        });
    }

    private void loadPopularPostersFromApi() {
        String requestUrl = "https://cygnux.in/postermaker/api/v1/poster/trending";
        String key = preferenceClass.getDataType("field_0");
        android.util.Log.d("CygnuxAPI", "--> URL: " + requestUrl + " | Params: device=1, app_id=2, key=" + key);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
            try {
                android.util.Log.d("CygnuxAPI", "<-- Popular Posters Response: " + response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("data")) {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    popularPosterList.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject posterObj = dataArray.getJSONObject(i);
                        String bannerImage = posterObj.optString("banner_image", "");
                        String catName = posterObj.optString("cat_name", "");
                        String catId = posterObj.optString("cat_id", "");
                        
                        // name is left empty, category is catName, id is catId
                        popularPosterList.add(new Leaflet_PopularPoster(catId, "", catName, bannerImage));
                    }
                    if (popularPosterAdapter != null) {
                        popularPosterAdapter.updateData(popularPosterList);
                    }
                } else {
                    android.util.Log.e("CygnuxAPI", "<-- No data field in popular posters response");
                }
            } catch (Exception e) {
                android.util.Log.e("CygnuxAPI", "<-- Popular Posters Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }, error -> {
            android.util.Log.e("CygnuxAPI", "<-- Popular Posters Error: " + error.getMessage());
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
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
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