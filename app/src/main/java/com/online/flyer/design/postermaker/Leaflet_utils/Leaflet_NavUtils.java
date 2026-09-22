package com.online.flyer.design.postermaker.Leaflet_utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.online.flyer.design.postermaker.R;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_BackgroundSelectionActivity;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_PosterMainActivity;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_SettingsActivity;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_TemplateSelectionActivity;
import com.online.flyer.design.postermaker.Leaflet_MyApplication;

public class Leaflet_NavUtils {

    public static final int TAB_HOME = 0;
    public static final int TAB_TEMPLATES = 1;
    public static final int TAB_BACKGROUNDS = 2;
    public static final int TAB_SETTINGS = 3;

    public static void setupBottomNav(Activity activity, int activeTab) {
        View navHome = activity.findViewById(R.id.nav_home);
        View navTemplates = activity.findViewById(R.id.nav_templates);
        View navBackgrounds = activity.findViewById(R.id.nav_backgrounds);
        View navSettings = activity.findViewById(R.id.nav_settings);
        View navFab = activity.findViewById(R.id.nav_fab);

        if (navHome == null) return;

        ImageView icHome = activity.findViewById(R.id.nav_home_ic);
        TextView tvHome = activity.findViewById(R.id.nav_home_tv);
        ImageView icTemplates = activity.findViewById(R.id.nav_templates_ic);
        TextView tvTemplates = activity.findViewById(R.id.nav_templates_tv);
        ImageView icBackgrounds = activity.findViewById(R.id.nav_backgrounds_ic);
        TextView tvBackgrounds = activity.findViewById(R.id.nav_backgrounds_tv);
        ImageView icSettings = activity.findViewById(R.id.nav_settings_ic);
        TextView tvSettings = activity.findViewById(R.id.nav_settings_tv);

        int activeColor = Color.parseColor("#2563EB");
        int inactiveColor = Color.parseColor("#9CA3AF");

        icHome.setImageTintList(ColorStateList.valueOf(activeTab == TAB_HOME ? activeColor : inactiveColor));
        tvHome.setTextColor(activeTab == TAB_HOME ? activeColor : inactiveColor);

        icTemplates.setImageTintList(ColorStateList.valueOf(activeTab == TAB_TEMPLATES ? activeColor : inactiveColor));
        tvTemplates.setTextColor(activeTab == TAB_TEMPLATES ? activeColor : inactiveColor);

        icBackgrounds.setImageTintList(ColorStateList.valueOf(activeTab == TAB_BACKGROUNDS ? activeColor : inactiveColor));
        tvBackgrounds.setTextColor(activeTab == TAB_BACKGROUNDS ? activeColor : inactiveColor);

        icSettings.setImageTintList(ColorStateList.valueOf(activeTab == TAB_SETTINGS ? activeColor : inactiveColor));
        tvSettings.setTextColor(activeTab == TAB_SETTINGS ? activeColor : inactiveColor);

        navHome.setOnClickListener(v -> navigate(activity, activeTab, TAB_HOME, Leaflet_PosterMainActivity.class));
        navTemplates.setOnClickListener(v -> navigate(activity, activeTab, TAB_TEMPLATES, Leaflet_TemplateSelectionActivity.class));
        navBackgrounds.setOnClickListener(v -> navigate(activity, activeTab, TAB_BACKGROUNDS, Leaflet_BackgroundSelectionActivity.class));
        navSettings.setOnClickListener(v -> navigate(activity, activeTab, TAB_SETTINGS, Leaflet_SettingsActivity.class));

        navFab.setOnClickListener(v -> {
            Intent intent = new Intent(activity, com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_CreateCustomActivity.class);
            activity.startActivity(intent);
        });
    }

    private static void navigate(Activity currentActivity, int currentTab, int targetTab, Class<?> targetClass) {
        if (currentTab == targetTab) return;

        Leaflet_MyApplication.showInterstitialAd(currentActivity, () -> {
            Intent intent = new Intent(currentActivity, targetClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            currentActivity.startActivity(intent);
            currentActivity.overridePendingTransition(0, 0);
        });
    }
}
