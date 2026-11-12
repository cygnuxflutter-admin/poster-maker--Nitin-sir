package com.online.flyer.design.postermaker.Leaflet_activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_controller.Leaflet_BGSelectionController;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_FileUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_MaterialDialogUtils;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NetworkUtils;
import android.view.View;
import android.widget.RelativeLayout;
import com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_PreferenceClass;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class Leaflet_CreateCustomActivity extends AppCompatActivity {

    private Leaflet_BGSelectionController bgSelectionController;
    private String path;

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "create_custom");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_create_custom);

        Leaflet_PreferenceClass preferenceClass = new Leaflet_PreferenceClass(this);
        // We pass "create" so we don't return to caller but launch editor
        bgSelectionController = new Leaflet_BGSelectionController(this, getSupportFragmentManager(), preferenceClass, "create");

        findViewById(R.id.ic_back).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            onBackPressed();
        });

        findViewById(R.id.card_gallery).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackgroundSourceSelected(this, "gallery");
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            bgSelectionController.openPhotoGallery();
        });

        findViewById(R.id.card_camera).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackgroundSourceSelected(this, "camera");
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            if (!checkPermission()) {
                try {
                    requestPermission();
                    Leaflet_MaterialDialogUtils.getInstance().PermissionDialog(this);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                }
            } else {
                bgSelectionController.openCamera();
            }
        });

        findViewById(R.id.card_color).setOnClickListener(v -> {
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackgroundSourceSelected(this, "solid_color");
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            bgSelectionController.openColorDialog();
        });

        loadAds(preferenceClass);
    }

    private void loadAds(Leaflet_PreferenceClass pref) {
        RelativeLayout nativeContainer = findViewById(R.id.native_ad_container_create_custom);
        RelativeLayout bannerContainer = findViewById(R.id.rl_ad);

        boolean isNativeEnabled = pref.isCreateCustomNativeAdEnabled();
        if (isNativeEnabled && nativeContainer != null) {
            int customHeight = pref.getCreateCustomNativeAdHeight();
            if (customHeight > 0) {
                int heightPx = (int) (customHeight * getResources().getDisplayMetrics().density);
                if (nativeContainer.getLayoutParams() != null) {
                    nativeContainer.getLayoutParams().height = heightPx;
                    nativeContainer.requestLayout();
                }
            } else {
                if (nativeContainer.getLayoutParams() != null) {
                    nativeContainer.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
                    nativeContainer.requestLayout();
                }
            }

            String nativeAdId = pref.getCreateCustomNativeAdId();
            boolean showShimmerOnFail = pref.shouldShowNativeShimmerOnFail("create_custom");

            nativeContainer.setVisibility(View.VISIBLE);
            com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil util =
                    new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil(this);
            util.fillAdmobNativeAd(nativeContainer, nativeAdId, showShimmerOnFail, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_NativeAdUtil.OnNativeAdListener() {
                @Override
                public void onAdLoaded() {
                    if (bannerContainer != null) {
                        bannerContainer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAdFailed() {
                    if (!showShimmerOnFail && nativeContainer != null) {
                        nativeContainer.setVisibility(View.GONE);
                    }
                    if (bannerContainer != null && pref.isBannerAdEnabled("create_custom")) {
                        bannerContainer.setVisibility(View.VISIBLE);
                        com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(Leaflet_CreateCustomActivity.this, bannerContainer, "create_custom");
                    }
                }
            });
        } else {
            if (nativeContainer != null) {
                nativeContainer.removeAllViews();
                nativeContainer.setVisibility(View.GONE);
            }
            if (bannerContainer != null && pref.isBannerAdEnabled("create_custom")) {
                bannerContainer.setVisibility(View.VISIBLE);
                com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_LoadAds.loadAdmobBannerAd(this, bannerContainer, "create_custom");
            } else if (bannerContainer != null) {
                bannerContainer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackPress(this, "create_custom", "back_button");
        com.online.flyer.design.postermaker.Leaflet_MyApplication.showInterstitialAd(this, new com.online.flyer.design.postermaker.Leaflet_adManager.Leaflet_InterstitialAdManager.OnAdLoadInterface() {
            @Override
            public void onAdClose() {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == bgSelectionController.CAMERA_INTENT) {
            Uri selectedImage = Uri.fromFile(bgSelectionController.camera_file);
            bgSelectionController.startCrop(selectedImage);
        }
        if (resultCode == RESULT_OK && requestCode == bgSelectionController.GALLERY_INTENT && data != null) {
            Uri selectedImage = data.getData();
            assert selectedImage != null;
            bgSelectionController.startCrop(selectedImage);
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            assert data != null;
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                path = resultUri.toString();
                if (Leaflet_NetworkUtils.isNetworkAvailable(this)) {
                    Leaflet_MyApplication.showInterstitialAd(this, this::startIntent);
                } else {
                    Toast.makeText(this, "Make sure you are connected to internet!!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
            final Throwable resultUri = UCrop.getError(data);
            Toast.makeText(this, resultUri.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startIntent() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logEditorOpened(this, "custom_image", true);
        if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
        Intent intent = new Intent(this, Leaflet_PosterEditActivity.class);
        intent.putExtra("bg_path", path);
        intent.putExtra("loadUserFrame", true);
        intent.putExtra("Temp_Type", "MY_TEMP");
        startActivity(intent);
        finish();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA
            }, 1);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE, 
                    Manifest.permission.CAMERA
            }, 1);
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            return result1 == 0 && result2 == 0;
        } else {
            int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            return result == 0 && result1 == 0 && result2 == 0;
        }
    }
}
