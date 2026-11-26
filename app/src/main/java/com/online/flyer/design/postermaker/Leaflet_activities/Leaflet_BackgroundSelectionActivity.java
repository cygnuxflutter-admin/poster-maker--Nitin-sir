package com.online.flyer.design.postermaker.Leaflet_activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_RewardVideoManager;
import com.online.flyer.design.postermaker.Leaflet_controller.Leaflet_BGSelectionController;
import com.online.flyer.design.postermaker.Leaflet_fragment.Leaflet_BackgroundFragment;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_DlBgAsync;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

public class Leaflet_BackgroundSelectionActivity extends AppCompatActivity implements Leaflet_BackgroundFragment.GetPosterListener {

    private final ArrayList<String> strings = new ArrayList<>();
    private Leaflet_BGSelectionController bgSelectionController;
    private String mode, path;
    private boolean local = false;
    private boolean isRewarded = false;

    private ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> allCategories;
    private boolean isLoadingBg = false;
    private ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> currentTabList;
    private androidx.viewpager.widget.ViewPager.OnPageChangeListener lazyLoadPageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_background_selection);

        findByID();

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                if (bgSelectionController != null) {
                    bgSelectionController.refreshData(true);
                }
                swipeRefresh.setRefreshing(false);
            });
        }

        android.view.View backBtn = findViewById(R.id.ic_back);
        if (backBtn != null) backBtn.setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            onBackPressed();
        });
        android.view.View backContainer = findViewById(R.id.ic_back_container);
        if (backContainer != null) backContainer.setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            onBackPressed();
        });

        findViewById(R.id.ic_gallery).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            bgSelectionController.openPhotoGallery();
        });

        findViewById(R.id.ic_color).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            bgSelectionController.openColorDialog();
        });


        android.view.View lyTitle = findViewById(R.id.ly_title);
        android.view.View lySearch = findViewById(R.id.ly_search);
        android.view.View icSearchContainer = findViewById(R.id.ic_search_container);
        android.view.View icSearchClose = findViewById(R.id.ic_search_close);
        android.widget.EditText etSearch = findViewById(R.id.et_search);
        final android.view.View llBottomBar = findViewById(R.id.ll_bottom_bar);
        final android.widget.RelativeLayout rl_ad = findViewById(R.id.rl_ad);

        if (getIntent().getBooleanExtra("auto_load", false)) {
            String autoBgPath = getIntent().getStringExtra("bg_image");
            boolean premium = getIntent().getBooleanExtra("premium", false);
            onPosterClick(autoBgPath, premium);
        }

        if (icSearchContainer != null) {
            icSearchContainer.setOnClickListener(v -> {
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
                Intent searchIntent = new Intent(this, Leaflet_SearchActivity.class);
                searchIntent.putExtra("filter", "backgrounds");
                startActivity(searchIntent);
            });
        }

        if (icSearchClose != null) {
            icSearchClose.setOnClickListener(v -> {
                etSearch.setText("");
                lySearch.setVisibility(android.view.View.GONE);
                lyTitle.setVisibility(android.view.View.VISIBLE);
                icSearchContainer.setVisibility(android.view.View.VISIBLE);
                if (llBottomBar != null) {
                    llBottomBar.setTag(null);
                    llBottomBar.setVisibility(android.view.View.VISIBLE);
                }
                if (rl_ad != null) {
                    rl_ad.setTag(null);
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(Leaflet_BackgroundSelectionActivity.this);
                    if (pref.isBannerAdEnabled("backgrounds")) {
                        rl_ad.setVisibility(android.view.View.VISIBLE);
                    }
                }
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            });
        }

        if (etSearch != null) {
            etSearch.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    if (llBottomBar != null) {
                        llBottomBar.setTag("SUPPRESSED");
                        llBottomBar.setVisibility(android.view.View.GONE);
                    }
                    if (rl_ad != null) {
                        rl_ad.setTag("SUPPRESSED");
                        rl_ad.setVisibility(android.view.View.GONE);
                    }
                }
            });
        }

        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_KeyboardUtils.attachKeyboardListener(this, isVisible -> {
            if (llBottomBar != null) {
                if (isVisible) {
                    llBottomBar.setTag("SUPPRESSED");
                    llBottomBar.setVisibility(android.view.View.GONE);
                    if (rl_ad != null) {
                        rl_ad.setTag("SUPPRESSED");
                        rl_ad.setVisibility(android.view.View.GONE);
                    }
                } else {
                    android.view.View currentLySearch = findViewById(R.id.ly_search);
                    boolean isSearchOpen = currentLySearch != null && currentLySearch.getVisibility() == android.view.View.VISIBLE;
                    if (!isSearchOpen) {
                        llBottomBar.setTag(null);
                        llBottomBar.setVisibility(android.view.View.VISIBLE);
                        if (rl_ad != null) {
                            rl_ad.setTag(null);
                            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(Leaflet_BackgroundSelectionActivity.this);
                            if (pref.isBannerAdEnabled("backgrounds")) {
                                rl_ad.setVisibility(android.view.View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });

        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (llBottomBar != null && llBottomBar.getVisibility() != android.view.View.GONE) {
                        llBottomBar.setTag("SUPPRESSED");
                        llBottomBar.setVisibility(android.view.View.GONE);
                    }
                    if (rl_ad != null && rl_ad.getVisibility() != android.view.View.GONE) {
                        rl_ad.setTag("SUPPRESSED");
                        rl_ad.setVisibility(android.view.View.GONE);
                    }
                    if (allCategories == null) return;
                    String query = s.toString().toLowerCase().trim();
                    currentTabList.clear();
                    if (query.isEmpty()) {
                        currentTabList.addAll(allCategories);
                    } else {
                        for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel category : allCategories) {
                            if (category.getCategory_name().toLowerCase().contains(query)) {
                                currentTabList.add(category);
                            }
                        }
                    }
                    
                    com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                    androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
                    if (vp != null && vp.getAdapter() != null) {
                        vp.getAdapter().notifyDataSetChanged();
                        if (tabs != null) tabs.notifyDataSetChanged();
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
        
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_BACKGROUNDS);

        // Load Banner Ad
        if (rl_ad != null) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(this);
            if (pref.isBannerAdEnabled("backgrounds")) {
                if (!"SUPPRESSED".equals(rl_ad.getTag()) && !com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_KeyboardUtils.isKeyboardVisible(this)) {
                    rl_ad.setVisibility(android.view.View.VISIBLE);
                }
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils.isNetworkAvailable(this)) {
                    com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(this, rl_ad, "backgrounds");
                }
            } else {
                rl_ad.removeAllViews();
                rl_ad.setVisibility(android.view.View.GONE);
            }
        }
    }

    private void findByID() {
        Leaflet_PreferenceClass preferenceClass = new Leaflet_PreferenceClass(this);
        mode = getIntent().getStringExtra("mode");
        bgSelectionController = new Leaflet_BGSelectionController(this, getSupportFragmentManager(), preferenceClass, mode);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        selectTargetCategory();
    }

    public int findTargetCategoryIndex(ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> categories) {
        if (categories == null || getIntent() == null) return -1;
        String targetCat = getIntent().getStringExtra("category_name");
        String targetCatId = getIntent().getStringExtra("category_id");
        if (targetCat == null && targetCatId == null) return -1;

        // 1. Try matching by category_id
        if (targetCatId != null && !targetCatId.trim().isEmpty()) {
            String cleanId = targetCatId.trim();
            for (int i = 0; i < categories.size(); i++) {
                if (String.valueOf(categories.get(i).getCategory_id()).equals(cleanId)) {
                    return i;
                }
            }
        }

        // 2. Try exact name match (case-insensitive)
        if (targetCat != null && !targetCat.trim().isEmpty()) {
            String cleanTarget = targetCat.trim().toLowerCase();
            for (int i = 0; i < categories.size(); i++) {
                String catName = categories.get(i).getCategory_name();
                if (catName != null && catName.trim().equalsIgnoreCase(cleanTarget)) {
                    return i;
                }
            }
            // 3. Try partial name match (contains)
            for (int i = 0; i < categories.size(); i++) {
                String catName = categories.get(i).getCategory_name();
                if (catName != null) {
                    String cleanCat = catName.trim().toLowerCase();
                    if (cleanCat.contains(cleanTarget) || cleanTarget.contains(cleanCat)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private void selectTargetCategory() {
        if (allCategories == null || currentTabList == null) return;
        androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
        if (vp == null) return;

        int targetIndex = findTargetCategoryIndex(allCategories);
        if (targetIndex == -1) return;

        if (targetIndex < currentTabList.size() && vp.getCurrentItem() == targetIndex) {
            return;
        }

        com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
        boolean added = false;
        while (currentTabList.size() <= targetIndex) {
            com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel nextCat = allCategories.get(currentTabList.size());
            currentTabList.add(nextCat);
            if (tabs != null) tabs.appendTabBeforeMore(nextCat.getCategory_name());
            added = true;
        }
        if (added && vp.getAdapter() != null) {
            vp.getAdapter().notifyDataSetChanged();
        }
        vp.setCurrentItem(targetIndex, false);
    }

    public void onCategoriesLoaded(ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> categories, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> tabList) {
        this.allCategories = categories;
        this.currentTabList = tabList;
        
        selectTargetCategory();
        
        if (getIntent().getBooleanExtra("show_all_categories", false)) {
            openAllCategories();
            getIntent().removeExtra("show_all_categories");
        }
        
        androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
        if (vp != null) {
            if (lazyLoadPageListener != null) {
                vp.removeOnPageChangeListener(lazyLoadPageListener);
            }
            lazyLoadPageListener = new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {}

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING) {
                        if (vp.getCurrentItem() == currentTabList.size() - 1) {
                            if (currentTabList.size() < allCategories.size()) {
                                com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel nextCat = allCategories.get(currentTabList.size());
                                currentTabList.add(nextCat);
                                if (vp.getAdapter() != null) {
                                    vp.getAdapter().notifyDataSetChanged();
                                }
                                com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                                if (tabs != null) tabs.appendTabBeforeMore(nextCat.getCategory_name());
                            }
                        }
                    }
                }
            };
            vp.addOnPageChangeListener(lazyLoadPageListener);
        }
    }

    public void openAllCategories() {
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        if (allCategories == null) return;
        java.util.ArrayList<String> catNames = new java.util.ArrayList<>();
        for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel model : allCategories) {
            catNames.add(model.getCategory_name());
        }
        com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.allCategoriesList = catNames;
        
        androidx.viewpager.widget.ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null && viewPager.getAdapter() != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < currentTabList.size()) {
                com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.currentlySelectedCategory = 
                    currentTabList.get(currentItem).getCategory_name();
            }
        }

        Intent intent = new Intent(this, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.class);
        startActivityForResult(intent, 1001);
    }

    public String getMode() {
        return mode;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2001 && resultCode == RESULT_OK && data != null) {
            String bgUrl = data.getStringExtra("image_url");
            String bgImagePath = data.getStringExtra("bg_image");
            if (bgImagePath == null || bgImagePath.isEmpty()) {
                if (bgUrl != null) {
                    bgImagePath = Leaflet_FileUtils.getFile(this, bgUrl);
                }
            }

            if ("user".equals(mode)) {
                if (bgImagePath != null && new File(bgImagePath).exists()) {
                    Intent result = new Intent();
                    result.putExtra("local", false);
                    result.putExtra("bg_image", bgImagePath);
                    setResult(RESULT_OK, result);
                    finish();
                    return;
                } else if (bgUrl != null && !bgUrl.isEmpty()) {
                    strings.clear();
                    strings.add(bgUrl);
                    downloadTask(strings);
                    return;
                }
            }

            boolean isPremium = data.getBooleanExtra("is_premium", false);
            boolean alreadyUnlocked = data.getBooleanExtra("is_unlocked_by_reward", false);
            if (alreadyUnlocked) {
                isRewarded = true;
            }
            if (bgUrl != null && !bgUrl.isEmpty()) {
                strings.clear();
                strings.add(bgUrl);
                downloadTask(strings);
            }
            return;
        }

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String selectedCatName = data.getStringExtra("selected_category_name");
            if (selectedCatName != null && allCategories != null && currentTabList != null) {
                int targetIndex = -1;
                for (int i = 0; i < allCategories.size(); i++) {
                    if (selectedCatName.equalsIgnoreCase(allCategories.get(i).getCategory_name())) {
                        targetIndex = i;
                        break;
                    }
                }
                
                if (targetIndex != -1) {
                    com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                    boolean added = false;
                    while (currentTabList.size() <= targetIndex) {
                        com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel nextCat = allCategories.get(currentTabList.size());
                        currentTabList.add(nextCat);
                        if (tabs != null) tabs.appendTabBeforeMore(nextCat.getCategory_name());
                        added = true;
                    }
                    
                    androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
                    if (vp != null && vp.getAdapter() != null) {
                        if (added) vp.getAdapter().notifyDataSetChanged();
                        vp.setCurrentItem(targetIndex, false);
                    }
                }
            }
            return;
        }

        if (resultCode == RESULT_OK && requestCode == bgSelectionController.CAMERA_INTENT) {
            Uri selectedImage = Uri.fromFile(bgSelectionController.camera_file);
            if ("user".equals(mode)) {
                Intent intent = new Intent();
                intent.putExtra("local", true);
                intent.putExtra("bg_image", selectedImage.toString());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                bgSelectionController.startCrop(selectedImage);
            }
        }
        if (resultCode == RESULT_OK && requestCode == bgSelectionController.GALLERY_INTENT && data != null) {
            Uri selectedImage = data.getData();
            assert selectedImage != null;
            if ("user".equals(mode)) {
                Intent intent = new Intent();
                intent.putExtra("local", true);
                intent.putExtra("bg_image", selectedImage.toString());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                bgSelectionController.startCrop(selectedImage);
            }
        }

        if (resultCode == RESULT_CANCELED && requestCode == UCrop.REQUEST_CROP) {
            isRewarded = false;
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            assert data != null;//file:///storage/emulated/0/SampleCropImage.png
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                path = resultUri.toString();
                if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
                    if (isRewarded) {
                        if ("user".equals(mode)) {
                            setPosterIntent();
                        } else {
                            startIntent();
                        }
                        isRewarded = false;
                        return;
                    }

                    if ("user".equals(mode)) {
                        Leaflet_MyApplication.showInterstitialAd(this, this::setPosterIntent);
                    } else {
                        Leaflet_MyApplication.showInterstitialAd(this, this::startIntent);
                    }

                } else {
                    Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
            final Throwable resultUri = UCrop.getError(data);
            Toast.makeText(this, resultUri.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "background_selection");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        android.view.View lySearch = findViewById(R.id.ly_search);
        android.view.View icSearchClose = findViewById(R.id.ic_search_close);
        if (lySearch != null && lySearch.getVisibility() == android.view.View.VISIBLE) {
            if (icSearchClose != null) {
                icSearchClose.performClick();
                return;
            }
        }
        if (System.currentTimeMillis() - com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.getLastAdDismissedTime() < 800) {
            return;
        }
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackPress(this, "background_selection", "back_button");
        if (getIntent().getBooleanExtra("auto_load", false)) {
            com.online.flyer.design.postermaker.Leaflet_MyApplication.showBackInterstitialAd(this, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                @Override
                public void onAdClose() {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        } else {
            com.online.flyer.design.postermaker.Leaflet_MyApplication.showBackInterstitialAd(this, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                @Override
                public void onAdClose() {
                    android.content.Intent intent = new android.content.Intent(Leaflet_BackgroundSelectionActivity.this, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity.class);
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    public void onPosterClick(String path, boolean premium) {
        if (isLoadingBg || com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackgroundSelected(this, path, premium ? "premium" : "free");
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                        requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                    } else {
//                        MailER_MaterialDialogUtils.getInstance().PermissionDialog(this);
//                    }
//                    return;
//                }
//            }
            strings.clear();
            strings.add(path);
            final Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(this);
            boolean shouldLockBg = premium
                    && pref.isPremiumLockEnabled("backgrounds")
                    && pref.isProEditPopupEnabled("backgrounds")
                    && !pref.isBackgroundUnlocked(path);
            if (shouldLockBg) {
                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogShown(this, "background_unlock");
                Leaflet_MaterialDialogUtils.getInstance().rewardDialog(this, "Unlock Background", "Watch a short video ad to unlock and use this premium background.", "backgrounds", materialDialog -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogAction(this, "background_unlock", "watch_video");
//                    bgSelectionController.startMaterialDialog();
                    String adType = pref.getPremiumPosterAdType("backgrounds");
                    if ("Reward".equalsIgnoreCase(adType)) {
                        String rewardId = pref.getPremiumPosterRewardAdId("backgrounds");
                        Leaflet_RewardVideoManager.showRewardVideoAd(Leaflet_BackgroundSelectionActivity.this, rewardId, new Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardAdCompleted(Leaflet_BackgroundSelectionActivity.this, "background_unlock");
                                pref.saveBackgroundUnlockTime(path);
                                isRewarded = true;
                                downloadTask(strings);
                            }

                            @Override
                            public void onAdFailedToEarnReward() {
                                if (getIntent().getBooleanExtra("auto_load", false)) {
                                    finish();
                                }
                            }
                        });
                    } else {
                        Leaflet_MyApplication.showUnlockInterstitialAd(Leaflet_BackgroundSelectionActivity.this, new Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardAdCompleted(Leaflet_BackgroundSelectionActivity.this, "background_unlock");
                                pref.saveBackgroundUnlockTime(path);
                                isRewarded = true;
                                downloadTask(strings);
                            }

                            @Override
                            public void onAdFailedToEarnReward() {
                                if (getIntent().getBooleanExtra("auto_load", false)) {
                                    finish();
                                }
                            }
                        });
                    }
//                    MyApplication.showInterstitialAd(this,() -> downloadTask(strings));
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                }, materialDialog -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogAction(this, "background_unlock", "cancel");
                    isLoadingBg = false;
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    if (getIntent().getBooleanExtra("auto_load", false)) {
                        finish();
                    }
                });
            } else {
                downloadTask(strings);
            }
        } else {
            Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
            if (getIntent().getBooleanExtra("auto_load", false)) {
                finish();
            }
        }
    }

    private void downloadTask(ArrayList<String> strings) {
        if (isLoadingBg) return;
        isLoadingBg = true;
        bgSelectionController.startMaterialDialog();
        new Leaflet_DlBgAsync(Leaflet_BackgroundSelectionActivity.this, strings, new Leaflet_DlBgAsync.OnDownloadTemplateListener() {
            @Override
            public void onDownloadComplete() {
                bgSelectionController.dismissMaterialDialog();
                if ("user".equals(mode)) {
                    local = false;
                    path = Leaflet_FileUtils.getFile(Leaflet_BackgroundSelectionActivity.this, strings.get(0));

                    if (isRewarded) {
                        setPosterIntent();
                    } else {
                        Leaflet_MyApplication.showInterstitialAd(Leaflet_BackgroundSelectionActivity.this, () -> setPosterIntent());
                    }
                } else {
                    if (isRewarded) {
                        bgSelectionController.startCrop(Uri.fromFile(new File(Leaflet_FileUtils.getFile(Leaflet_BackgroundSelectionActivity.this, strings.get(0)))));
                    } else {
                        Leaflet_MyApplication.showInterstitialAd(Leaflet_BackgroundSelectionActivity.this, () -> {
                            bgSelectionController.startCrop(Uri.fromFile(new File(Leaflet_FileUtils.getFile(Leaflet_BackgroundSelectionActivity.this, strings.get(0)))));
                        });
                    }

                }
            }

            @Override
            public void onError() {
                isLoadingBg = false;
                Toast.makeText(Leaflet_BackgroundSelectionActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                bgSelectionController.dismissMaterialDialog();
            }
        }).execute();
    }

    private void startIntent() {
        isLoadingBg = false;
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        Intent intent = new Intent(this, Leaflet_PosterEditActivity.class);
        intent.putExtra("bg_path", path);
        intent.putExtra("loadUserFrame", true);
        intent.putExtra("Temp_Type", "MY_TEMP");
        if (isRewarded) {
            intent.putExtra("show_celebration", true);
            intent.putExtra("celebration_message", "✨ Background Unlocked!");
        }
        startActivity(intent);
        if (getIntent().getBooleanExtra("auto_load", false)) {
            finish();
        }
    }

    private void setPosterIntent() {
        Intent intent = new Intent();
        intent.putExtra("local", local);
        String finalPath = path;
        if (finalPath != null && !finalPath.startsWith("content://") && !finalPath.startsWith("file://") && !new File(finalPath).exists()) {
            finalPath = Leaflet_FileUtils.getFile(Leaflet_BackgroundSelectionActivity.this, finalPath);
        }
        intent.putExtra("bg_image", finalPath);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(Leaflet_BackgroundSelectionActivity.this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA

            }, 1);
        } else {
            ActivityCompat.requestPermissions(Leaflet_BackgroundSelectionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA

            }, 1);
        }

    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result1 = ContextCompat.checkSelfPermission(Leaflet_BackgroundSelectionActivity.this, Manifest.permission.READ_MEDIA_IMAGES);
            int result2 = ContextCompat.checkSelfPermission(Leaflet_BackgroundSelectionActivity.this, Manifest.permission.CAMERA);

            if (result1 == 0 && result2 == 0) {
                return true;
            }
        } else {
            int result = ContextCompat.checkSelfPermission(Leaflet_BackgroundSelectionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(Leaflet_BackgroundSelectionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(Leaflet_BackgroundSelectionActivity.this, Manifest.permission.CAMERA);

            if (result == 0 && result1 == 0 && result2 == 0) {
                return true;
            }
        }

        return false;
    }

}
