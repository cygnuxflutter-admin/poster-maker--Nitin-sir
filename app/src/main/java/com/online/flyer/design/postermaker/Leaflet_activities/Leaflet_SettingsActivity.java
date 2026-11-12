package com.online.flyer.design.postermaker.Leaflet_activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ShareUtils;
import com.online.flyer.design.postermaker.R;

public class Leaflet_SettingsActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logScreenView(this, "settings");
    }

    @Override
    public void onBackPressed() {
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_AnalyticsUtils.logBackPress(this, "settings", "back_button");
        android.content.Intent intent = new android.content.Intent(this, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaflet_activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#F4F6F9"));
        }

        findViewById(R.id.ic_back).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            onBackPressed();
        });

        findViewById(R.id.tv_my_creations).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                startActivity(new Intent(Leaflet_SettingsActivity.this, Leaflet_MyCreationActivity.class));
            });
        });

        findViewById(R.id.tv_share_app).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Leaflet_ShareUtils.onShare(Leaflet_SettingsActivity.this);
        });

        findViewById(R.id.tv_rate_app).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_RatingManager.showRatingDialog(Leaflet_SettingsActivity.this, "settings_rate_us");
        });

        findViewById(R.id.tv_privacy_policy).setOnClickListener(v -> {
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Leaflet_ShareUtils.onPrivacyPolicy(Leaflet_SettingsActivity.this);
        });

        findViewById(R.id.tv_liked_posters).setOnClickListener(v -> { 
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Leaflet_MyApplication.showInterstitialAd(this, () -> { 
                Intent intent = new Intent(Leaflet_SettingsActivity.this, Leaflet_FavoritesActivity.class); 
                intent.putExtra(Leaflet_FavoritesActivity.EXTRA_TYPE, Leaflet_FavoritesActivity.TYPE_POSTERS); 
                startActivity(intent); 
            }); 
        });

        findViewById(R.id.tv_liked_backgrounds).setOnClickListener(v -> { 
            if (com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_ClickUtils.isFastDoubleClick(800)) return;
            Leaflet_MyApplication.showInterstitialAd(this, () -> { 
                Intent intent = new Intent(Leaflet_SettingsActivity.this, Leaflet_FavoritesActivity.class); 
                intent.putExtra(Leaflet_FavoritesActivity.EXTRA_TYPE, Leaflet_FavoritesActivity.TYPE_BACKGROUNDS); 
                startActivity(intent); 
            }); 
        });
        
        android.widget.TextView tvAppVersion = findViewById(R.id.tv_app_version);
        if (tvAppVersion != null) {
            String versionName;
            try {
                versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (Exception e) {
                versionName = com.online.flyer.design.postermaker.BuildConfig.VERSION_NAME;
            }
            tvAppVersion.setText("Version " + versionName);
        }

        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_SETTINGS);
    }
}
