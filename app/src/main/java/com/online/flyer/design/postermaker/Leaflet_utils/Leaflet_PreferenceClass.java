package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class Leaflet_PreferenceClass {

    private final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private final SharedPreferences prefs;

    public Leaflet_PreferenceClass(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstTimeLaunch() {
        return prefs.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        prefs.edit().putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime).apply();
    }

    public void setRateSubmited(String type, Boolean value) {
        prefs.edit().putBoolean(type, value).apply();
    }

    public Boolean getRateSubmited(String type) {
        if (prefs != null) {
            return prefs.getBoolean(type, false);
        }
        return false;
    }

    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (prefs != null) {
            return prefs.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public void setDataType(String type, String value) {
        prefs.edit().putString(type, value).apply();
    }

    public String getDataType(String type) {
        return getDataType(type, "");
    }

    public String getDataType(String type, String defaultValue) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String val = rc.getString(type);
            if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                return val.trim();
            }
        } catch (Exception ignored) {}
        if (prefs != null) {
            return prefs.getString(type, defaultValue);
        }
        return defaultValue;
    }

    public void setAdsId(String type, String value) {
        prefs.edit().putString(type, value).apply();
    }

    public String getAdsId(String type) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String rcId = rc.getString(type);
            if (rcId != null && !rcId.trim().isEmpty() && !rcId.equals("null") && !rcId.contains("xxxx")) {
                return rcId.trim();
            }
        } catch (Exception ignored) {}

        if (prefs != null) {
            return prefs.getString(type, "");
        }
        return "";
    }

    public void setAdsStatus(String type, int value) {
        prefs.edit().putInt(type, value).apply();
    }

    public int getAdsStatus(String type) {
        if ("interstitialAdStatus".equalsIgnoreCase(type) || "interstitialAdCount".equalsIgnoreCase(type) || "interstitial_ad_count".equalsIgnoreCase(type) || "interstitial_count".equalsIgnoreCase(type)) {
            return getGlobalInterstitialCount();
        }
        if ("EditScreenAdCount".equalsIgnoreCase(type) || "edit_interstitial_count".equalsIgnoreCase(type)) {
            return getEditScreenInterstitialCount();
        }
        return getInt(type, 1);
    }

    public void setInt(String type, int value) {
        prefs.edit().putInt(type, value).apply();
    }

    public int getInt(String type) {
        return getInt(type, 0);
    }

    public int getInt(String type, int def) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String rcVal = rc.getString(type);
            if (rcVal != null && !rcVal.trim().isEmpty()) {
                String val = rcVal.trim();
                if ("true".equalsIgnoreCase(val)) return 1;
                if ("false".equalsIgnoreCase(val)) return 0;
                try {
                    return Integer.parseInt(val);
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}

        if (prefs != null) {
            return prefs.getInt(type, def);
        }
        return def;
    }

    public void setFirstDate(String firstDate) {
        prefs.edit().putString("firstDate", firstDate).apply();
    }

    public String getFirstDate() {
        return prefs.getString("firstDate", null);
    }

    public void setDecryptionType(int type) {
        prefs.edit().putInt("decryptionType", type).apply();
    }

    public int getDecryptionType() {
        return prefs.getInt("decryptionType", 0);
    }

    // =========================================================================
    // Firebase Remote Config Screen & Ad Dynamic Management
    // =========================================================================

    public JSONObject getScreenAdConfigJson(String screenType) {
        if (screenType == null) return null;
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            
            String primaryKey = screenType.toLowerCase();
            String altKey = primaryKey.endsWith("s") ? primaryKey.substring(0, primaryKey.length() - 1) : (primaryKey + "s");

            // 1. Direct parameter lookup
            String jsonStr = rc.getString(primaryKey);
            if (jsonStr == null || jsonStr.trim().isEmpty() || !jsonStr.trim().startsWith("{")) {
                jsonStr = rc.getString(altKey);
            }

            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }

            // 2. Nested under root parameters
            String[] rootKeys = new String[]{"ads_config", "screens_config", "ad_config", "screens", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has(primaryKey)) {
                        return rootObj.optJSONObject(primaryKey);
                    }
                    if (rootObj.has(altKey)) {
                        return rootObj.optJSONObject(altKey);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isGridAdEnabled(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null && nativeObj.has("enabled")) {
                    return nativeObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        if ("exit_dialog".equalsIgnoreCase(screenType) || "exit".equalsIgnoreCase(screenType)) {
            if (getInt("show_native_exit", 1) == 0) return false;
        }

        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if ("false".equalsIgnoreCase(rc.getString("show_grid_native_ad"))
                    || "false".equalsIgnoreCase(rc.getString("native_ad_enabled"))) {
                return false;
            }
            if (("template".equalsIgnoreCase(screenType) || "templates".equalsIgnoreCase(screenType))
                    && "false".equalsIgnoreCase(rc.getString("show_template_native_ad"))) {
                return false;
            }
            if (("background".equalsIgnoreCase(screenType) || "backgrounds".equalsIgnoreCase(screenType))
                    && "false".equalsIgnoreCase(rc.getString("show_background_native_ad"))) {
                return false;
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getGridFirstAdAfter(String screenType) {
        int count = 0;
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null) {
                    if (nativeObj.has("first_ad_after")) {
                        count = nativeObj.optInt("first_ad_after", 0);
                    }
                    if (count <= 0 && nativeObj.has("repeat_ad_after")) {
                        count = nativeObj.optInt("repeat_ad_after", 0);
                    }
                }
            }
        } catch (Exception ignored) {}

        if (count <= 0) {
            count = getGridAdInterval(screenType);
        }

        if (count > 0 && count % 2 != 0) {
            count++;
        }
        return count > 0 ? count : 4;
    }

    public int getGridRepeatAdAfter(String screenType) {
        int count = 0;
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null) {
                    if (nativeObj.has("repeat_ad_after")) {
                        count = nativeObj.optInt("repeat_ad_after", 0);
                    }
                    if (count <= 0 && nativeObj.has("first_ad_after")) {
                        count = nativeObj.optInt("first_ad_after", 0);
                    }
                }
            }
        } catch (Exception ignored) {}

        if (count <= 0) {
            count = getGridAdInterval(screenType);
        }

        if (count > 0 && count % 2 != 0) {
            count++;
        }
        return count > 0 ? count : 4;
    }

    public String getGridNativeAdId(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null && nativeObj.has("ad_id")) {
                    String id = nativeObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("NativeUnitID");
    }

    public boolean isBannerAdEnabled(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("banner_ad")) {
                JSONObject bannerObj = screenObj.optJSONObject("banner_ad");
                if (bannerObj != null && bannerObj.has("enabled")) {
                    return bannerObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        if ("templates".equalsIgnoreCase(screenType) || "template".equalsIgnoreCase(screenType)) {
            if (getInt("show_banner_templates", 1) == 0) return false;
        } else if ("backgrounds".equalsIgnoreCase(screenType) || "background".equalsIgnoreCase(screenType)) {
            if (getInt("show_banner_background", 1) == 0) return false;
        } else if ("home".equalsIgnoreCase(screenType)) {
            if (getInt("show_banner_home", 1) == 0) return false;
        } else if ("share".equalsIgnoreCase(screenType)) {
            if (getInt("show_banner_share", 1) == 0) return false;
        }

        String bannerId = getBannerAdId(screenType);
        return bannerId != null && !bannerId.trim().isEmpty() && !bannerId.equals("null");
    }

    public String getBannerAdId(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("banner_ad")) {
                JSONObject bannerObj = screenObj.optJSONObject("banner_ad");
                if (bannerObj != null && bannerObj.has("ad_id")) {
                    String id = bannerObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("BannerAdunitID");
    }

    public boolean shouldShowNativeShimmerOnFail(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null) {
                if (screenObj.has("native_ad")) {
                    JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                    if (nativeObj != null) {
                        if (nativeObj.has("native_show_shimmer_on_fail")) {
                            return nativeObj.optBoolean("native_show_shimmer_on_fail", false);
                        }
                        if (nativeObj.has("show_shimmer_on_fail")) {
                            return nativeObj.optBoolean("show_shimmer_on_fail", false);
                        }
                    }
                }
                if (screenObj.has("native_show_shimmer_on_fail")) {
                    return screenObj.optBoolean("native_show_shimmer_on_fail", false);
                }
                if (screenObj.has("show_shimmer_on_fail")) {
                    return screenObj.optBoolean("show_shimmer_on_fail", false);
                }
            }
        } catch (Exception ignored) {}

        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String val = rc.getString("native_show_shimmer_on_fail");
            if (val != null && !val.trim().isEmpty()) {
                return "true".equalsIgnoreCase(val.trim());
            }
            String val2 = rc.getString("show_shimmer_on_fail");
            if (val2 != null && !val2.trim().isEmpty()) {
                return "true".equalsIgnoreCase(val2.trim());
            }
        } catch (Exception ignored) {}

        return false;
    }

    public int getGridAdInterval(String screenType) {
        int interval = 4;
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            long rcCount = 0;
            if ("template".equalsIgnoreCase(screenType) || "templates".equalsIgnoreCase(screenType)) {
                rcCount = rc.getLong("template_ad_interval");
                if (rcCount <= 0) rcCount = rc.getLong("templates_ad_interval");
                if (rcCount <= 0) rcCount = rc.getLong("template_first_ad_after");
                if (rcCount <= 0) rcCount = rc.getLong("template_rv_count");
            } else if ("background".equalsIgnoreCase(screenType) || "backgrounds".equalsIgnoreCase(screenType)) {
                rcCount = rc.getLong("background_ad_interval");
                if (rcCount <= 0) rcCount = rc.getLong("backgrounds_ad_interval");
                if (rcCount <= 0) rcCount = rc.getLong("background_first_ad_after");
                if (rcCount <= 0) rcCount = rc.getLong("background_rv_count");
            }
            if (rcCount <= 0) {
                rcCount = rc.getLong("rv_count");
            }
            if (rcCount <= 0) {
                rcCount = rc.getLong("native_ad_interval");
            }
            if (rcCount <= 0) {
                rcCount = rc.getLong("ad_interval");
            }
            if (rcCount > 0) {
                interval = (int) rcCount;
            } else {
                int fallback = getInt("rv_count", 4);
                interval = fallback > 0 ? fallback : 4;
            }
        } catch (Exception ignored) {
            int fallback = getInt("rv_count", 4);
            interval = fallback > 0 ? fallback : 4;
        }

        // In a 2-column grid, full-width row ads must follow a complete row (even number of items)
        if (interval % 2 != 0) {
            interval = interval + 1;
        }
        return interval;
    }

    // =========================================================================
    // Splash & Foreground App Open Config (Remote Config)
    // =========================================================================

    public JSONObject getSplashConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("splash");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_open", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("splash")) {
                        return rootObj.optJSONObject("splash");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public JSONObject getForegroundAppOpenConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("foreground_app_open");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_open", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("foreground_app_open")) {
                        return rootObj.optJSONObject("foreground_app_open");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public int getSplashTimerSeconds() {
        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("timer_seconds")) {
                int sec = splashObj.optInt("timer_seconds", -1);
                if (sec > 0) return sec;
            }
        } catch (Exception ignored) {}

        int val = getInt("splash_timer_seconds", -1);
        if (val > 0) return val;
        val = getInt("splash_time", -1);
        if (val > 0) return val;

        return 4;
    }

    public boolean isSplashAppOpenEnabled() {
        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("app_open_ad")) {
                JSONObject adObj = splashObj.optJSONObject("app_open_ad");
                if (adObj != null && adObj.has("enabled")) {
                    return adObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        if (getInt("show_app_open_ad", 1) == 0) return false;
        if (getInt("show_splash_app_open", 1) == 0) return false;
        return true;
    }

    public String getSplashAppOpenAdId() {
        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("app_open_ad")) {
                JSONObject adObj = splashObj.optJSONObject("app_open_ad");
                if (adObj != null && adObj.has("ad_id")) {
                    String id = adObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("AppOpenID");
    }

    public boolean isSplashFallbackInterstitialEnabled() {
        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("fallback_interstitial_ad")) {
                JSONObject adObj = splashObj.optJSONObject("fallback_interstitial_ad");
                if (adObj != null && adObj.has("enabled")) {
                    return adObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        return getInt("show_fallback_interstitial", 0) == 1;
    }

    public String getSplashFallbackInterstitialAdId() {
        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("fallback_interstitial_ad")) {
                JSONObject adObj = splashObj.optJSONObject("fallback_interstitial_ad");
                if (adObj != null && adObj.has("ad_id")) {
                    String id = adObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("InterstitalAdunitID");
    }

    public boolean isForegroundAppOpenEnabled() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("enabled")) {
                return fgObj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}

        return getInt("show_app_open_ad", 1) == 1;
    }

    public String getForegroundAppOpenAdId() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("ad_id")) {
                String id = fgObj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("AppOpenID");
    }

    public int getForegroundCooldownSeconds() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("cooldown_seconds")) {
                return fgObj.optInt("cooldown_seconds", 2);
            }
        } catch (Exception ignored) {}

        return getInt("cooldown_seconds", 2);
    }

    public int getGalleryCooldownSeconds() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("gallery_cooldown_seconds")) {
                return fgObj.optInt("gallery_cooldown_seconds", 0);
            }
        } catch (Exception ignored) {}

        return getInt("gallery_cooldown_seconds", 0);
    }

    public boolean isSuppressOnIntent() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("suppress_on_intent")) {
                return fgObj.optBoolean("suppress_on_intent", false);
            }
        } catch (Exception ignored) {}

        return getBoolean("suppress_on_intent", false);
    }

    public boolean isSuppressOnAdClick() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("suppress_on_ad_click")) {
                return fgObj.optBoolean("suppress_on_ad_click", false);
            }
        } catch (Exception ignored) {}

        return getBoolean("suppress_on_ad_click", false);
    }

    // =========================================================================
    // Loading Screen Config (Remote Config)
    // =========================================================================

    public JSONObject getLoadingScreenConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("loading_screen");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "screens", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("loading_screen")) {
                        return rootObj.optJSONObject("loading_screen");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isLoadingScreenEnabled() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return true;
    }

    public boolean isShowLoadingAfterSplash() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("show_after_splash")) {
                return obj.optBoolean("show_after_splash", true);
            }
        } catch (Exception ignored) {}
        return true;
    }

    public boolean isShowLoadingAfterOnboarding() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("show_after_onboarding")) {
                return obj.optBoolean("show_after_onboarding", true);
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getLoadingScreenTimerSeconds() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("timer_seconds")) {
                int sec = obj.optInt("timer_seconds", 10);
                if (sec > 0) return sec;
            }
        } catch (Exception ignored) {}
        return 10;
    }

    public boolean isLoadingNativeAdEnabled() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("enabled")) {
                    return adObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getLoadingNativeAdHeight() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String directH = rc.getString("loading_native_ad_height");
            if (directH != null && !directH.trim().isEmpty()) {
                int dh = Integer.parseInt(directH.trim());
                if (dh > 0) return dh;
            }
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("height")) {
                    int h = adObj.optInt("height", 280);
                    if (h > 0) return h;
                }
            }
        } catch (Exception ignored) {}
        return 280;
    }

    public JSONObject getOnboardingConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("onboarding");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String rootJson = rc.getString("all_data");
            if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                JSONObject rootObj = new JSONObject(rootJson);
                if (rootObj.has("onboarding")) {
                    return rootObj.optJSONObject("onboarding");
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public int getOnboardingNativeAdHeight(int step) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();

            // 1. Check direct step height key (e.g. onboarding_1_native_height)
            String stepDirect = rc.getString("onboarding_" + step + "_native_height");
            if (stepDirect != null && !stepDirect.trim().isEmpty()) {
                int h = Integer.parseInt(stepDirect.trim());
                if (h > 0) return h;
            }

            // 2. Check global direct height key (onboarding_native_height)
            String globalDirect = rc.getString("onboarding_native_height");
            if (globalDirect != null && !globalDirect.trim().isEmpty()) {
                int h = Integer.parseInt(globalDirect.trim());
                if (h > 0) return h;
            }

            // 3. Check onboarding JSON configuration
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null) {
                JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                if (stepObj != null && stepObj.has("height")) {
                    int h = stepObj.optInt("height", 0);
                    if (h > 0) return h;
                }
                JSONObject nativeAdObj = obj.optJSONObject("native_ad");
                if (nativeAdObj != null && nativeAdObj.has("height")) {
                    int h = nativeAdObj.optInt("height", 0);
                    if (h > 0) return h;
                }
                if (obj.has("height")) {
                    int h = obj.optInt("height", 0);
                    if (h > 0) return h;
                }
            }

            // 4. Check SharedPreferences fallback (if synced from Firebase Database in SplashScreen)
            int prefH = getInt("onboarding_" + step + "_native_height", 0);
            if (prefH > 0) return prefH;
        } catch (Exception ignored) {}
        return 220; // Default height for onboarding native ad
    }

    public boolean isOnboardingNativeAdEnabled(int step) {
        try {
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null) {
                // 1. Check step-specific config: onboarding_1.show_native_ad
                JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                if (stepObj != null && stepObj.has("show_native_ad")) {
                    return stepObj.optBoolean("show_native_ad", true);
                }
                // 2. Check global native_ad.enabled in onboarding JSON
                JSONObject nativeAdObj = obj.optJSONObject("native_ad");
                if (nativeAdObj != null && nativeAdObj.has("enabled")) {
                    return nativeAdObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        // Fallback to flat Remote Config / prefs
        boolean globalShow = getInt("show_onboarding_native", 1) == 1;
        boolean stepShow = getInt("onboarding_" + step + "_native", 1) == 1;
        return globalShow && stepShow;
    }

    public boolean isMasterOnboardingInterstitialEnabled() {
        try {
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null && obj.has("is_interstitial_enabled")) {
                return obj.optBoolean("is_interstitial_enabled", true);
            }
        } catch (Exception ignored) {}
        return getInt("onboarding_is_interstitial_enabled", getInt("show_onboarding_interstitial", 1)) == 1;
    }

    public boolean isOnboardingInterstitialEnabled(int step) {
        if (!isMasterOnboardingInterstitialEnabled()) {
            return false;
        }
        try {
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null) {
                JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                if (stepObj != null && stepObj.has("next_interstitial")) {
                    return stepObj.optBoolean("next_interstitial", step != 3);
                }
            }
        } catch (Exception ignored) {}
        return getInt("onboarding_" + step + "_interstitial", step == 3 ? 0 : 1) == 1;
    }

    public String getOnboardingNativeAdId(int step) {
        try {
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null) {
                JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                if (stepObj != null) {
                    if (stepObj.has("native_ad_id")) {
                        String id = stepObj.optString("native_ad_id", "").trim();
                        if (!id.isEmpty() && !id.contains("xxxx")) return id;
                    }
                    if (stepObj.has("ad_id")) {
                        String id = stepObj.optString("ad_id", "").trim();
                        if (!id.isEmpty() && !id.contains("xxxx")) return id;
                    }
                }
                JSONObject nativeAdObj = obj.optJSONObject("native_ad");
                if (nativeAdObj != null && nativeAdObj.has("ad_id")) {
                    String id = nativeAdObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.contains("xxxx")) return id;
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("onboarding_" + step + "_native_id");
    }

    public String getOnboardingInterstitialAdId() {
        try {
            JSONObject obj = getOnboardingConfigJson();
            if (obj != null && obj.has("interstitial_ad_id")) {
                String id = obj.optString("interstitial_ad_id", "").trim();
                if (!id.isEmpty() && !id.contains("xxxx")) return id;
            }
        } catch (Exception ignored) {}
        return getAdsId("onboarding_interstitial_id");
    }

    public String getLoadingNativeAdType() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("type")) {
                    return adObj.optString("type", "video");
                }
            }
        } catch (Exception ignored) {}
        return "video";
    }

    public String getLoadingNativeAdId() {
        try {
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("ad_id")) {
                    String id = adObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("NativeUnitID");
    }

    // =========================================================================
    // Interstitial Ad Config (Remote Config)
    // =========================================================================

    public JSONObject getInterstitialConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("interstitial_ad");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            jsonStr = rc.getString("interstitial");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("interstitial_ad")) {
                        return rootObj.optJSONObject("interstitial_ad");
                    }
                    if (rootObj.has("interstitial")) {
                        return rootObj.optJSONObject("interstitial");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isGlobalInterstitialEnabled() {
        try {
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null) {
                if (obj.has("enabled") && !obj.optBoolean("enabled", true)) {
                    return false;
                }
                if (obj.has("count") && obj.optInt("count", 1) <= 0) {
                    return false;
                }
            }
        } catch (Exception ignored) {}

        if (getInt("show_interstitial", 1) == 0) return false;
        if (getInt("show_interstitial_ad", 1) == 0) return false;
        if (getInt("interstitialAdStatus", 1) == 0) return false;
        if (getInt("interstitial_ad_status", 1) == 0) return false;
        if (getInt("interstitial_count", 1) <= 0 && getInt("interstitial_count", -1) != -1) return false;

        return true;
    }

    public int getGlobalInterstitialCount() {
        // 1. From Remote Config JSON "interstitial_ad": { "count": 2 }
        try {
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null) {
                if (obj.has("count")) {
                    int val = obj.optInt("count", -1);
                    if (val > 0) return val;
                }
                if (obj.has("interstitial_count")) {
                    int val = obj.optInt("interstitial_count", -1);
                    if (val > 0) return val;
                }
                if (obj.has("target_count")) {
                    int val = obj.optInt("target_count", -1);
                    if (val > 0) return val;
                }
            }
        } catch (Exception ignored) {}

        // 2. Direct keys from Remote Config (or Realtime Database fallback)
        int count = getInt("interstitial_ad_count", -1);
        if (count > 0) return count;

        count = getInt("interstitial_count", -1);
        if (count > 0) return count;

        count = getInt("interstitialAdCount", -1);
        if (count > 0) return count;

        count = getInt("interstitialAdStatus", -1);
        if (count > 0) return count;

        // 3. Fallback to SharedPreferences
        if (prefs != null) {
            int pCount = prefs.getInt("interstitialAdStatus", 1);
            if (pCount > 0) return pCount;
        }

        return 1;
    }

    public int getEditScreenInterstitialCount() {
        try {
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("edit_count")) {
                int val = obj.optInt("edit_count", -1);
                if (val > 0) return val;
            }
        } catch (Exception ignored) {}

        int count = getInt("edit_interstitial_count", -1);
        if (count > 0) return count;

        count = getInt("EditScreenAdCount", -1);
        if (count > 0) return count;

        if (prefs != null) {
            int pCount = prefs.getInt("EditScreenAdCount", 3);
            if (pCount > 0) return pCount;
        }

        return 3;
    }

    public String getGlobalInterstitialAdId() {
        try {
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("InterstitalAdunitID");
    }

    // =========================================================================
    // Reward Timer Config (Remote Config)
    // =========================================================================

    public JSONObject getRewardTimerConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("reward_timer");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "editor", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("reward_timer")) {
                        return rootObj.optJSONObject("reward_timer");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isRewardTimerEnabled() {
        try {
            JSONObject obj = getRewardTimerConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("reward_timer_enabled", true);
    }

    public int getRewardTimerSeconds() {
        try {
            JSONObject obj = getRewardTimerConfigJson();
            if (obj != null && obj.has("timer_seconds")) {
                int sec = obj.optInt("timer_seconds", 50);
                if (sec > 0) return sec;
            }
        } catch (Exception ignored) {}
        return getInt("reward_timer_seconds", 50);
    }

    public String getRewardTimerAdId() {
        try {
            JSONObject obj = getRewardTimerConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("RewardVideoUnitID");
    }

    // =========================================================================
    // Undo / Redo Config (Remote Config)
    // =========================================================================

    public JSONObject getUndoRedoConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("undo_redo");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "editor", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("undo_redo")) {
                        return rootObj.optJSONObject("undo_redo");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isUndoRedoAdEnabled() {
        try {
            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("undo_redo_enabled", true);
    }

    public int getUndoRedoFreeActions() {
        try {
            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("free_actions_allowed")) {
                return obj.optInt("free_actions_allowed", 0);
            }
        } catch (Exception ignored) {}
        return getInt("undo_redo_free_actions", 0);
    }

    public String getUndoRedoAdType() {
        try {
            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
        } catch (Exception ignored) {}
        return "reward";
    }

    public String getUndoRedoAdId() {
        try {
            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("RewardVideoUnitID");
    }

    // Search Interstitial Ad Config (Remote Config)
    public boolean isSearchInterstitialEnabled() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("search");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && !jsonStr.trim().equals("null")) {
                org.json.JSONObject obj = new org.json.JSONObject(jsonStr);
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("search_interstitial_enabled", true);
    }

    // Premium Poster Blur & Lock Config (Remote Config JSON)
    public JSONObject getPremiumPosterLockConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("premium_poster_lock");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "editor", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("premium_poster_lock")) {
                        return rootObj.optJSONObject("premium_poster_lock");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isPremiumPosterLockEnabled() {
        try {
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("premium_poster_lock_enabled", true);
    }

    public boolean isPremiumPosterBlurEnabled() {
        try {
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("blur_enabled")) {
                return obj.optBoolean("blur_enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("premium_poster_blur_enabled", true);
    }

    public boolean isPremiumRewardPopupEnabled() {
        try {
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("reward_popup_enabled")) {
                return obj.optBoolean("reward_popup_enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("premium_reward_popup_enabled", true);
    }

    public String getPremiumPosterAdType() {
        try {
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
        } catch (Exception ignored) {}
        return getDataType("PremiumAdType") != null ? getDataType("PremiumAdType") : "reward";
    }

    public boolean isRewardDialogNativeAdEnabled(String screenOrItemType) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();

            // 1. Direct boolean overrides
            if ("false".equalsIgnoreCase(rc.getString("reward_dialog_native_ad_enabled"))
                    || "false".equalsIgnoreCase(rc.getString("show_reward_dialog_native_ad"))) {
                return false;
            }

            // Type specific check: "templates" vs "backgrounds" vs "download"
            boolean isDownload = screenOrItemType != null && (screenOrItemType.toLowerCase().contains("download") || screenOrItemType.toLowerCase().contains("save") || screenOrItemType.toLowerCase().contains("editor"));
            boolean isBg = screenOrItemType != null && (screenOrItemType.toLowerCase().contains("bg") || screenOrItemType.toLowerCase().contains("background"));
            if (isDownload) {
                if ("false".equalsIgnoreCase(rc.getString("show_download_reward_native_ad"))) {
                    return false;
                }
                JSONObject dlObj = getDownloadRewardConfigJson();
                if (dlObj != null && dlObj.has("native_ad")) {
                    JSONObject nad = dlObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("enabled")) {
                        return nad.optBoolean("enabled", true);
                    }
                }
            } else {
                String specificKey = isBg ? "show_background_reward_native_ad" : "show_template_reward_native_ad";
                if ("false".equalsIgnoreCase(rc.getString(specificKey))) {
                    return false;
                }
            }

            // 2. Check premium_poster_lock JSON
            JSONObject lockObj = getPremiumPosterLockConfigJson();
            if (lockObj != null) {
                if (lockObj.has("native_ad_enabled") && !lockObj.optBoolean("native_ad_enabled", true)) {
                    return false;
                }
                if (lockObj.has("native_ad")) {
                    JSONObject nad = lockObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("enabled") && !nad.optBoolean("enabled", true)) {
                        return false;
                    }
                }
            }

            // 3. Check reward_dialog JSON key
            String rewardDialogJson = rc.getString("reward_dialog");
            if (rewardDialogJson != null && !rewardDialogJson.trim().isEmpty() && rewardDialogJson.trim().startsWith("{")) {
                JSONObject rdObj = new JSONObject(rewardDialogJson);
                if (rdObj.has("enabled") && !rdObj.optBoolean("enabled", true)) return false;
                if (rdObj.has("native_ad")) {
                    JSONObject nad = rdObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("enabled")) {
                        return nad.optBoolean("enabled", true);
                    }
                }
            }

            // 4. Check templates / backgrounds screen JSON
            String screenKey = isBg ? "backgrounds" : "templates";
            String screenJson = rc.getString(screenKey);
            if (screenJson != null && !screenJson.trim().isEmpty() && screenJson.trim().startsWith("{")) {
                JSONObject sObj = new JSONObject(screenJson);
                if (sObj.has("reward_dialog_native_ad")) {
                    JSONObject rObj = sObj.optJSONObject("reward_dialog_native_ad");
                    if (rObj != null && rObj.has("enabled")) {
                        return rObj.optBoolean("enabled", true);
                    }
                    return sObj.optBoolean("reward_dialog_native_ad", true);
                }
                if (sObj.has("reward_native_ad")) {
                    JSONObject rObj = sObj.optJSONObject("reward_native_ad");
                    if (rObj != null && rObj.has("enabled")) {
                        return rObj.optBoolean("enabled", true);
                    }
                    return sObj.optBoolean("reward_native_ad", true);
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public String getRewardDialogNativeAdId(String screenOrItemType) {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            boolean isBg = screenOrItemType != null && (screenOrItemType.toLowerCase().contains("bg") || screenOrItemType.toLowerCase().contains("background"));

            // 1. Check reward_dialog JSON
            String rewardDialogJson = rc.getString("reward_dialog");
            if (rewardDialogJson != null && !rewardDialogJson.trim().isEmpty() && rewardDialogJson.trim().startsWith("{")) {
                JSONObject rdObj = new JSONObject(rewardDialogJson);
                if (rdObj.has("native_ad")) {
                    JSONObject nad = rdObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("ad_id")) {
                        String id = nad.optString("ad_id", "").trim();
                        if (!id.isEmpty()) return id;
                    }
                }
            }

            // 2. Check premium_poster_lock JSON
            JSONObject lockObj = getPremiumPosterLockConfigJson();
            if (lockObj != null && lockObj.has("native_ad")) {
                JSONObject nad = lockObj.optJSONObject("native_ad");
                if (nad != null && nad.has("ad_id")) {
                    String id = nad.optString("ad_id", "").trim();
                    if (!id.isEmpty()) return id;
                }
            }

            // 3. Check screen JSON
            String screenKey = isBg ? "backgrounds" : "templates";
            String screenJson = rc.getString(screenKey);
            if (screenJson != null && !screenJson.trim().isEmpty() && screenJson.trim().startsWith("{")) {
                JSONObject sObj = new JSONObject(screenJson);
                if (sObj.has("native_ad")) {
                    JSONObject nad = sObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("ad_id")) {
                        String id = nad.optString("ad_id", "").trim();
                        if (!id.isEmpty()) return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("NativeUnitID");
    }

    public int getSearchInterstitialCount() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("search");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && !jsonStr.trim().equals("null")) {
                org.json.JSONObject obj = new org.json.JSONObject(jsonStr);
                return obj.optInt("count", 3);
            }
        } catch (Exception ignored) {}
        return getInt("search_interstitial_count", 3);
    }

    // =========================================================================
    // Download / Save Reward Ad & Watermark Remote Config
    // =========================================================================

    public JSONObject getDownloadRewardConfigJson() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String jsonStr = rc.getString("download_reward_ad");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            jsonStr = rc.getString("download_config");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "editor", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("download_reward_ad")) {
                        return rootObj.optJSONObject("download_reward_ad");
                    }
                    if (rootObj.has("download_config")) {
                        return rootObj.optJSONObject("download_config");
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isDownloadRewardAdEnabled() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if ("false".equalsIgnoreCase(rc.getString("download_reward_enabled"))
                    || "0".equals(rc.getString("download_reward_enabled"))
                    || "0".equals(rc.getString("show_reward_on_save"))) {
                return false;
            }
            if ("true".equalsIgnoreCase(rc.getString("download_reward_enabled"))
                    || "1".equals(rc.getString("download_reward_enabled"))
                    || "1".equals(rc.getString("show_reward_on_save"))) {
                return true;
            }

            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getInt("show_reward_on_save", 1) == 1;
    }

    public int getFreeDownloadCount() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null) {
                if (obj.has("free_downloads")) {
                    return obj.optInt("free_downloads", 0);
                }
                if (obj.has("free_download_count")) {
                    return obj.optInt("free_download_count", 0);
                }
            }
            String rcCount = rc.getString("free_download_count");
            if (rcCount != null && !rcCount.trim().isEmpty() && !rcCount.equals("null")) {
                try {
                    return Integer.parseInt(rcCount.trim());
                } catch (NumberFormatException ignored) {}
            }
            rcCount = rc.getString("freeDownloadCount");
            if (rcCount != null && !rcCount.trim().isEmpty() && !rcCount.equals("null")) {
                try {
                    return Integer.parseInt(rcCount.trim());
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception ignored) {}
        return getInt("freeDownloadCount", 0);
    }

    public boolean isDownloadRemoveWatermarkEnabled() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if ("false".equalsIgnoreCase(rc.getString("download_remove_watermark"))) {
                return false;
            }
            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null) {
                if (obj.has("remove_watermark")) {
                    return obj.optBoolean("remove_watermark", true);
                }
                if (obj.has("remove_watermark_on_reward")) {
                    return obj.optBoolean("remove_watermark_on_reward", true);
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public String getDownloadAdType() {
        try {
            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
        } catch (Exception ignored) {}
        String prefAdType = getDataType("PremiumAdType");
        return (prefAdType != null && !prefAdType.isEmpty()) ? prefAdType : "Reward";
    }
}
