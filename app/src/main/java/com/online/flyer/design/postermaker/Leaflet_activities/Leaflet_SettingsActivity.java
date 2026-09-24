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
    public void onBackPressed() {
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

        findViewById(R.id.ic_back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.tv_my_creations).setOnClickListener(v -> {
            Leaflet_MyApplication.showInterstitialAd(this, () -> {
                startActivity(new Intent(Leaflet_SettingsActivity.this, Leaflet_MyCreationActivity.class));
            });
        });

        findViewById(R.id.tv_share_app).setOnClickListener(v -> {
            Leaflet_ShareUtils.onShare(Leaflet_SettingsActivity.this);
        });

        findViewById(R.id.tv_rate_app).setOnClickListener(v -> {
            Leaflet_ShareUtils.rateUs(Leaflet_SettingsActivity.this);
        });

        findViewById(R.id.tv_privacy_policy).setOnClickListener(v -> {
            Leaflet_ShareUtils.onPrivacyPolicy(Leaflet_SettingsActivity.this);
        });

        findViewById(R.id.tv_liked_posters).setOnClickListener(v -> {
            Intent intent = new Intent(Leaflet_SettingsActivity.this, Leaflet_FavoritesActivity.class);
            intent.putExtra(Leaflet_FavoritesActivity.EXTRA_TYPE, Leaflet_FavoritesActivity.TYPE_POSTERS);
            startActivity(intent);
        });

        findViewById(R.id.tv_liked_backgrounds).setOnClickListener(v -> {
            Intent intent = new Intent(Leaflet_SettingsActivity.this, Leaflet_FavoritesActivity.class);
            intent.putExtra(Leaflet_FavoritesActivity.EXTRA_TYPE, Leaflet_FavoritesActivity.TYPE_BACKGROUNDS);
            startActivity(intent);
        });
        
        com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.setupBottomNav(this, com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NavUtils.TAB_SETTINGS);
    }
}
