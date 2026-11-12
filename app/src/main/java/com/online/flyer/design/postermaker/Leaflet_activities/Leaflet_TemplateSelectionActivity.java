package com.online.flyer.design.postermaker.Leaflet_activities;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_RewardVideoManager;
import com.online.flyer.design.postermaker.Leaflet_controller.Leaflet_TemplateSelectionController;
import com.online.flyer.design.postermaker.Leaflet_fragment.Leaflet_TemplateFragment;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_StickerModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TemplateModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_TextModel;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_DlTemplateAsync;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetPosDetail;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Leaflet_TemplateSelectionActivity extends AppCompatActivity implements Leaflet_TemplateFragment.GetPosterListener {

    private final ArrayList<String> stringArrayList = new ArrayList<>();
    private Leaflet_TemplateSelectionController templateSelectionController;
    private ArrayList<Leaflet_TemplateModel> templateModels;
    private ArrayList<Leaflet_StickerModel> sticker_model;
    private ArrayList<Leaflet_TextModel> text_model;
    private int cat_id, post_id;
    private boolean local_permission = false;
    private boolean isRewarded = false;
    private Leaflet_PreferenceClass preferenceClass;
    private ArrayList<Leaflet_PosterModel> allCategories;
    private boolean isLoadingPoster = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_template_selection);

        findByID();

        androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                if (templateSelectionController != null) {
                    templateSelectionController.refreshData(true);
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
        deleteFromExternalStorage();

        if (getIntent().getBooleanExtra("auto_load", false)) {
            int catId = getIntent().getIntExtra("cat_id", 0);
            int postId = getIntent().getIntExtra("post_id", 0);
            boolean isPremium = getIntent().getBooleanExtra("auto_open_premium", false);
            onPosterClick(catId, postId, isPremium);
        }
    }

    private ArrayList<Leaflet_PosterModel> currentTabList;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        selectTargetCategory();
    }

    private void selectTargetCategory() {
        if (allCategories == null || currentTabList == null) return;
        String targetCat = getIntent().getStringExtra("category_name");
        String targetCatId = getIntent().getStringExtra("category_id");
        if (targetCat == null && targetCatId == null) return;

        androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
        if (vp == null) return;

        int targetIndex = -1;
        // 1. Match by ID
        if (targetCatId != null && !targetCatId.trim().isEmpty()) {
            for (int i = 0; i < allCategories.size(); i++) {
                if (String.valueOf(allCategories.get(i).getCat_id()).equals(targetCatId.trim())) {
                    targetIndex = i;
                    break;
                }
            }
        }

        // 2. Exact match by name (case-insensitive)
        if (targetIndex == -1 && targetCat != null && !targetCat.trim().isEmpty()) {
            String cleanTarget = targetCat.trim().toLowerCase();
            for (int i = 0; i < allCategories.size(); i++) {
                String catName = allCategories.get(i).getCat_name();
                if (catName != null && catName.trim().equalsIgnoreCase(cleanTarget)) {
                    targetIndex = i;
                    break;
                }
            }
        }

        // 3. Partial match (contains)
        if (targetIndex == -1 && targetCat != null && !targetCat.trim().isEmpty()) {
            String cleanTarget = targetCat.trim().toLowerCase();
            for (int i = 0; i < allCategories.size(); i++) {
                String catName = allCategories.get(i).getCat_name();
                if (catName != null) {
                    String cleanCat = catName.trim().toLowerCase();
                    if (cleanCat.contains(cleanTarget) || cleanTarget.contains(cleanCat)) {
                        targetIndex = i;
                        break;
                    }
                }
            }
        }

        if (targetIndex != -1) {
            while (currentTabList.size() <= targetIndex) {
                currentTabList.add(allCategories.get(currentTabList.size()));
            }
            if (vp.getAdapter() != null) {
                vp.getAdapter().notifyDataSetChanged();
            }
            com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
            if (tabs != null) tabs.notifyDataSetChanged();
            vp.setCurrentItem(targetIndex);
        }
    }

    public void onCategoriesLoaded(ArrayList<Leaflet_PosterModel> categories, ArrayList<Leaflet_PosterModel> tabList) {
        this.allCategories = categories;
        this.currentTabList = tabList;
        
        androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
        selectTargetCategory();
        
        if (getIntent().getBooleanExtra("show_all_categories", false)) {
            openAllCategories();
            getIntent().removeExtra("show_all_categories");
        }


        if (vp != null) {
            vp.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    // Do nothing here, wait for manual swipe drag
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING) {
                        if (vp.getCurrentItem() == currentTabList.size() - 1) {
                            if (currentTabList.size() < allCategories.size()) {
                                currentTabList.add(allCategories.get(currentTabList.size()));
                                if (vp.getAdapter() != null) {
                                    vp.getAdapter().notifyDataSetChanged();
                                }
                                com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                                if (tabs != null) tabs.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
        }

        android.view.View lyTitle = findViewById(R.id.ly_title);
        android.view.View lySearch = findViewById(R.id.ly_search);
        android.view.View icSearchContainer = findViewById(R.id.ic_search_container);
        android.view.View icSearchClose = findViewById(R.id.ic_search_close);
        android.widget.EditText etSearch = findViewById(R.id.et_search);

        if (icSearchContainer != null) {
            icSearchContainer.setOnClickListener(v -> {
                lyTitle.setVisibility(android.view.View.GONE);
                icSearchContainer.setVisibility(android.view.View.GONE);
                lySearch.setVisibility(android.view.View.VISIBLE);
                etSearch.requestFocus();
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            });
        }

        if (icSearchClose != null) {
            icSearchClose.setOnClickListener(v -> {
                etSearch.setText("");
                lySearch.setVisibility(android.view.View.GONE);
                lyTitle.setVisibility(android.view.View.VISIBLE);
                icSearchContainer.setVisibility(android.view.View.VISIBLE);
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            });
        }

        if (etSearch != null) {
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (allCategories == null) return;
                    currentTabList.clear();
                    String query = s.toString().toLowerCase().trim();
                    if (query.isEmpty()) {
                        int maxItems = Math.min(6, allCategories.size());
                        for (int i = 0; i < maxItems; i++) {
                            currentTabList.add(allCategories.get(i));
                        }
                    } else {
                        for (Leaflet_PosterModel cat : allCategories) {
                            if (cat.getCat_name().toLowerCase().contains(query)) {
                                currentTabList.add(cat);
                            }
                        }
                    }
                    if (vp != null && vp.getAdapter() != null) {
                        vp.getAdapter().notifyDataSetChanged();
                    }
                    com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                    if (tabs != null) tabs.notifyDataSetChanged();
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {}
            });
        }
        
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_TEMPLATES);

        // Load Banner Ad
        android.widget.RelativeLayout rl_ad = findViewById(R.id.rl_ad);
        if (rl_ad != null) {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass pref = new com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass(this);
            if (pref.isBannerAdEnabled("templates")) {
                rl_ad.setVisibility(android.view.View.VISIBLE);
                if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils.isNetworkAvailable(this)) {
                    com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(this, rl_ad, "templates");
                }
            } else {
                rl_ad.removeAllViews();
                rl_ad.setVisibility(android.view.View.GONE);
            }
        }
    }

    public void openAllCategories() {
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        if (allCategories == null) return;
        java.util.ArrayList<String> catNames = new java.util.ArrayList<>();
        for (Leaflet_PosterModel model : allCategories) {
            catNames.add(model.getCat_name());
        }
        com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.allCategoriesList = catNames;
        
        androidx.viewpager.widget.ViewPager viewPager = findViewById(R.id.viewPager);
        if (viewPager != null && viewPager.getAdapter() != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < currentTabList.size()) {
                com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.currentlySelectedCategory = 
                    currentTabList.get(currentItem).getCat_name();
            }
        }

        android.content.Intent intent = new android.content.Intent(this, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_AllCategoriesActivity.class);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2002 && resultCode == RESULT_OK && data != null) {
            int catId = data.getIntExtra("cat_id", 0);
            int postId = data.getIntExtra("post_id", 0);
            boolean isPremium = data.getBooleanExtra("is_premium", false);
            if (catId != 0 && postId != 0) {
                onPosterClick(catId, postId, isPremium);
            }
            return;
        }
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String selectedCatName = data.getStringExtra("selected_category_name");
            if (selectedCatName != null && allCategories != null && currentTabList != null) {
                int targetIndex = -1;
                for (int i = 0; i < allCategories.size(); i++) {
                    if (selectedCatName.equalsIgnoreCase(allCategories.get(i).getCat_name())) {
                        targetIndex = i;
                        break;
                    }
                }
                
                if (targetIndex != -1) {
                    while (currentTabList.size() <= targetIndex) {
                        currentTabList.add(allCategories.get(currentTabList.size()));
                    }
                    
                    androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
                    if (vp != null && vp.getAdapter() != null) {
                        vp.getAdapter().notifyDataSetChanged();
                        com.online.flyer.design.postermaker.Leaflet_view.Leaflet_PagerSlidingTabStrip tabs = findViewById(R.id.pagerSlidingTabStrip);
                        if (tabs != null) tabs.notifyDataSetChanged();
                        vp.setCurrentItem(targetIndex);
                    }
                }
            }
        }
    }

    public void deleteFromExternalStorage() {
        new Thread(() -> {
            String fullPath = "/DCIM/BM Infotech/Festival Adbanao/.poster_data/.temp/.framedata/";
            File dir = new File(Environment.getExternalStorageDirectory() + fullPath);
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        new File(dir, children[i]).delete();
                    }
                }
            }
        }).start();
    }


    private void findByID() {
        preferenceClass = new Leaflet_PreferenceClass(this);
        templateSelectionController = new Leaflet_TemplateSelectionController(this, getSupportFragmentManager(), preferenceClass);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
            if (local_permission) {
                loadPoster(preferenceClass.getDataType("field_0"), cat_id, post_id);
                local_permission = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "template_selection");
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_AppOpenManager.getLastAdDismissedTime() < 800) {
            return;
        }
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackPress(this, "template_selection", "back_button");
        if (getIntent().getBooleanExtra("auto_load", false)) {
            com.online.flyer.design.postermaker.Leaflet_MyApplication.showInterstitialAd(this, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                @Override
                public void onAdClose() {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        } else {
            com.online.flyer.design.postermaker.Leaflet_MyApplication.showInterstitialAd(this, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                @Override
                public void onAdClose() {
                    android.content.Intent intent = new android.content.Intent(Leaflet_TemplateSelectionActivity.this, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity.class);
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    public void onPosterClick(int cat_id, int post_id, boolean premium) {
        if (isLoadingPoster || com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logTemplateSelected(this, String.valueOf(post_id), String.valueOf(cat_id), !premium, "template_click");
        if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PERMISSION_GRANTED || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            local_permission = true;
            this.cat_id = cat_id;
            this.post_id = post_id;
//                        requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, PERMISSION_GRANTED);
//                    } else {
//                        MailER_MaterialDialogUtils.getInstance().PermissionDialog(this);
//                    }
//                    return;
//                }
//            }
            if (premium) {
                this.cat_id = cat_id;
                this.post_id = post_id;

                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogShown(this, "template_unlock");
                Leaflet_MaterialDialogUtils.getInstance().rewardDialog(this, "Use Template(One Time)", "Use premium template by watching Ads", materialDialog -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogAction(this, "template_unlock", "watch_video");
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    Leaflet_PreferenceClass preferenceClass = new Leaflet_PreferenceClass(this);
                    if (preferenceClass.getDataType("PremiumAdType") != null && preferenceClass.getDataType("PremiumAdType").equals("Reward")) {
                        Leaflet_RewardVideoManager.showRewardVideoAd(Leaflet_TemplateSelectionActivity.this, new Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardAdCompleted(Leaflet_TemplateSelectionActivity.this, "template_unlock");
                                isRewarded = true;
                                templateSelectionController.startMaterialDialog();
                                loadPoster(preferenceClass.getDataType("field_0"), cat_id, post_id);
                            }

                            @Override
                            public void onAdFailedToEarnReward() {
                                if (getIntent().getBooleanExtra("auto_load", false)) {
                                    finish();
                                }
                            }
                        });
                    } else {
                        Leaflet_MyApplication.showUnlockInterstitialAd(Leaflet_TemplateSelectionActivity.this, new Leaflet_InterstitialAdManager.OnAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardAdCompleted(Leaflet_TemplateSelectionActivity.this, "template_unlock");
                                isRewarded = true;
                                templateSelectionController.startMaterialDialog();
                                loadPoster(preferenceClass.getDataType("field_0"), cat_id, post_id);
                            }

                            @Override
                            public void onAdFailedToEarnReward() {
                                if (getIntent().getBooleanExtra("auto_load", false)) {
                                    finish();
                                }
                            }
                        });
                    }
                }, materialDialog -> {
                    com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logRewardDialogAction(this, "template_unlock", "cancel");
                    isLoadingPoster = false;
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                    if (getIntent().getBooleanExtra("auto_load", false)) {
                        finish();
                    }
                });

            } else {
                templateSelectionController.startMaterialDialog();
                loadPoster(preferenceClass.getDataType("field_0"), cat_id, post_id);
            }
        } else {
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(Leaflet_TemplateSelectionActivity.this, "Make sure you are connected to internet!!");
            if (getIntent().getBooleanExtra("auto_load", false)) {
                finish();
            }
        }
    }

    public void loadPoster(String key, final int cat_id, final int pos_id) {
        if (isLoadingPoster) return;
        isLoadingPoster = true;
        if (cat_id == 0) {
            isLoadingPoster = false;
            templateSelectionController.dismissMaterialDialog();
            Leaflet_MaterialDialogUtils.getInstance().errorDialog2(Leaflet_TemplateSelectionActivity.this, "This liked poster is from an older version. Please re-like it from the Templates screen!");
            return;
        }

        String requestUrl = preferenceClass.getDataType("field_1") + preferenceClass.getDataType("field_34") + preferenceClass.getDataType("field_36");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                int error = jsonObject.getInt(preferenceClass.getDataType("field_3"));
                if (error == 0 || error == 404) {
                    isLoadingPoster = false;
                    templateSelectionController.dismissMaterialDialog();
                    Leaflet_MaterialDialogUtils.getInstance().errorDialog2(Leaflet_TemplateSelectionActivity.this, getResources().getString(R.string.something_went_wrong));
                    return;
                }

                if (error == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray(preferenceClass.getDataType("field_4"));
                    if (jsonArray.length() == 0) {
                        Leaflet_MaterialDialogUtils.getInstance().errorDialog2(Leaflet_TemplateSelectionActivity.this, getResources().getString(R.string.something_went_wrong));
                    } else {
                        new Leaflet_GetPosDetail(preferenceClass, jsonArray, new Leaflet_GetPosDetail.OnGetDataListener() {
                            @Override
                            public void onGetDataComplete(ArrayList<Leaflet_TemplateModel> posterCos) {
                                Leaflet_TemplateSelectionActivity.this.templateModels = posterCos;
                                sticker_model = posterCos.get(0).getSticker_model();
                                text_model = posterCos.get(0).getText_model();
                                stringArrayList.clear();
                                stringArrayList.add(posterCos.get(0).getBack_image());

                                for (int i = 0; i < sticker_model.size(); i++) {
                                    if (!sticker_model.get(i).getSt_image().equals("")) {
                                        stringArrayList.add(preferenceClass.getDataType("field_1") + "/" + sticker_model.get(i).getSt_image());
                                    }
                                }

                                if (preferenceClass.getInt("download") == 0) {
                                    for (int i = 0; i < text_model.size(); i++) {
                                        String fontFamily = text_model.get(i).getFont_family();
                                        if (fontFamily != null && !fontFamily.trim().isEmpty() && !com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils.isAssetExists(fontFamily, Leaflet_TemplateSelectionActivity.this)) {
                                            stringArrayList.add(preferenceClass.getDataType("field_37") + fontFamily);
                                        }
                                    }
                                }

                                new Leaflet_DlTemplateAsync(Leaflet_TemplateSelectionActivity.this, stringArrayList, new Leaflet_DlTemplateAsync.OnDownloadTemplateListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        templateSelectionController.dismissMaterialDialog();
                                        if (Leaflet_NetworkUtils.isNetworkAvailable(Leaflet_TemplateSelectionActivity.this)) {
                                            if (isRewarded) {
                                                startIntent();
                                                isRewarded = false;
                                                return;
                                            }
                                            Leaflet_MyApplication.showInterstitialAd(Leaflet_TemplateSelectionActivity.this, Leaflet_TemplateSelectionActivity.this::startIntent);
                                            templateSelectionController.dismissMaterialDialog();
                                        } else {
                                            Leaflet_MaterialDialogUtils.getInstance().errorDialog3(Leaflet_TemplateSelectionActivity.this, "Make sure you are connected to internet!!");
                                        }
                                    }

                                    @Override
                                    public void onError() {
                                        isLoadingPoster = false;
                                        templateSelectionController.dismissMaterialDialog();
                                        Leaflet_MaterialDialogUtils.getInstance().errorDialog3(Leaflet_TemplateSelectionActivity.this, getResources().getString(R.string.something_went_wrong));
                                    }
                                }).execute();

                            }

                            @Override
                            public void onError() {
                                templateSelectionController.dismissMaterialDialog();
                                Leaflet_MaterialDialogUtils.getInstance().errorDialog3(Leaflet_TemplateSelectionActivity.this, getResources().getString(R.string.something_went_wrong));
                            }
                        }).execute();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, error -> {
            templateSelectionController.dismissMaterialDialog();
            Leaflet_MaterialDialogUtils.getInstance().errorDialog(Leaflet_TemplateSelectionActivity.this, getResources().getString(R.string.something_went_wrong));
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> postMap = new HashMap<>();
                postMap.put(preferenceClass.getDataType("field_47"), key);
                postMap.put(preferenceClass.getDataType("field_48"), "1");
                postMap.put(preferenceClass.getDataType("field_46"), String.valueOf(cat_id));
                postMap.put(preferenceClass.getDataType("field_10"), String.valueOf(pos_id));
                return postMap;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
//        templateSelectionController.dismissMaterialDialog();
    }

    private void startIntent() {
        isLoadingPoster = false;
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        Intent intent = new Intent(this, Leaflet_PosterEditActivity.class);
        intent.putParcelableArrayListExtra("template", templateModels);
        intent.putParcelableArrayListExtra("sticker", sticker_model);
        intent.putParcelableArrayListExtra("text", text_model);
        intent.putExtra("loadUserFrame", false);
        intent.putExtra("Temp_Type", "MY_TEMP");
        if (isRewarded) {
            intent.putExtra("show_celebration", true);
            intent.putExtra("celebration_message", "🎉 Template Unlocked!");
        }
        startActivity(intent);
        templateSelectionController.dismissMaterialDialog();
        if (getIntent().getBooleanExtra("auto_load", false)) {
            finish();
        }
    }
}
