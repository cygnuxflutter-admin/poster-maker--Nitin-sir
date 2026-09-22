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
    private ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> currentTabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_background_selection);

        findByID();

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.ic_gallery).setOnClickListener(v -> bgSelectionController.openPhotoGallery());

        findViewById(R.id.ic_color).setOnClickListener(v -> bgSelectionController.openColorDialog());


        android.view.View lyTitle = findViewById(R.id.ly_title);
        android.view.View lySearch = findViewById(R.id.ly_search);
        android.view.View icSearchContainer = findViewById(R.id.ic_search_container);
        android.view.View icSearchClose = findViewById(R.id.ic_search_close);
        android.widget.EditText etSearch = findViewById(R.id.et_search);

        if (getIntent().getBooleanExtra("auto_load", false)) {
            findViewById(R.id.viewPager).setVisibility(android.view.View.GONE);
            findViewById(R.id.pagerSlidingTabStrip).setVisibility(android.view.View.GONE);
            String autoBgPath = getIntent().getStringExtra("bg_image");
            boolean premium = getIntent().getBooleanExtra("premium", false);
            onPosterClick(autoBgPath, premium);
        }

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
    }

    private void findByID() {
        Leaflet_PreferenceClass preferenceClass = new Leaflet_PreferenceClass(this);
        mode = getIntent().getStringExtra("mode");
        bgSelectionController = new Leaflet_BGSelectionController(this, getSupportFragmentManager(), preferenceClass, mode);
    }

    public void onCategoriesLoaded(ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> categories, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel> tabList) {
        this.allCategories = categories;
        this.currentTabList = tabList;
        
        androidx.viewpager.widget.ViewPager vp = findViewById(R.id.viewPager);
        if (vp != null) {
            vp.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int position) {
                    // Wait for manual swipe
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
    }

    public void openAllCategories() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

                    Leaflet_MyApplication.showInterstitialAd(this, this::startIntent);

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
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPosterClick(String path, boolean premium) {
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
            if (premium) {
                Leaflet_MaterialDialogUtils.getInstance().rewardDialog(this, "Use Background(One Time)", "Use premium background by watching Ads", materialDialog -> {
//                    bgSelectionController.startMaterialDialog();
                    Leaflet_PreferenceClass preferenceClass = new Leaflet_PreferenceClass(this);
                    if(preferenceClass.getDataType("PremiumAdType")!=null && preferenceClass.getDataType("PremiumAdType").equals("Reward")) {
                        Leaflet_RewardVideoManager.showRewardVideoAd(Leaflet_BackgroundSelectionActivity.this, new Leaflet_InterstitialAdManager.OnRewardAdLoadInterface() {
                            @Override
                            public void onAdClose() {
                                isRewarded = true;
                                downloadTask(strings);
                            }
                        });
                    }else {
                        Leaflet_MyApplication.showInterstitialAdWithOutCount(Leaflet_BackgroundSelectionActivity.this, () ->{
                            isRewarded = true;
                            downloadTask(strings);
                        });
                    }
//                    MyApplication.showInterstitialAd(this,() -> downloadTask(strings));
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                }, materialDialog -> {
                    if (materialDialog != null && materialDialog.isShowing())
                        materialDialog.dismiss();
                });
            } else {
                downloadTask(strings);
            }
        } else {
            Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadTask(ArrayList<String> strings) {
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
                Toast.makeText(Leaflet_BackgroundSelectionActivity.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                bgSelectionController.dismissMaterialDialog();
            }
        }).execute();
    }

    private void startIntent() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, Leaflet_PosterEditActivity.class);
            intent.putExtra("bg_path", path);
            intent.putExtra("loadUserFrame", true);
            intent.putExtra("Temp_Type", "MY_TEMP");
            startActivity(intent);
            if (getIntent().getBooleanExtra("auto_load", false)) {
                finish();
            }
        }, 1500);
    }

    private void setPosterIntent() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent();
            intent.putExtra("local", local);
            intent.putExtra("bg_image", Leaflet_FileUtils.getFile(Leaflet_BackgroundSelectionActivity.this, path));
            setResult(RESULT_OK, intent);
            finish();
        }, 1500);
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
