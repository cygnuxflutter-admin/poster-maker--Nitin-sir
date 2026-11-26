package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;
import android.util.Log;

public class Leaflet_PreferenceClass {

    private static final ThreadLocal<Boolean> IS_RESOLVING_ROOT_CONFIG = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private final SharedPreferences prefs;
    private Context context;

    public Leaflet_PreferenceClass(Context context) {
        this.context = context;
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
        try {
            // 1. Check Root Master Config JSON first (user's live config from remote)
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has(key)) {
                    Object obj = root.opt(key);
                    if (obj instanceof Boolean) return (Boolean) obj;
                    if (obj instanceof Number) return ((Number) obj).intValue() == 1;
                    if (obj instanceof String) {
                        String s = ((String) obj).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null && adData.has(key)) {
                        Object obj = adData.opt(key);
                        if (obj instanceof Boolean) return (Boolean) obj;
                        if (obj instanceof Number) return ((Number) obj).intValue() == 1;
                        if (obj instanceof String) {
                            String s = ((String) obj).trim();
                            if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                            if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                        }
                    }
                }
            }

            // 2. Check individual remote config parameter or defaults
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcValObj = rc.getValue(key);
                if (rcValObj.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String val = rcValObj.asString().trim();
                    if ("true".equalsIgnoreCase(val) || "1".equals(val)) return true;
                    if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
                }
                String rcVal = rc.getString(key);
                if (rcVal != null && !rcVal.trim().isEmpty() && !rcVal.equals("null")) {
                    String val = rcVal.trim();
                    if ("true".equalsIgnoreCase(val) || "1".equals(val)) return true;
                    if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
                }
            }
        } catch (Exception ignored) {}

        if (prefs != null) {
            return prefs.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

        private static final java.util.Map<String, String> DEFAULT_API_FIELDS = new java.util.HashMap<>();
    static {
        DEFAULT_API_FIELDS.put("field_0", "drytdsry6ne46ndr6");
        DEFAULT_API_FIELDS.put("field_1", "https://cygnux.in/postermaker");
        DEFAULT_API_FIELDS.put("field_2", "message");
        DEFAULT_API_FIELDS.put("field_3", "error");
        DEFAULT_API_FIELDS.put("field_4", "data");
        DEFAULT_API_FIELDS.put("field_5", "cat_name");
        DEFAULT_API_FIELDS.put("field_6", "thumb_img");
        DEFAULT_API_FIELDS.put("field_7", "poster_list");
        DEFAULT_API_FIELDS.put("field_8", "ratio");
        DEFAULT_API_FIELDS.put("field_9", "back_image");
        DEFAULT_API_FIELDS.put("field_10", "post_id");
        DEFAULT_API_FIELDS.put("field_11", "post_thumb");
        DEFAULT_API_FIELDS.put("field_12", "text_info");
        DEFAULT_API_FIELDS.put("field_13", "sticker_info");
        DEFAULT_API_FIELDS.put("field_14", "txt_height");
        DEFAULT_API_FIELDS.put("field_15", "txt_width");
        DEFAULT_API_FIELDS.put("field_16", "text");
        DEFAULT_API_FIELDS.put("field_17", "txt_x_pos");
        DEFAULT_API_FIELDS.put("field_18", "font_family");
        DEFAULT_API_FIELDS.put("field_19", "txt_y_pos");
        DEFAULT_API_FIELDS.put("field_20", "txt_order");
        DEFAULT_API_FIELDS.put("field_21", "text_id");
        DEFAULT_API_FIELDS.put("field_22", "txt_rotation");
        DEFAULT_API_FIELDS.put("field_23", "txt_color");
        DEFAULT_API_FIELDS.put("field_24", "st_order");
        DEFAULT_API_FIELDS.put("field_25", "st_image");
        DEFAULT_API_FIELDS.put("field_26", "st_rotation");
        DEFAULT_API_FIELDS.put("field_27", "st_height");
        DEFAULT_API_FIELDS.put("field_28", "st_y_pos");
        DEFAULT_API_FIELDS.put("field_29", "st_x_pos");
        DEFAULT_API_FIELDS.put("field_30", "st_res_uri");
        DEFAULT_API_FIELDS.put("field_31", "st_width");
        DEFAULT_API_FIELDS.put("field_32", "sticker_id");
        DEFAULT_API_FIELDS.put("field_33", "st_res_id");
        DEFAULT_API_FIELDS.put("field_34", "/API/V1/");
        DEFAULT_API_FIELDS.put("field_35", "poster/swiperCat");
        DEFAULT_API_FIELDS.put("field_36", "poster/poster");
        DEFAULT_API_FIELDS.put("field_37", "http://varniapp.com/fontasset/assets/");
        DEFAULT_API_FIELDS.put("field_38", "poster/swiperBG");
        DEFAULT_API_FIELDS.put("field_39", "thumbnail_bg");
        DEFAULT_API_FIELDS.put("field_40", "category_id");
        DEFAULT_API_FIELDS.put("field_41", "category_name");
        DEFAULT_API_FIELDS.put("field_42", "category_list");
        DEFAULT_API_FIELDS.put("field_43", "id");
        DEFAULT_API_FIELDS.put("field_44", "thumb_url");
        DEFAULT_API_FIELDS.put("field_45", "image_url");
        DEFAULT_API_FIELDS.put("field_46", "cat_id");
        DEFAULT_API_FIELDS.put("field_47", "key");
        DEFAULT_API_FIELDS.put("field_48", "device");
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
            if (rc != null) {
                String val = rc.getString(type);
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    return val.trim();
                }
            }
        } catch (Exception ignored) {}
        if (prefs != null) {
            String p = prefs.getString(type, null);
            if (p != null && !p.trim().isEmpty() && !p.equals("null")) {
                return p.trim();
            }
        }
        if (DEFAULT_API_FIELDS.containsKey(type)) {
            return DEFAULT_API_FIELDS.get(type);
        }
        return defaultValue;
    }

    public void setAdsId(String type, String value) {
        prefs.edit().putString(type, value).apply();
    }

    public String getAdsId(String type) {
        try {
            // 1. Check Root Master Config JSON first (user's live config from remote)
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has(type)) {
                    String id = root.optString(type, "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
                    if (id.equals("null") || id.isEmpty()) return ""; // Explicitly disabled
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null && adData.has(type)) {
                        String id = adData.optString(type, "").trim();
                        if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
                        if (id.equals("null") || id.isEmpty()) return ""; // Explicitly disabled
                    }
                }
            }

            // 2. Check individual remote config parameter or defaults
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcValObj = rc.getValue(type);
                if (rcValObj.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String rcId = rcValObj.asString().trim();
                    if (!rcId.isEmpty() && !rcId.equals("null") && !rcId.contains("xxxx")) return rcId;
                }
                String rcId = rc.getString(type);
                if (rcId != null && !rcId.trim().isEmpty() && !rcId.equals("null") && !rcId.contains("xxxx")) {
                    return rcId.trim();
                }
            }
        } catch (Exception ignored) {}

        if (prefs != null) {
            String p = prefs.getString(type, "");
            if (p != null && !p.equals("null") && !p.contains("xxxx")) return p;
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
            // 1. Check Root Master Config JSON first (user's live config from remote)
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has(type)) {
                    Object obj = root.opt(type);
                    if (obj instanceof Boolean) return ((Boolean) obj) ? 1 : 0;
                    if (obj instanceof Number) return ((Number) obj).intValue();
                    if (obj instanceof String) {
                        String s = ((String) obj).trim();
                        if ("true".equalsIgnoreCase(s)) return 1;
                        if ("false".equalsIgnoreCase(s)) return 0;
                        try { return Integer.parseInt(s); } catch (Exception ignored) {}
                    }
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null && adData.has(type)) {
                        Object obj = adData.opt(type);
                        if (obj instanceof Boolean) return ((Boolean) obj) ? 1 : 0;
                        if (obj instanceof Number) return ((Number) obj).intValue();
                        if (obj instanceof String) {
                            String s = ((String) obj).trim();
                            if ("true".equalsIgnoreCase(s)) return 1;
                            if ("false".equalsIgnoreCase(s)) return 0;
                            try { return Integer.parseInt(s); } catch (Exception ignored) {}
                        }
                    }
                }
            }

            // 2. Check individual remote config parameter or defaults
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcValObj = rc.getValue(type);
                if (rcValObj.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String val = rcValObj.asString().trim();
                    if ("true".equalsIgnoreCase(val)) return 1;
                    if ("false".equalsIgnoreCase(val)) return 0;
                    try {
                        return Integer.parseInt(val);
                    } catch (NumberFormatException ignored) {}
                }
                String rcVal = rc.getString(type);
                if (rcVal != null && !rcVal.trim().isEmpty() && !rcVal.equals("null")) {
                    String val = rcVal.trim();
                    if ("true".equalsIgnoreCase(val)) return 1;
                    if ("false".equalsIgnoreCase(val)) return 0;
                    try {
                        return Integer.parseInt(val);
                    } catch (NumberFormatException ignored) {}
                }
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

            java.util.List<String> lookupKeys = new java.util.ArrayList<>();
            lookupKeys.add(primaryKey);
            lookupKeys.add(altKey);
            if ("create_custom".equals(primaryKey) || "create".equals(primaryKey) || "custom".equals(primaryKey)) {
                lookupKeys.add("create");
                lookupKeys.add("create_custom");
                lookupKeys.add("custom");
            }
            if ("exit_dialog".equals(primaryKey) || "app_exit_dialog".equals(primaryKey) || "exit".equals(primaryKey)) {
                lookupKeys.add("app_exit_dialog");
                lookupKeys.add("exit_dialog");
                lookupKeys.add("exit");
            }
            if ("my_creation".equals(primaryKey) || "my_creations".equals(primaryKey) || "creation".equals(primaryKey)) {
                lookupKeys.add("my_creation");
                lookupKeys.add("my_creations");
                lookupKeys.add("creation");
            }
            if ("custom_category".equals(primaryKey) || "category".equals(primaryKey) || "categories".equals(primaryKey)) {
                lookupKeys.add("custom_category");
                lookupKeys.add("category");
                lookupKeys.add("categories");
            }

            // 1. Check Root Master Config JSON first (Prioritizing live remote master JSON)
            JSONObject masterRoot = getRootAdConfigJson();
            if (masterRoot != null) {
                for (String k : lookupKeys) {
                    if (masterRoot.has(k)) {
                        JSONObject screenObj = masterRoot.optJSONObject(k);
                        if (screenObj != null) return screenObj;
                    }
                }
            }

            String acquisitionType = getAcquisitionType();
            java.util.List<String> rootKeys = new java.util.ArrayList<>();
            if ("paid".equalsIgnoreCase(acquisitionType)) {
                rootKeys.add("poster_maker_paid");
                rootKeys.add("Poster_maker_paid");
                rootKeys.add("postermaker_paid");
            } else {
                rootKeys.add("poster_maker_organic");
                rootKeys.add("Poster_maker_organic");
                rootKeys.add("poster_maker");
            }
            rootKeys.add("all_data");
            rootKeys.add("app_config");
            rootKeys.add("all_ads");
            rootKeys.add("ads_config");
            rootKeys.add("screens_config");
            rootKeys.add("ad_config");
            rootKeys.add("screens");
            rootKeys.add("app_ads");
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    for (String k : lookupKeys) {
                        if (rootObj.has(k)) {
                            return rootObj.optJSONObject(k);
                        }
                    }
                }
            }

            // 2. Direct parameter lookup as fallback
            for (String k : lookupKeys) {
                String jsonStr = rc.getString(k);
                if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                    return new JSONObject(jsonStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isCreateCustomNativeAdEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("create_custom_native_ad_enabled")) return root.optBoolean("create_custom_native_ad_enabled", false);
                if (root.has("show_create_custom_native")) return root.optBoolean("show_create_custom_native", false);
                if (root.has("create_custom_native")) return root.optBoolean("create_custom_native", false);
            }
            JSONObject screenObj = getScreenAdConfigJson("create_custom");
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null && nativeObj.has("enabled")) {
                    return nativeObj.optBoolean("enabled", false);
                }
            }
        } catch (Exception ignored) {}

        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue("create_custom_native_ad_enabled");
                if (val != null && val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    return val.asBoolean();
                }
            }
        } catch (Exception ignored) {}

        return false;
    }

    public String getCreateCustomNativeAdId() {
        try {
            JSONObject screenObj = getScreenAdConfigJson("create_custom");
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

        return getGridNativeAdId("create_custom");
    }

    public int getCreateCustomNativeAdHeight() {
        try {
            JSONObject screenObj = getScreenAdConfigJson("create_custom");
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null && nativeObj.has("height")) {
                    return nativeObj.optInt("height", 0);
                }
            }
        } catch (Exception ignored) {}

        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            long h = rc.getLong("create_custom_native_ad_height");
            if (h > 0) return (int) h;
        } catch (Exception ignored) {}

        return 0;
    }

    public boolean isGridAdEnabled(String screenType) {
        // 1. Check Root Master JSON toggles first
        if (!getBoolean("show_grid_native_ad", true) || getInt("show_grid_native_ad", 1) == 0) {
            return false;
        }
        if (!getBoolean("native_ad_enabled", true) || getInt("native_ad_enabled", 1) == 0) {
            return false;
        }
        if ("exit_dialog".equalsIgnoreCase(screenType) || "exit".equalsIgnoreCase(screenType)) {
            if (getInt("show_native_exit", 1) == 0) return false;
        }

        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && screenType != null) {
                JSONObject screens = root.optJSONObject("screens_config");
                if (screens == null) screens = root.optJSONObject("screens");
                if (screens != null) {
                    JSONObject sObj = screens.optJSONObject(screenType);
                    if (sObj != null && sObj.has("native_ad")) {
                        JSONObject nativeObj = sObj.optJSONObject("native_ad");
                        if (nativeObj != null && nativeObj.has("enabled")) {
                            return nativeObj.optBoolean("enabled", true);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null && screenObj.has("native_ad")) {
                JSONObject nativeObj = screenObj.optJSONObject("native_ad");
                if (nativeObj != null && nativeObj.has("enabled")) {
                    return nativeObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if ("exit_dialog".equalsIgnoreCase(screenType) || "exit".equalsIgnoreCase(screenType)) {
                String val = rc.getString("show_native_exit");
                if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
            }
            String val = rc.getString("show_grid_native_ad");
            if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
            val = rc.getString("native_ad_enabled");
            if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
        } catch (Exception ignored) {}
        return true;
    }

    public int getGridFirstAdAfter(String screenType) {
        int count = 0;
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if ("template".equalsIgnoreCase(screenType) || "templates".equalsIgnoreCase(screenType)) {
                    if (root.has("template_ad_interval")) count = root.optInt("template_ad_interval", 0);
                    if (count <= 0 && root.has("templates_ad_interval")) count = root.optInt("templates_ad_interval", 0);
                    if (count <= 0 && root.has("template_first_ad_after")) count = root.optInt("template_first_ad_after", 0);
                } else if ("background".equalsIgnoreCase(screenType) || "backgrounds".equalsIgnoreCase(screenType)) {
                    if (root.has("background_ad_interval")) count = root.optInt("background_ad_interval", 0);
                    if (count <= 0 && root.has("backgrounds_ad_interval")) count = root.optInt("backgrounds_ad_interval", 0);
                    if (count <= 0 && root.has("background_first_ad_after")) count = root.optInt("background_first_ad_after", 0);
                }
                if (count <= 0 && root.has("grid_ad_interval")) count = root.optInt("grid_ad_interval", 0);
                if (count <= 0 && root.has("ad_interval")) count = root.optInt("ad_interval", 0);
            }
        } catch (Exception ignored) {}

        if (count <= 0) {
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
        }

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
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if ("template".equalsIgnoreCase(screenType) || "templates".equalsIgnoreCase(screenType)) {
                    if (root.has("template_ad_interval")) count = root.optInt("template_ad_interval", 0);
                    if (count <= 0 && root.has("templates_ad_interval")) count = root.optInt("templates_ad_interval", 0);
                    if (count <= 0 && root.has("template_repeat_ad_after")) count = root.optInt("template_repeat_ad_after", 0);
                } else if ("background".equalsIgnoreCase(screenType) || "backgrounds".equalsIgnoreCase(screenType)) {
                    if (root.has("background_ad_interval")) count = root.optInt("background_ad_interval", 0);
                    if (count <= 0 && root.has("backgrounds_ad_interval")) count = root.optInt("backgrounds_ad_interval", 0);
                    if (count <= 0 && root.has("background_repeat_ad_after")) count = root.optInt("background_repeat_ad_after", 0);
                }
                if (count <= 0 && root.has("grid_ad_interval")) count = root.optInt("grid_ad_interval", 0);
                if (count <= 0 && root.has("ad_interval")) count = root.optInt("ad_interval", 0);
            }
        } catch (Exception ignored) {}

        if (count <= 0) {
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
        }

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
            // 1. Master global banner status check (Live JSON + RemoteConfig)
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("bannerAdStatus")) {
                    Object val = root.opt("bannerAdStatus");
                    if (val instanceof Boolean && !((Boolean) val)) return false;
                    if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    }
                }
                if (root.has("banner_ad_status")) {
                    Object val = root.opt("banner_ad_status");
                    if (val instanceof Boolean && !((Boolean) val)) return false;
                    if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    }
                }

                // 2. Highest Priority: Screen-Specific Banner Toggles directly from Root JSON
                String sKey = null;
                if ("templates".equalsIgnoreCase(screenType) || "template".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_templates";
                } else if ("backgrounds".equalsIgnoreCase(screenType) || "background".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_background";
                    if (!root.has(sKey) && root.has("show_banner_backgrounds")) sKey = "show_banner_backgrounds";
                } else if ("home".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_home";
                } else if ("share".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_share";
                } else if ("create_custom".equalsIgnoreCase(screenType) || "custom".equalsIgnoreCase(screenType) || "create".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_custom";
                    if (!root.has(sKey) && root.has("show_banner_create_custom")) sKey = "show_banner_create_custom";
                } else if ("my_creation".equalsIgnoreCase(screenType) || "my_creations".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_my_creation";
                }

                if (sKey != null && root.has(sKey)) {
                    Object val = root.opt(sKey);
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    }
                }

                // Check screens_config inside root JSON
                JSONObject screens = root.optJSONObject("screens_config");
                if (screens == null) screens = root.optJSONObject("screens");
                if (screens != null) {
                    JSONObject sObj = screens.optJSONObject(screenType);
                    if (sObj != null) {
                        if (sObj.has("banner_ad")) {
                            JSONObject bObj = sObj.optJSONObject("banner_ad");
                            if (bObj != null && bObj.has("enabled")) {
                                return bObj.optBoolean("enabled", true);
                            }
                        }
                        if (sObj.has("enabled")) {
                            return sObj.optBoolean("enabled", true);
                        }
                    }
                }
            }

            // 3. Check Remote Config direct string parameters
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String rcStatus = rc.getString("bannerAdStatus");
                if ("0".equals(rcStatus) || "false".equalsIgnoreCase(rcStatus)) return false;
                rcStatus = rc.getString("banner_ad_status");
                if ("0".equals(rcStatus) || "false".equalsIgnoreCase(rcStatus)) return false;

                String sKey = null;
                if ("templates".equalsIgnoreCase(screenType) || "template".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_templates";
                } else if ("backgrounds".equalsIgnoreCase(screenType) || "background".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_background";
                } else if ("home".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_home";
                } else if ("share".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_share";
                } else if ("create_custom".equalsIgnoreCase(screenType) || "custom".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_custom";
                } else if ("my_creation".equalsIgnoreCase(screenType)) {
                    sKey = "show_banner_my_creation";
                }

                if (sKey != null) {
                    String val = rc.getString(sKey);
                    if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                        if ("0".equals(val.trim()) || "false".equalsIgnoreCase(val.trim())) return false;
                        if ("1".equals(val.trim()) || "true".equalsIgnoreCase(val.trim())) return true;
                    }
                }
            }

            // 4. SharedPreferences / Local fallbacks
            if (getInt("bannerAdStatus", 1) == 0 || !getBoolean("bannerAdStatus", true)) return false;
            if (getInt("banner_ad_status", 1) == 0 || !getBoolean("banner_ad_status", true)) return false;

            if ("templates".equalsIgnoreCase(screenType) || "template".equalsIgnoreCase(screenType)) {
                if (getInt("show_banner_templates", 1) == 0 || !getBoolean("show_banner_templates", true)) return false;
            } else if ("backgrounds".equalsIgnoreCase(screenType) || "background".equalsIgnoreCase(screenType)) {
                if (getInt("show_banner_background", 1) == 0 && getInt("show_banner_backgrounds", 1) == 0) return false;
            } else if ("home".equalsIgnoreCase(screenType)) {
                if (getInt("show_banner_home", 1) == 0 || !getBoolean("show_banner_home", true)) return false;
            } else if ("share".equalsIgnoreCase(screenType)) {
                if (getInt("show_banner_share", 1) == 0 || !getBoolean("show_banner_share", true)) return false;
            } else if ("create_custom".equalsIgnoreCase(screenType) || "custom".equalsIgnoreCase(screenType) || "create".equalsIgnoreCase(screenType)) {
                if (getInt("show_banner_custom", 1) == 0 || getInt("show_banner_create_custom", 1) == 0) return false;
            }

            // 5. Screen specific XML object fallback
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null) {
                if (screenObj.has("banner_ad")) {
                    JSONObject bannerObj = screenObj.optJSONObject("banner_ad");
                    if (bannerObj != null && bannerObj.has("enabled")) {
                        return bannerObj.optBoolean("enabled", true);
                    }
                }
                if (screenObj.has("enabled")) {
                    return screenObj.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}

        String bannerId = getBannerAdId(screenType);
        return bannerId != null && !bannerId.trim().isEmpty() && !bannerId.equals("null");
    }

    public String getBannerAdId(String screenType) {
        try {
            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null) {
                if (screenObj.has("banner_ad")) {
                    JSONObject bannerObj = screenObj.optJSONObject("banner_ad");
                    if (bannerObj != null && bannerObj.has("ad_id")) {
                        String id = bannerObj.optString("ad_id", "").trim();
                        if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                            return id;
                        }
                    }
                }
                if (screenObj.has("ad_id")) {
                    String id = screenObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                        return id;
                    }
                }
            }
            JSONObject bannerGlobal = getScreenAdConfigJson("banner");
            if (bannerGlobal != null && bannerGlobal.has("ad_id")) {
                String id = bannerGlobal.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}

        return getAdsId("BannerAdunitID");
    }


    public static final String KEY_POSTER_MAKER_ORGANIC = "poster_maker_organic";
    public static final String KEY_POSTER_MAKER_PAID = "poster_maker_paid";

    public static String activeConfigKey = "none";
    public static String activeConfigOrigin = "none";

    public void setAcquisitionType(String type) {
        prefs.edit().putString("user_acquisition_type", type).apply();
    }

    public String getAcquisitionType() {
        return (prefs != null) ? prefs.getString("user_acquisition_type", "organic") : "organic";
    }

    public void setAttributionResolved(boolean resolved) {
        prefs.edit().putBoolean("is_attribution_resolved", resolved).apply();
    }

    public boolean isAttributionResolved() {
        return prefs.getBoolean("is_attribution_resolved", false);
    }

    /**
     * Resolves the Master Config JSON depending on whether the user came from a Paid ad or is Organic.
     * Fallback to Organic/Default if Paid config is missing or empty.
     */
    public static String getMasterConfigJson(com.google.firebase.remoteconfig.FirebaseRemoteConfig rc, Context context) {
        if (rc == null) return "";
        Leaflet_PreferenceClass pref = (context != null) ? new Leaflet_PreferenceClass(context) : null;
        String acquisitionType = (pref != null) ? pref.getAcquisitionType() : "organic"; // "paid", "organic"

        // 1. Remote Config override controls for developer testing
        String forceType = rc.getString("force_config_type");
        if ("organic".equalsIgnoreCase(forceType) || "true".equalsIgnoreCase(rc.getString("force_organic_config"))) {
            acquisitionType = "organic";
            if (pref != null) {
                pref.setAcquisitionType("organic");
                pref.setAttributionResolved(true);
            }
        } else if ("paid".equalsIgnoreCase(forceType) || "true".equalsIgnoreCase(rc.getString("force_paid_config"))) {
            acquisitionType = "paid";
            if (pref != null) {
                pref.setAcquisitionType("paid");
                pref.setAttributionResolved(true);
            }
        } else if (com.online.flyer.design.postermaker.BuildConfig.DEBUG) {
            // When running in Debug mode / testing, ALWAYS default to ORGANIC unless explicitly set to paid in Remote Config
            acquisitionType = "organic";
            if (pref != null) {
                pref.setAcquisitionType("organic");
            }
        } else if ("unknown".equalsIgnoreCase(acquisitionType)) {
            // Live production users without ad click are ORGANIC
            acquisitionType = "organic";
            if (pref != null) {
                pref.setAcquisitionType("organic");
            }
        }

        String finalJson = "";

        // 2. If User is "PAID", load paid config
        if ("paid".equalsIgnoreCase(acquisitionType)) {
            String[] paidKeys = new String[]{KEY_POSTER_MAKER_PAID, "Poster_maker_paid", "postermaker_paid", "poster_maker_ads_paid", "paid_config"};
            for (String key : paidKeys) {
                String paidJson = rc.getString(key);
                if (paidJson != null && !paidJson.trim().isEmpty() && !paidJson.trim().equals("{}") && paidJson.trim().startsWith("{")) {
                    finalJson = paidJson;
                    activeConfigKey = key;
                    activeConfigOrigin = "PAID";
                    Log.i("REMOTE_CONFIG", "==================================================");
                    Log.i("REMOTE_CONFIG", ">>> [CONFIG LOADED]: " + key + " (User Type: PAID)");
                    Log.i("REMOTE_CONFIG", "==================================================");
                    break;
                }
            }
        }

        // 3. If User is "ORGANIC" (or if paid was empty, fallback):
        if (finalJson.isEmpty()) {
            String[] organicKeys = new String[]{KEY_POSTER_MAKER_ORGANIC, "Poster_maker_organic", "poster_maker", "Poster_maker", "postermaker_organic", "all_data", "app_config", "all_ads", "ads_config"};
            for (String key : organicKeys) {
                String defaultJson = rc.getString(key);
                if (defaultJson != null && !defaultJson.trim().isEmpty() && !defaultJson.trim().equals("{}") && defaultJson.trim().startsWith("{")) {
                    finalJson = defaultJson;
                    activeConfigKey = key;
                    activeConfigOrigin = "ORGANIC";
                    Log.i("REMOTE_CONFIG", "==================================================");
                    Log.i("REMOTE_CONFIG", ">>> [CONFIG LOADED]: " + key + " (User Type: " + acquisitionType + ")");
                    Log.i("REMOTE_CONFIG", "==================================================");
                    break;
                }
            }
        }

        return finalJson;
    }

    private static volatile JSONObject cachedRootConfig = null;
    private static volatile String cachedMasterJsonStr = null;

    public JSONObject getRootAdConfigJson() {
        if (Boolean.TRUE.equals(IS_RESOLVING_ROOT_CONFIG.get())) {
            return null;
        }
        IS_RESOLVING_ROOT_CONFIG.set(Boolean.TRUE);
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String masterJson = getMasterConfigJson(rc, context);
            if (masterJson != null && !masterJson.trim().isEmpty() && masterJson.trim().startsWith("{")) {
                if (masterJson.equals(cachedMasterJsonStr) && cachedRootConfig != null) {
                    return cachedRootConfig;
                }
                cachedMasterJsonStr = masterJson;
                cachedRootConfig = new JSONObject(masterJson);
                return cachedRootConfig;
            }

            String acquisitionType = getAcquisitionType();
            java.util.List<String> rootKeys = new java.util.ArrayList<>();
            if ("paid".equalsIgnoreCase(acquisitionType)) {
                rootKeys.add("poster_maker_paid");
                rootKeys.add("Poster_maker_paid");
                rootKeys.add("postermaker_paid");
            } else {
                rootKeys.add("poster_maker_organic");
                rootKeys.add("Poster_maker_organic");
                rootKeys.add("poster_maker");
            }
            rootKeys.add("all_data");
            rootKeys.add("app_config");
            rootKeys.add("all_ads");
            rootKeys.add("ads_config");
            rootKeys.add("screens_config");
            rootKeys.add("ad_config");
            rootKeys.add("screens");
            rootKeys.add("app_ads");

            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    if (rootJson.equals(cachedMasterJsonStr) && cachedRootConfig != null) {
                        return cachedRootConfig;
                    }
                    cachedMasterJsonStr = rootJson;
                    cachedRootConfig = new JSONObject(rootJson);
                    return cachedRootConfig;
                }
            }
        } catch (Exception ignored) {
        } finally {
            IS_RESOLVING_ROOT_CONFIG.set(Boolean.FALSE);
        }
        return null;
    }

    public boolean shouldShowBannerShimmerOnFail(String screenType) {
        try {
            // 1. Check Root Master JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("banner_show_shimmer_on_fail")) {
                    Object val = root.opt("banner_show_shimmer_on_fail");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
                if (root.has("show_shimmer_on_fail")) {
                    Object val = root.opt("show_shimmer_on_fail");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
            }

            // 2. Check Remote Config / Defaults
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("banner_show_shimmer_on_fail");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
            }

            JSONObject screenObj = getScreenAdConfigJson(screenType);
            if (screenObj != null) {
                if (screenObj.has("banner_ad")) {
                    JSONObject bannerObj = screenObj.optJSONObject("banner_ad");
                    if (bannerObj != null) {
                        if (bannerObj.has("banner_show_shimmer_on_fail")) {
                            return bannerObj.optBoolean("banner_show_shimmer_on_fail", false);
                        }
                        if (bannerObj.has("show_shimmer_on_fail")) {
                            return bannerObj.optBoolean("show_shimmer_on_fail", false);
                        }
                    }
                }
                if (screenObj.has("banner_show_shimmer_on_fail")) {
                    return screenObj.optBoolean("banner_show_shimmer_on_fail", false);
                }
                if (screenObj.has("show_shimmer_on_fail")) {
                    return screenObj.optBoolean("show_shimmer_on_fail", false);
                }
            }
        } catch (Exception ignored) {}

        return false;
    }

    public boolean shouldShowDialogShimmerOnFail(String dialogType) {
        try {
            // 1. Check Root Master JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("dialog_show_shimmer_on_fail")) {
                    Object val = root.opt("dialog_show_shimmer_on_fail");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
            }

            // 2. Check Remote Config / Defaults
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("dialog_show_shimmer_on_fail");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
            }

            JSONObject dialogObj = getScreenAdConfigJson(dialogType != null ? dialogType : "exit_dialog");
            if (dialogObj != null) {
                if (dialogObj.has("dialog_show_shimmer_on_fail")) {
                    return dialogObj.optBoolean("dialog_show_shimmer_on_fail", true);
                }
                if (dialogObj.has("show_shimmer_on_fail")) {
                    return dialogObj.optBoolean("show_shimmer_on_fail", true);
                }
                if (dialogObj.has("native_show_shimmer_on_fail")) {
                    return dialogObj.optBoolean("native_show_shimmer_on_fail", true);
                }
            }
        } catch (Exception ignored) {}

        return true;
    }

    public boolean shouldShowNativeShimmerOnFail(String screenType) {
        try {
            // 1. Check Root Master JSON FIRST (Live config from user)
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("native_show_shimmer_on_fail")) {
                    Object val = root.opt("native_show_shimmer_on_fail");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
                if (root.has("show_shimmer_on_fail")) {
                    Object val = root.opt("show_shimmer_on_fail");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("true".equalsIgnoreCase(s) || "1".equals(s)) return true;
                        if ("false".equalsIgnoreCase(s) || "0".equals(s)) return false;
                    }
                }
                if (screenType != null) {
                    JSONObject screens = root.optJSONObject("screens_config");
                    if (screens == null) screens = root.optJSONObject("screens");
                    if (screens != null) {
                        JSONObject sObj = screens.optJSONObject(screenType);
                        if (sObj != null) {
                            if (sObj.has("native_show_shimmer_on_fail")) return sObj.optBoolean("native_show_shimmer_on_fail", false);
                            if (sObj.has("show_shimmer_on_fail")) return sObj.optBoolean("show_shimmer_on_fail", false);
                            JSONObject nad = sObj.optJSONObject("native_ad");
                            if (nad != null) {
                                if (nad.has("native_show_shimmer_on_fail")) return nad.optBoolean("native_show_shimmer_on_fail", false);
                                if (nad.has("show_shimmer_on_fail")) return nad.optBoolean("show_shimmer_on_fail", false);
                            }
                        }
                    }
                }
            }

            // 2. Check Remote Config individual keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("native_show_shimmer_on_fail");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
                String val2 = rc.getString("show_shimmer_on_fail");
                if (val2 != null && !val2.trim().isEmpty() && !val2.equals("null")) {
                    if ("true".equalsIgnoreCase(val2.trim()) || "1".equals(val2.trim())) return true;
                    if ("false".equalsIgnoreCase(val2.trim()) || "0".equals(val2.trim())) return false;
                }
            }

            // 3. Screen-specific XML fallback
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
                if (screenObj.has("preview_native_ad")) {
                    JSONObject prevObj = screenObj.optJSONObject("preview_native_ad");
                    if (prevObj != null) {
                        if (prevObj.has("native_show_shimmer_on_fail")) {
                            return prevObj.optBoolean("native_show_shimmer_on_fail", false);
                        }
                        if (prevObj.has("show_shimmer_on_fail")) {
                            return prevObj.optBoolean("show_shimmer_on_fail", false);
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
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("splash")) {
                return root.optJSONObject("splash");
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue("splash");
                if (val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String jsonStr = val.asString();
                    if (jsonStr != null && jsonStr.trim().startsWith("{")) {
                        return new JSONObject(jsonStr);
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

        public JSONObject getForegroundAppOpenConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("foreground_app_open")) return root.optJSONObject("foreground_app_open");
                if (root.has("app_open")) return root.optJSONObject("app_open");
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null) {
                        if (adData.has("foreground_app_open")) return adData.optJSONObject("foreground_app_open");
                        if (adData.has("app_open")) return adData.optJSONObject("app_open");
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String jsonStr = rc.getString("foreground_app_open");
                if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                    return new JSONObject(jsonStr);
                }
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_open", "app_ads"};
            if (rc != null) {
                for (String rKey : rootKeys) {
                    String rootJson = rc.getString(rKey);
                    if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                        JSONObject rootObj = new JSONObject(rootJson);
                        if (rootObj.has("foreground_app_open")) {
                            return rootObj.optJSONObject("foreground_app_open");
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public int getSplashTimerSeconds() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("splash_timer_seconds")) {
                    int sec = root.optInt("splash_timer_seconds", -1);
                    if (sec > 0) return sec;
                }
                if (root.has("splash_timeout_seconds")) {
                    int sec = root.optInt("splash_timeout_seconds", -1);
                    if (sec > 0) return sec;
                }
                if (root.has("splash_time")) {
                    int sec = root.optInt("splash_time", -1);
                    if (sec > 0) return sec;
                }
            }
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("timer_seconds")) {
                int sec = splashObj.optInt("timer_seconds", -1);
                if (sec > 0) return sec;
            }
        } catch (Exception ignored) {}

        int val = getInt("splash_timer_seconds", -1);
        if (val > 0) return val;
        val = getInt("splash_timeout_seconds", -1);
        if (val > 0) return val;
        val = getInt("splash_time", -1);
        if (val > 0) return val;

        return 4;
    }

    public boolean isSplashAppOpenEnabled() {
        if (getInt("splashscreen", 1) == 0 || !getBoolean("splashscreen", true)) return false;
        if (getInt("show_app_open_ad", 1) == 0 || !getBoolean("show_app_open_ad", true)) return false;
        if (getInt("show_splash_app_open", 1) == 0 || !getBoolean("show_splash_app_open", true)) return false;

        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("app_open_ad")) {
                JSONObject adObj = splashObj.optJSONObject("app_open_ad");
                if (adObj != null && adObj.has("enabled")) {
                    if (!adObj.optBoolean("enabled", true)) return false;
                }
            }
        } catch (Exception ignored) {}

        String id = getSplashAppOpenAdId();
        if (id == null || id.trim().isEmpty() || id.equals("null")) {
            return false;
        }

        return true;
    }

        public String getSplashAppOpenAdId() {
        String mainId = getAdsId("AppOpenID");
        if (mainId != null && !mainId.trim().isEmpty() && !mainId.equals("null") && !mainId.contains("xxxx")) {
            return mainId.trim();
        }

        try {
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("app_open_ad")) {
                JSONObject adObj = splashObj.optJSONObject("app_open_ad");
                if (adObj != null && adObj.has("ad_id")) {
                    String id = adObj.optString("ad_id", "").trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx") && !id.contains("5706123402805812")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}

        return "";
    }

    public boolean isSplashFallbackInterstitialEnabled() {
        if (getInt("splashscreen", 1) == 0 || !getBoolean("splashscreen", true)) return false;

        try {
            // Support both "app_open_fallback_interstitial" and "show_fallback_interstitial"
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("app_open_fallback_interstitial")) {
                    return parseJsonBoolOrInt(root, "app_open_fallback_interstitial", false);
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null && adData.has("app_open_fallback_interstitial")) {
                        return parseJsonBoolOrInt(adData, "app_open_fallback_interstitial", false);
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue("app_open_fallback_interstitial");
                if (rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String s = rcVal.asString().trim();
                    if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                }
            }
        } catch (Exception ignored) {}

        if (getInt("show_fallback_interstitial", 0) == 0 || !getBoolean("show_fallback_interstitial", false)) return false;

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

    private boolean parseJsonBoolOrInt(JSONObject obj, String key, boolean def) {
        if (obj == null || !obj.has(key)) return def;
        Object val = obj.opt(key);
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        if (val instanceof String) {
            String s = ((String) val).trim();
            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
        }
        return def;
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
                if (!fgObj.optBoolean("enabled", true)) return false;
            }
        } catch (Exception ignored) {}

        if (getInt("show_app_open_ad", 1) == 0 || !getBoolean("show_app_open_ad", true)) return false;
        if (getInt("show_foreground_app_open", 1) == 0 || !getBoolean("show_foreground_app_open", true)) return false;

        String id = getForegroundAppOpenAdId();
        if (id == null || id.trim().isEmpty() || id.equals("null")) {
            return false;
        }

        return true;
    }

    public String getForegroundAppOpenAdId() {
        String mainId = getAdsId("AppOpenID");
        if (mainId != null && !mainId.trim().isEmpty() && !mainId.equals("null") && !mainId.contains("xxxx")) {
            return mainId.trim();
        }

        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("ad_id")) {
                String id = fgObj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx") && !id.contains("5706123402805812")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}

        return "";
    }

    public int getForegroundCooldownSeconds() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("cooldown_seconds")) return rootObj.optInt("cooldown_seconds", 2);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("cooldown_seconds")) {
                        return adData.optInt("cooldown_seconds", 2);
                    }
                }
            }

            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("cooldown_seconds")) {
                return fgObj.optInt("cooldown_seconds", 2);
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("cooldown_seconds");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    try { return Integer.parseInt(val.trim()); } catch (Exception ignored) {}
                }
            }
            if (rootObj != null) {
                if (rootObj.has("cooldown_seconds")) return rootObj.optInt("cooldown_seconds", 2);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("cooldown_seconds")) {
                        return adData.optInt("cooldown_seconds", 2);
                    }
                }
            }
        } catch (Exception ignored) {}

        return getInt("cooldown_seconds", 2);
    }

    public int getMinBackgroundSeconds() {
        try {
            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("min_background_seconds")) {
                return Math.max(0, fgObj.optInt("min_background_seconds", 5));
            }

            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("min_background_seconds")) {
                    return Math.max(0, rootObj.optInt("min_background_seconds", 5));
                }
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("min_background_seconds")) {
                        return Math.max(0, adData.optInt("min_background_seconds", 5));
                    }
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("min_background_seconds");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    try {
                        return Math.max(0, Integer.parseInt(val.trim()));
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        return Math.max(0, getInt("min_background_seconds", 5));
    }

    public int getGalleryCooldownSeconds() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("gallery_cooldown_seconds")) return rootObj.optInt("gallery_cooldown_seconds", 0);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("gallery_cooldown_seconds")) {
                        return adData.optInt("gallery_cooldown_seconds", 0);
                    }
                }
            }

            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("gallery_cooldown_seconds")) {
                return fgObj.optInt("gallery_cooldown_seconds", 0);
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("gallery_cooldown_seconds");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    try { return Integer.parseInt(val.trim()); } catch (Exception ignored) {}
                }
            }
            if (rootObj != null) {
                if (rootObj.has("gallery_cooldown_seconds")) return rootObj.optInt("gallery_cooldown_seconds", 0);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("gallery_cooldown_seconds")) {
                        return adData.optInt("gallery_cooldown_seconds", 0);
                    }
                }
            }
        } catch (Exception ignored) {}

        return getInt("gallery_cooldown_seconds", 0);
    }

    public boolean isSuppressOnIntent() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("suppress_on_intent")) return rootObj.optBoolean("suppress_on_intent", false);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("suppress_on_intent")) {
                        return adData.optBoolean("suppress_on_intent", false);
                    }
                }
            }

            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("suppress_on_intent")) {
                return fgObj.optBoolean("suppress_on_intent", false);
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("suppress_on_intent");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
            }
            if (rootObj != null) {
                if (rootObj.has("suppress_on_intent")) return rootObj.optBoolean("suppress_on_intent", false);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("suppress_on_intent")) {
                        return adData.optBoolean("suppress_on_intent", false);
                    }
                }
            }
        } catch (Exception ignored) {}

        return prefs != null ? prefs.getBoolean("suppress_on_intent", false) : false;
    }

    public boolean isSuppressOnAdClick() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("suppress_on_ad_click")) return rootObj.optBoolean("suppress_on_ad_click", false);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("suppress_on_ad_click")) {
                        return adData.optBoolean("suppress_on_ad_click", false);
                    }
                }
            }

            JSONObject fgObj = getForegroundAppOpenConfigJson();
            if (fgObj != null && fgObj.has("suppress_on_ad_click")) {
                return fgObj.optBoolean("suppress_on_ad_click", false);
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("suppress_on_ad_click");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
            }
            if (rootObj != null) {
                if (rootObj.has("suppress_on_ad_click")) return rootObj.optBoolean("suppress_on_ad_click", false);
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null && adData.has("suppress_on_ad_click")) {
                        return adData.optBoolean("suppress_on_ad_click", false);
                    }
                }
            }
        } catch (Exception ignored) {}

        return prefs != null ? prefs.getBoolean("suppress_on_ad_click", false) : false;
    }

    public int getRewardAdTimeoutSeconds() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("reward_ad_timeout_seconds")) return rootObj.optInt("reward_ad_timeout_seconds", 10);
                if (rootObj.has("reward_timeout_seconds")) return rootObj.optInt("reward_timeout_seconds", 10);
                if (rootObj.has("reward_dialog")) {
                    JSONObject rewObj = rootObj.optJSONObject("reward_dialog");
                    if (rewObj != null && rewObj.has("reward_ad_timeout_seconds")) {
                        return rewObj.optInt("reward_ad_timeout_seconds", 10);
                    }
                }
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null) {
                        if (adData.has("reward_ad_timeout_seconds")) return adData.optInt("reward_ad_timeout_seconds", 10);
                        if (adData.has("reward_timeout_seconds")) return adData.optInt("reward_timeout_seconds", 10);
                    }
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("reward_ad_timeout_seconds");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    try { return Integer.parseInt(val.trim()); } catch (Exception ignored) {}
                }
                String val2 = rc.getString("reward_timeout_seconds");
                if (val2 != null && !val2.trim().isEmpty() && !val2.equals("null")) {
                    try { return Integer.parseInt(val2.trim()); } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}

        return getInt("reward_ad_timeout_seconds", 10);
    }

    // =========================================================================
    // Loading Screen Config (Remote Config)
    // =========================================================================

    public JSONObject getLoadingScreenConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("loading_screen")) {
                JSONObject obj = root.optJSONObject("loading_screen");
                if (obj != null) return obj;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
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
            String jsonStr = rc.getString("loading_screen");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isLoadingScreenEnabled() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null && rootObj.has("show_loading_screen")) {
                Object val = rootObj.get("show_loading_screen");
                if (val instanceof Boolean) return (Boolean) val;
                if (val instanceof Number) return ((Number) val).intValue() != 0;
                if (val instanceof String) {
                    String s = ((String) val).trim();
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                }
            }
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("show_loading_screen");
                if (val != null && !val.trim().isEmpty()) {
                    if ("0".equals(val.trim()) || "false".equalsIgnoreCase(val.trim())) return false;
                    if ("1".equals(val.trim()) || "true".equalsIgnoreCase(val.trim())) return true;
                }
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
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("loading_timer_seconds")) {
                    int sec = rootObj.optInt("loading_timer_seconds", 0);
                    if (sec > 0) return sec;
                }
                if (rootObj.has("loading_screen_timer_seconds")) {
                    int sec = rootObj.optInt("loading_screen_timer_seconds", 0);
                    if (sec > 0) return sec;
                }
            }
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("timer_seconds")) {
                int sec = obj.optInt("timer_seconds", 0);
                if (sec > 0) return sec;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("loading_timer_seconds");
                if (val == null || val.trim().isEmpty() || "0".equals(val.trim())) {
                    val = rc.getString("loading_screen_timer_seconds");
                }
                if (val != null && !val.trim().isEmpty()) {
                    try {
                        int sec = Integer.parseInt(val.trim());
                        if (sec > 0) return sec;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception ignored) {}
        return 10;
    }

    public boolean isLoadingNativeAdEnabled() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("show_loading_native")) {
                    Object val = rootObj.get("show_loading_native");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
                if (rootObj.has("show_loading_screen_native")) {
                    Object val = rootObj.get("show_loading_screen_native");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
            }
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("enabled")) {
                    return adObj.optBoolean("enabled", true);
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("show_loading_native");
                if (val == null || val.trim().isEmpty()) {
                    val = rc.getString("show_loading_screen_native");
                }
                if (val != null && !val.trim().isEmpty()) {
                    if ("0".equals(val.trim()) || "false".equalsIgnoreCase(val.trim())) return false;
                    if ("1".equals(val.trim()) || "true".equalsIgnoreCase(val.trim())) return true;
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getLoadingNativeAdHeight() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("loading_native_height")) {
                    int dh = rootObj.optInt("loading_native_height", 0);
                    if (dh > 0) return dh;
                }
                if (rootObj.has("loading_native_ad_height")) {
                    int dh = rootObj.optInt("loading_native_ad_height", 0);
                    if (dh > 0) return dh;
                }
            }
            JSONObject obj = getLoadingScreenConfigJson();
            if (obj != null && obj.has("native_ad")) {
                JSONObject adObj = obj.optJSONObject("native_ad");
                if (adObj != null && adObj.has("height")) {
                    int h = adObj.optInt("height", 0);
                    if (h > 0) return h;
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String directH = rc.getString("loading_native_height");
                if (directH == null || directH.trim().isEmpty() || "0".equals(directH.trim())) {
                    directH = rc.getString("loading_native_ad_height");
                }
                if (directH != null && !directH.trim().isEmpty()) {
                    int dh = Integer.parseInt(directH.trim());
                    if (dh > 0) return dh;
                }
            }
        } catch (Exception ignored) {}
        return 280;
    }

    public JSONObject getOnboardingConfigJson() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null && rootObj.has("onboarding")) {
                JSONObject obj = rootObj.optJSONObject("onboarding");
                if (obj != null) return obj;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String rootJson = rc.getString("all_data");
            if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                JSONObject rObj = new JSONObject(rootJson);
                if (rObj.has("onboarding")) {
                    return rObj.optJSONObject("onboarding");
                }
            }
            String jsonStr = rc.getString("onboarding");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public int getPreviewNativeAdHeight() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("preview_native_height")) {
                    int h = rootObj.optInt("preview_native_height", 0);
                    if (h > 0) return h;
                }
                if (rootObj.has("preview_native_ad_height")) {
                    int h = rootObj.optInt("preview_native_ad_height", 0);
                    if (h > 0) return h;
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String hStr = rc.getString("preview_native_height");
                if (hStr == null || hStr.trim().isEmpty() || "0".equals(hStr.trim())) {
                    hStr = rc.getString("preview_native_ad_height");
                }
                if (hStr != null && !hStr.trim().isEmpty()) {
                    try {
                        int h = Integer.parseInt(hStr.trim());
                        if (h > 0) return h;
                    } catch (NumberFormatException ignored) {}
                }
            }
            if (rootObj != null) {
                if (rootObj.has("preview_native_height")) {
                    int h = rootObj.optInt("preview_native_height", 0);
                    if (h > 0) return h;
                }
                if (rootObj.has("preview_native_ad_height")) {
                    int h = rootObj.optInt("preview_native_ad_height", 0);
                    if (h > 0) return h;
                }
            }

            int prefH = getInt("preview_native_height", 0);
            if (prefH > 0) return prefH;
        } catch (Exception ignored) {}
        return 95;
    }

    public boolean isPreviewNativeAdEnabled(String screenType) {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("show_preview_native")) {
                    Object val = rootObj.get("show_preview_native");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                }
                if (rootObj.has("show_native_preview")) {
                    Object val = rootObj.get("show_native_preview");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("show_preview_native");
                if (val == null || val.trim().isEmpty()) {
                    val = rc.getString("show_native_preview");
                }
                if ("0".equals(val) || "false".equalsIgnoreCase(val)) {
                    return false;
                } else if ("1".equals(val) || "true".equalsIgnoreCase(val)) {
                    return true;
                }
            }
            if (rootObj != null) {
                if (rootObj.has("show_preview_native")) {
                    Object val = rootObj.get("show_preview_native");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                }
                if (rootObj.has("show_native_preview")) {
                    Object val = rootObj.get("show_native_preview");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() != 0;
                }
            }

            if (!getBoolean("show_preview_native", true) || getInt("show_preview_native", 1) == 0) {
                return false;
            }
            if (!getBoolean("show_native_preview", true) || getInt("show_native_preview", 1) == 0) {
                return false;
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getOnboardingNativeAdHeight(int step) {
        try {
            // 1. Check Root Master Config JSON (User's live JSON) FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                String stepKey = "onboarding_" + step + "_native_height";
                if (root.has(stepKey)) {
                    int h = root.optInt(stepKey, 0);
                    if (h > 0) return h;
                }
                if (root.has("onboarding_native_height")) {
                    int h = root.optInt("onboarding_native_height", 0);
                    if (h > 0) return h;
                }
            }

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

            // 4. Check Master/Root Config or SharedPreferences fallback
            int prefH = getInt("onboarding_" + step + "_native_height", 0);
            if (prefH > 0) return prefH;
            int globalPrefH = getInt("onboarding_native_height", 0);
            if (globalPrefH > 0) return globalPrefH;
        } catch (Exception ignored) {}
        return -1; // Default wrap_content for onboarding native ad
    }

    public boolean isOnboardingNativeAdEnabled(int step) {
        try {
            // 1. Check Root Master Config JSON (User's live JSON) FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"show_onboarding_native", "onboarding_native_enabled"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean && !((Boolean) val)) return false;
                        if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        }
                    }
                }
                String stepKey = "onboarding_" + step + "_native";
                if (root.has(stepKey)) {
                    Object val = root.opt(stepKey);
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
                if (root.has("onboarding")) {
                    JSONObject obj = root.optJSONObject("onboarding");
                    if (obj != null) {
                        JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                        if (stepObj != null && stepObj.has("show_native_ad")) {
                            return stepObj.optBoolean("show_native_ad", true);
                        }
                        JSONObject nativeAdObj = obj.optJSONObject("native_ad");
                        if (nativeAdObj != null && nativeAdObj.has("enabled")) {
                            return nativeAdObj.optBoolean("enabled", true);
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue globalVal = rc.getValue("show_onboarding_native");
                if (globalVal != null && globalVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String s = globalVal.asString().trim();
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                }
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue stepVal = rc.getValue("onboarding_" + step + "_native");
                if (stepVal != null && stepVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String s = stepVal.asString().trim();
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                }
            }
        } catch (Exception ignored) {}

        boolean globalShow = getInt("show_onboarding_native", 1) == 1;
        boolean stepShow = getInt("onboarding_" + step + "_native", 1) == 1;
        return globalShow && stepShow;
    }

    public boolean isMasterOnboardingInterstitialEnabled() {
        try {
            // 1. Check Root Master Config JSON (User's live JSON) FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"show_onboarding_interstitial", "onboarding_interstitial_enabled", "onboarding_is_interstitial_enabled"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
                if (root.has("onboarding")) {
                    JSONObject obj = root.optJSONObject("onboarding");
                    if (obj != null && obj.has("is_interstitial_enabled")) {
                        return obj.optBoolean("is_interstitial_enabled", true);
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"show_onboarding_interstitial", "onboarding_interstitial_enabled", "onboarding_is_interstitial_enabled"}) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(k);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        String s = rcVal.asString().trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getInt("onboarding_is_interstitial_enabled", getInt("show_onboarding_interstitial", 1)) == 1;
    }

    public boolean isOnboardingInterstitialEnabled(int step) {
        if (!isMasterOnboardingInterstitialEnabled()) {
            return false;
        }
        try {
            // 1. Check Root Master Config JSON (User's live JSON) FIRST!
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                String key = "onboarding_" + step + "_interstitial";
                if (root.has(key)) {
                    Object val = root.opt(key);
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
                if (root.has("onboarding")) {
                    JSONObject obj = root.optJSONObject("onboarding");
                    if (obj != null) {
                        JSONObject stepObj = obj.optJSONObject("onboarding_" + step);
                        if (stepObj != null && stepObj.has("next_interstitial")) {
                            return stepObj.optBoolean("next_interstitial", true);
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config key
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String key = "onboarding_" + step + "_interstitial";
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(key);
                if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String s = rcVal.asString().trim();
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
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
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("interstitial_ad")) return root.optJSONObject("interstitial_ad");
                if (root.has("interstitial")) return root.optJSONObject("interstitial");
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
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
            String jsonStr = rc.getString("interstitial_ad");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            jsonStr = rc.getString("interstitial");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isGlobalInterstitialEnabled() {
        // 1. Highest Priority: Root Master JSON (User's live config from remote)
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("show_interstitial")) {
                    Object val = root.opt("show_interstitial");
                    if (val instanceof Boolean && !((Boolean) val)) return false;
                    if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                    if (val instanceof String && ("0".equals(val) || "false".equalsIgnoreCase((String) val))) return false;
                }
                if (root.has("show_interstitial_ad")) {
                    Object val = root.opt("show_interstitial_ad");
                    if (val instanceof Boolean && !((Boolean) val)) return false;
                    if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                    if (val instanceof String && ("0".equals(val) || "false".equalsIgnoreCase((String) val))) return false;
                }
                if (root.has("interstitial_ad_enabled")) {
                    Object val = root.opt("interstitial_ad_enabled");
                    if (val instanceof Boolean && !((Boolean) val)) return false;
                    if (val instanceof Number && ((Number) val).intValue() == 0) return false;
                    if (val instanceof String && ("0".equals(val) || "false".equalsIgnoreCase((String) val))) return false;
                }
                if (root.has("interstitial_ad_count") && root.optInt("interstitial_ad_count", 1) <= 0) {
                    return false;
                }
                if (root.has("interstitial_ad")) {
                    JSONObject obj = root.optJSONObject("interstitial_ad");
                    if (obj != null) {
                        if (obj.has("enabled") && !obj.optBoolean("enabled", true)) return false;
                        if (obj.has("count") && obj.optInt("count", 1) <= 0) return false;
                    }
                }
            }
        } catch (Exception ignored) {}

        // 2. Direct keys from Remote Config
        if (getInt("show_interstitial", 1) == 0) return false;
        if (getInt("show_interstitial_ad", 1) == 0) return false;
        if (getInt("interstitial_ad_enabled", 1) == 0) return false;
        if (getInt("interstitialAdStatus", 1) == 0) return false;
        if (getInt("interstitalAdStatus", 1) == 0) return false;
        if (getInt("interstitial_ad_status", 1) == 0) return false;
        if (getInt("interstitial_count", 1) <= 0 && getInt("interstitial_count", -1) != -1) return false;
        if (getInt("interstitial_ad_count", 1) <= 0 && getInt("interstitial_ad_count", -1) != -1) return false;

        // 3. Fallback to interstitial config JSON
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

        String adId = getGlobalInterstitialAdId();
        if (adId == null || adId.trim().isEmpty() || adId.equals("null")) {
            return false;
        }

        return true;
    }

    public int getGlobalInterstitialCount() {
        // 1. Highest Priority: Root Master Config JSON (User's live config from remote)
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("interstitial_ad_count")) {
                    int val = root.optInt("interstitial_ad_count", -1);
                    if (val > 0) return val;
                }
                if (root.has("interstitial_count")) {
                    int val = root.optInt("interstitial_count", -1);
                    if (val > 0) return val;
                }
                if (root.has("interstitialAdCount")) {
                    int val = root.optInt("interstitialAdCount", -1);
                    if (val > 0) return val;
                }
                if (root.has("interstitialAdStatus")) {
                    int val = root.optInt("interstitialAdStatus", -1);
                    if (val > 0) return val;
                }
                if (root.has("interstitial_ad")) {
                    JSONObject obj = root.optJSONObject("interstitial_ad");
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

        // 3. From Remote Config JSON "interstitial_ad"
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

        // 4. Fallback to SharedPreferences
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
        String mainId = getAdsId("InterstitalAdunitID");
        if (mainId != null && !mainId.trim().isEmpty() && !mainId.equals("null") && !mainId.contains("xxxx")) {
            return mainId.trim();
        }

        try {
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx") && !id.contains("5706123402805812")) {
                    return id;
                }
            }
        } catch (Exception ignored) {}

        return "";
    }


    // =========================================================================
    // Interstitial Frequency Capping & Cooldown Config (Remote Config)
    // =========================================================================

    public int getInterstitialCooldownSeconds() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("interstitial_cooldown_seconds")) return root.optInt("interstitial_cooldown_seconds", 60);
                if (root.has("cooldown_seconds")) return root.optInt("cooldown_seconds", 60);
                if (root.has("interstitial_ad")) {
                    JSONObject obj = root.optJSONObject("interstitial_ad");
                    if (obj != null && obj.has("cooldown_seconds")) {
                        return obj.optInt("cooldown_seconds", 60);
                    }
                }
            }
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("cooldown_seconds")) {
                return obj.optInt("cooldown_seconds", 60);
            }
        } catch (Exception ignored) {}
        int rcVal = getInt("interstitial_cooldown_seconds", -1);
        if (rcVal >= 0) return rcVal;
        return 60; // Default 60 seconds cooldown between interstitials
    }

    public int getInterstitialDailyCap() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("interstitial_daily_cap")) return root.optInt("interstitial_daily_cap", 7);
                if (root.has("daily_cap")) return root.optInt("daily_cap", 7);
                if (root.has("interstitial_ad")) {
                    JSONObject obj = root.optJSONObject("interstitial_ad");
                    if (obj != null && obj.has("daily_cap")) {
                        return obj.optInt("daily_cap", 7);
                    }
                }
            }
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("daily_cap")) {
                return obj.optInt("daily_cap", 7);
            }
        } catch (Exception ignored) {}
        int rcVal = getInt("interstitial_daily_cap", -1);
        if (rcVal >= 0) return rcVal;
        return 7; // Default max 7 interstitials per user per day (0 = unlimited)
    }

    public int getInterstitialHourlyCap() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("interstitial_hourly_cap")) return root.optInt("interstitial_hourly_cap", 3);
                if (root.has("hourly_cap")) return root.optInt("hourly_cap", 3);
                if (root.has("interstitial_ad")) {
                    JSONObject obj = root.optJSONObject("interstitial_ad");
                    if (obj != null && obj.has("hourly_cap")) {
                        return obj.optInt("hourly_cap", 3);
                    }
                }
            }
            JSONObject obj = getInterstitialConfigJson();
            if (obj != null && obj.has("hourly_cap")) {
                return obj.optInt("hourly_cap", 3);
            }
        } catch (Exception ignored) {}
        int rcVal = getInt("interstitial_hourly_cap", -1);
        if (rcVal >= 0) return rcVal;
        return 3; // Default max 3 interstitials per hour (0 = unlimited)
    }

    public int getInterstitialDailyCount() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date());
        String lastDate = getString("interstitial_daily_date", "");
        if (!today.equals(lastDate)) {
            return 0;
        }
        return getInt("interstitial_daily_count", 0);
    }

    public int getInterstitialHourlyCount() {
        long now = System.currentTimeMillis();
        long windowStart = getLong("interstitial_hourly_window_start", 0);
        if (windowStart <= 0 || (now - windowStart) >= 3600000L) {
            return 0;
        }
        return getInt("interstitial_hourly_count", 0);
    }

    public boolean isInterstitialDailyCapReached() {
        int dailyCap = getInterstitialDailyCap();
        if (dailyCap <= 0) return false; // 0 or negative = unlimited
        return getInterstitialDailyCount() >= dailyCap;
    }

    public boolean isInterstitialHourlyCapReached() {
        int hourlyCap = getInterstitialHourlyCap();
        if (hourlyCap <= 0) return false; // 0 or negative = unlimited
        return getInterstitialHourlyCount() >= hourlyCap;
    }

    public boolean isInterstitialCooldownPassed() {
        int cooldown = getInterstitialCooldownSeconds();
        if (cooldown <= 0) return true; // 0 or negative = no cooldown required
        long lastTime = getLong("interstitial_last_shown_time", 0);
        if (lastTime <= 0) return true; // First ad ever
        long elapsedSeconds = (System.currentTimeMillis() - lastTime) / 1000;
        return elapsedSeconds >= cooldown;
    }

    public boolean canShowInterstitialAd() {
        if (!isGlobalInterstitialEnabled()) {
            return false;
        }
        if (isInterstitialDailyCapReached()) {
            android.util.Log.d("AdFrequencyCap", "Interstitial Ad blocked: Daily cap (" + getInterstitialDailyCap() + ") reached. Today's count: " + getInterstitialDailyCount());
            return false;
        }
        if (isInterstitialHourlyCapReached()) {
            android.util.Log.d("AdFrequencyCap", "Interstitial Ad blocked: Hourly cap (" + getInterstitialHourlyCap() + ") reached. This hour's count: " + getInterstitialHourlyCount());
            return false;
        }
        if (!isInterstitialCooldownPassed()) {
            long elapsed = (System.currentTimeMillis() - getLong("interstitial_last_shown_time", 0)) / 1000;
            android.util.Log.d("AdFrequencyCap", "Interstitial Ad blocked: Cooldown active (" + elapsed + "s elapsed, requires " + getInterstitialCooldownSeconds() + "s)");
            return false;
        }
        return true;
    }

    public void recordInterstitialAdShown() {
        long now = System.currentTimeMillis();
        setLong("interstitial_last_shown_time", now);

        // Daily count tracking
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date(now));
        String lastDate = getString("interstitial_daily_date", "");
        int dailyCount = 1;
        if (today.equals(lastDate)) {
            dailyCount = getInt("interstitial_daily_count", 0) + 1;
        }
        setString("interstitial_daily_date", today);
        setInt("interstitial_daily_count", dailyCount);

        // Hourly count tracking (rolling 1-hour window)
        long windowStart = getLong("interstitial_hourly_window_start", 0);
        int hourlyCount = 1;
        if (windowStart > 0 && (now - windowStart) < 3600000L) {
            hourlyCount = getInt("interstitial_hourly_count", 0) + 1;
        } else {
            setLong("interstitial_hourly_window_start", now);
        }
        setInt("interstitial_hourly_count", hourlyCount);

        android.util.Log.d("AdFrequencyCap", "Interstitial Ad Recorded -> Daily: " + dailyCount + "/" + getInterstitialDailyCap() +
                ", Hourly: " + hourlyCount + "/" + getInterstitialHourlyCap() +
                ", Cooldown: " + getInterstitialCooldownSeconds() + "s");
    }

    // =========================================================================
    // Reward Timer Config (Remote Config)
    // =========================================================================

    public JSONObject getRewardTimerConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("reward_timer")) {
                JSONObject obj = root.optJSONObject("reward_timer");
                if (obj != null) return obj;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
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
            String jsonStr = rc.getString("reward_timer");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isRewardTimerEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("reward_timer_enabled")) {
                Object val = root.opt("reward_timer_enabled");
                if (val instanceof Boolean) return (Boolean) val;
                if (val instanceof Number) return ((Number) val).intValue() == 1;
                if (val instanceof String) return "1".equals(val) || "true".equalsIgnoreCase((String) val);
            }
            JSONObject obj = getRewardTimerConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        if (!getBoolean("reward_timer_enabled", true)) return false;
        if (!getBoolean("show_editor_timer", true)) return false;
        return true;
    }

    public int getRewardTimerSeconds() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                int mins = root.optInt("reward_timer_minutes", -1);
                int secs = root.optInt("reward_timer_seconds", -1);
                if (mins >= 0 || secs >= 0) {
                    int totalSecs = (mins > 0 ? mins * 60 : 0) + (secs > 0 ? secs : 0);
                    if (totalSecs > 0) return totalSecs;
                }
                if (root.has("editor_timer_seconds")) {
                    int sec = root.optInt("editor_timer_seconds", 0);
                    if (sec > 0) return sec;
                }
            }
            JSONObject obj = getRewardTimerConfigJson();
            if (obj != null) {
                int mins = obj.optInt("timer_minutes", obj.optInt("reward_timer_minutes", -1));
                int secs = obj.optInt("timer_seconds", obj.optInt("reward_timer_seconds", -1));
                if (mins >= 0 || secs >= 0) {
                    int totalSecs = (mins > 0 ? mins * 60 : 0) + (secs > 0 ? secs : 0);
                    if (totalSecs > 0) return totalSecs;
                }
            }
        } catch (Exception ignored) {}
        int mins = getInt("reward_timer_minutes", -1);
        int sec = getInt("reward_timer_seconds", -1);
        if (mins >= 0 || sec >= 0) {
            int totalSecs = (mins > 0 ? mins * 60 : 0) + (sec > 0 ? sec : 0);
            if (totalSecs > 0) return totalSecs;
        }
        sec = getInt("editor_timer_seconds", 0);
        if (sec > 0) return sec;
        return 50;
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
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("undo_redo")) {
                        JSONObject obj = editorObj.optJSONObject("undo_redo");
                        if (obj != null) return obj;
                    }
                }
                if (root.has("undo_redo")) {
                    JSONObject obj = root.optJSONObject("undo_redo");
                    if (obj != null) return obj;
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String editorStr = rc.getString("editor");
            if (editorStr != null && !editorStr.trim().isEmpty() && editorStr.trim().startsWith("{")) {
                try {
                    JSONObject editorObj = new JSONObject(editorStr);
                    if (editorObj.has("undo_redo")) {
                        return editorObj.optJSONObject("undo_redo");
                    }
                } catch (Exception ignored) {}
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("editor")) {
                        JSONObject editorObj = rootObj.optJSONObject("editor");
                        if (editorObj != null && editorObj.has("undo_redo")) {
                            return editorObj.optJSONObject("undo_redo");
                        }
                    }
                    if (rootObj.has("undo_redo")) {
                        return rootObj.optJSONObject("undo_redo");
                    }
                }
            }
            String jsonStr = rc.getString("undo_redo");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isUndoRedoAdEnabled() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("undo_redo")) {
                        JSONObject ur = editorObj.optJSONObject("undo_redo");
                        if (ur != null && ur.has("enabled")) return ur.optBoolean("enabled", true);
                    }
                }
                for (String k : new String[]{"undo_redo_ad_enabled", "editor_undo_redo_ad_enabled", "undo_redo_enabled", "show_undo_redo_ad"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"undo_redo_ad_enabled", "editor_undo_redo_ad_enabled", "undo_redo_enabled", "show_undo_redo_ad"}) {
                    String s = rc.getString(k);
                    if (s != null && !s.trim().isEmpty()) {
                        s = s.trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
            }

            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getUndoRedoFreeActions() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("undo_redo")) {
                        JSONObject ur = editorObj.optJSONObject("undo_redo");
                        if (ur != null && ur.has("free_actions_allowed")) return ur.optInt("free_actions_allowed", 1);
                    }
                }
                for (String k : new String[]{"undo_redo_free_actions", "editor_free_undo_redo_allowed", "undo_redo_count", "undo_redo_action_count"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Number) return ((Number) val).intValue();
                        if (val instanceof String) {
                            try { return Integer.parseInt(((String) val).trim()); } catch (Exception ignored) {}
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"undo_redo_free_actions", "editor_free_undo_redo_allowed", "undo_redo_count", "undo_redo_action_count"}) {
                    String s = rc.getString(k);
                    if (s != null && !s.trim().isEmpty()) {
                        try {
                            int v = Integer.parseInt(s.trim());
                            if (v >= 0) return v;
                        } catch (Exception ignored) {}
                    }
                }
            }

            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("free_actions_allowed")) {
                return obj.optInt("free_actions_allowed", 1);
            }
        } catch (Exception ignored) {}
        return 1;
    }

    public String getUndoRedoAdType() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("undo_redo")) {
                        JSONObject ur = editorObj.optJSONObject("undo_redo");
                        if (ur != null && ur.has("ad_type")) return ur.optString("ad_type", "reward");
                    }
                }
                for (String k : new String[]{"undo_redo_ad_type", "editor_undo_redo_ad_type"}) {
                    if (root.has(k)) {
                        String t = root.optString(k, "").trim();
                        if (!t.isEmpty() && !t.equals("null")) return t;
                    }
                }
            }

            // 2. Direct individual Remote Config keys (only if set remotely)
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"undo_redo_ad_type", "editor_undo_redo_ad_type"}) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(k);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        String rcType = rcVal.asString().trim();
                        if (!rcType.isEmpty() && !rcType.equals("null")) return rcType;
                    }
                }
            }

            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
        } catch (Exception ignored) {}
        return "reward";
    }

    public String getUndoRedoAdId() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            for (String k : new String[]{"editor_undo_redo_ad_id", "undo_redo_ad_id"}) {
                String id = rc.getString(k);
                if (id != null && !id.trim().isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    String nativeId = getAdsId("NativeUnitID");
                    if ((nativeId.isEmpty() || !id.equals(nativeId)) && !id.contains("5807970180")) {
                        return id.trim();
                    }
                }
            }

            JSONObject obj = getUndoRedoConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    String nativeId = getAdsId("NativeUnitID");
                    if ((nativeId.isEmpty() || !id.equals(nativeId)) && !id.contains("5807970180")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("RewardVideoUnitID");
    }

    // =========================================================================
    // Reset Canvas Config (Remote Config)
    // =========================================================================

    public JSONObject getResetConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("reset")) {
                        JSONObject obj = editorObj.optJSONObject("reset");
                        if (obj != null) return obj;
                    }
                }
                if (root.has("reset")) {
                    JSONObject obj = root.optJSONObject("reset");
                    if (obj != null) return obj;
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String editorStr = rc.getString("editor");
            if (editorStr != null && !editorStr.trim().isEmpty() && editorStr.trim().startsWith("{")) {
                try {
                    JSONObject editorObj = new JSONObject(editorStr);
                    if (editorObj.has("reset")) {
                        return editorObj.optJSONObject("reset");
                    }
                } catch (Exception ignored) {}
            }
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_ads"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("editor")) {
                        JSONObject editorObj = rootObj.optJSONObject("editor");
                        if (editorObj != null && editorObj.has("reset")) {
                            return editorObj.optJSONObject("reset");
                        }
                    }
                    if (rootObj.has("reset")) {
                        return rootObj.optJSONObject("reset");
                    }
                }
            }
            String jsonStr = rc.getString("reset");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isResetAdEnabled() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("reset")) {
                        JSONObject res = editorObj.optJSONObject("reset");
                        if (res != null && res.has("enabled")) return res.optBoolean("enabled", true);
                    }
                }
                for (String k : new String[]{"reset_ad_enabled", "editor_reset_ad_enabled", "reset_enabled", "show_reset_ad"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"reset_ad_enabled", "editor_reset_ad_enabled", "reset_enabled", "show_reset_ad"}) {
                    String s = rc.getString(k);
                    if (s != null && !s.trim().isEmpty()) {
                        s = s.trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
            }

            JSONObject obj = getResetConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return true;
    }

    public int getResetFreeActions() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("reset")) {
                        JSONObject res = editorObj.optJSONObject("reset");
                        if (res != null && res.has("free_actions_allowed")) return res.optInt("free_actions_allowed", 1);
                    }
                }
                for (String k : new String[]{"reset_free_actions", "editor_free_reset_allowed", "reset_count", "reset_action_count"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Number) return ((Number) val).intValue();
                        if (val instanceof String) {
                            try { return Integer.parseInt(((String) val).trim()); } catch (Exception ignored) {}
                        }
                    }
                }
            }

            // 2. Direct individual Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"reset_free_actions", "editor_free_reset_allowed", "reset_count", "reset_action_count"}) {
                    String s = rc.getString(k);
                    if (s != null && !s.trim().isEmpty()) {
                        try {
                            int v = Integer.parseInt(s.trim());
                            if (v >= 0) return v;
                        } catch (Exception ignored) {}
                    }
                }
            }

            JSONObject obj = getResetConfigJson();
            if (obj != null && obj.has("free_actions_allowed")) {
                return obj.optInt("free_actions_allowed", 1);
            }
        } catch (Exception ignored) {}
        return 1;
    }

    public String getResetAdType() {
        try {
            // 1. Check Root Master Config JSON FIRST
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("editor")) {
                    JSONObject editorObj = root.optJSONObject("editor");
                    if (editorObj != null && editorObj.has("reset")) {
                        JSONObject res = editorObj.optJSONObject("reset");
                        if (res != null && res.has("ad_type")) return res.optString("ad_type", "reward");
                    }
                }
                for (String k : new String[]{"reset_ad_type", "editor_reset_ad_type"}) {
                    if (root.has(k)) {
                        String t = root.optString(k, "").trim();
                        if (!t.isEmpty() && !t.equals("null")) return t;
                    }
                }
            }

            // 2. Direct individual Remote Config keys (only if set remotely)
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"reset_ad_type", "editor_reset_ad_type"}) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(k);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        String rcType = rcVal.asString().trim();
                        if (!rcType.isEmpty() && !rcType.equals("null")) return rcType;
                    }
                }
            }

            JSONObject obj = getResetConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
        } catch (Exception ignored) {}
        return "reward";
    }

    public String getResetAdId() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            for (String k : new String[]{"editor_reset_ad_id", "reset_ad_id"}) {
                String id = rc.getString(k);
                if (id != null && !id.trim().isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    String nativeId = getAdsId("NativeUnitID");
                    if ((nativeId.isEmpty() || !id.equals(nativeId)) && !id.contains("5807970180")) {
                        return id.trim();
                    }
                }
            }

            JSONObject obj = getResetConfigJson();
            if (obj != null && obj.has("ad_id")) {
                String id = obj.optString("ad_id", "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) {
                    String nativeId = getAdsId("NativeUnitID");
                    if ((nativeId.isEmpty() || !id.equals(nativeId)) && !id.contains("5807970180")) {
                        return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("RewardVideoUnitID");
    }

    // Search Interstitial Ad Config (Remote Config)
    public boolean isSearchInterstitialEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("search_interstitial_enabled")) {
                    return getBoolean("search_interstitial_enabled", true);
                }
                if (root.has("search_enabled")) {
                    return getBoolean("search_enabled", true);
                }
                if (root.has("search")) {
                    Object sObj = root.opt("search");
                    if (sObj instanceof JSONObject) {
                        return ((JSONObject) sObj).optBoolean("enabled", true);
                    } else if (sObj instanceof String) {
                        try {
                            return new JSONObject((String) sObj).optBoolean("enabled", true);
                        } catch (Exception ignored) {}
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue("search_interstitial_enabled");
                if (val != null && val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    return val.asBoolean();
                }
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue sVal = rc.getValue("search");
                if (sVal != null && sVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String jsonStr = sVal.asString();
                    if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                        return new JSONObject(jsonStr).optBoolean("enabled", true);
                    }
                }
            }
        } catch (Exception ignored) {}
        return getBoolean("search_interstitial_enabled", true);
    }

    // Premium Poster Blur & Lock Config (Remote Config JSON)
    public JSONObject getPremiumPosterLockConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("premium_poster_lock")) {
                JSONObject obj = root.optJSONObject("premium_poster_lock");
                if (obj != null) return obj;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
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
            String jsonStr = rc.getString("premium_poster_lock");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isPremiumPosterLockEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{
                        "premium_poster_lock_enabled",
                        "show_open_reward_popup",
                        "show_edit_reward_popup",
                        "show_template_edit_reward_popup",
                        "show_background_edit_reward_popup",
                        "premium_reward_popup_enabled"
                }) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean && (Boolean) val) return true;
                        if (val instanceof Number && ((Number) val).intValue() == 1) return true;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
                if (root.has("premium_poster_lock")) {
                    JSONObject obj = root.optJSONObject("premium_poster_lock");
                    if (obj != null && obj.has("enabled")) {
                        return obj.optBoolean("enabled", true);
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"premium_poster_lock_enabled", "show_open_reward_popup", "show_edit_reward_popup", "show_template_edit_reward_popup", "show_background_edit_reward_popup"}) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue(k);
                    if (val != null && val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        String s = val.asString().trim();
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                    }
                }
            }
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }
        } catch (Exception ignored) {}
        return getBoolean("premium_poster_lock_enabled", true);
    }

    public boolean isPremiumLockEnabled(String itemType) {
        try {
            boolean isBg = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background")));
            String specificKey = isBg ? "background_premium_enabled" : "templates_premium_enabled";

            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has(specificKey)) {
                    return parseJsonBoolOrInt(root, specificKey, true);
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null && adData.has(specificKey)) {
                        return parseJsonBoolOrInt(adData, specificKey, true);
                    }
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(specificKey);
                if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String s = rcVal.asString().trim();
                    if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                }
            }

            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = isBg ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("enabled")) {
                    return target.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}
        return isPremiumPosterLockEnabled();
    }

    public boolean isPremiumPosterBlurEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"premium_poster_blur_enabled", "poster_blur_enabled"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        }
                    }
                }
                if (root.has("premium_poster_lock")) {
                    JSONObject obj = root.optJSONObject("premium_poster_lock");
                    if (obj != null && obj.has("blur_enabled")) {
                        return obj.optBoolean("blur_enabled", false);
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue("premium_poster_blur_enabled");
                if (val != null && val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    return val.asBoolean();
                }
            }
            JSONObject obj = getPremiumPosterLockConfigJson();
            if (obj != null && obj.has("blur_enabled")) {
                return obj.optBoolean("blur_enabled", false);
            }
        } catch (Exception ignored) {}
        return getBoolean("premium_poster_blur_enabled", false);
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
            JSONObject rootObj = getRootAdConfigJson();

            // 1. Direct boolean / int overrides (Remote Config & Root JSON)
            String master1 = rc.getString("reward_dialog_native_ad_enabled");
            String master2 = rc.getString("show_reward_dialog_native_ad");
            if ("false".equalsIgnoreCase(master1) || "0".equals(master1)
                    || "false".equalsIgnoreCase(master2) || "0".equals(master2)) {
                return false;
            }
            if (rootObj != null) {
                if (rootObj.has("reward_dialog_native_ad_enabled")) {
                    Object val = rootObj.opt("reward_dialog_native_ad_enabled");
                    if (Boolean.FALSE.equals(val) || "0".equals(String.valueOf(val)) || Integer.valueOf(0).equals(val)) return false;
                }
                if (rootObj.has("show_reward_dialog_native_ad")) {
                    Object val = rootObj.opt("show_reward_dialog_native_ad");
                    if (Boolean.FALSE.equals(val) || "0".equals(String.valueOf(val)) || Integer.valueOf(0).equals(val)) return false;
                }
            }

            // Type specific check: "templates" vs "backgrounds" vs "download"
            boolean isDownload = screenOrItemType != null && (screenOrItemType.toLowerCase().contains("download") || screenOrItemType.toLowerCase().contains("save") || screenOrItemType.toLowerCase().contains("editor"));
            boolean isBg = screenOrItemType != null && (screenOrItemType.toLowerCase().contains("bg") || screenOrItemType.toLowerCase().contains("background"));
            if (isDownload) {
                String dlKey = rc.getString("show_download_reward_native_ad");
                if ("false".equalsIgnoreCase(dlKey) || "0".equals(dlKey)) {
                    return false;
                }
                if (rootObj != null && rootObj.has("show_download_reward_native_ad")) {
                    Object val = rootObj.opt("show_download_reward_native_ad");
                    if (Boolean.FALSE.equals(val) || "0".equals(String.valueOf(val)) || Integer.valueOf(0).equals(val)) return false;
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
                String specVal = rc.getString(specificKey);
                if ("false".equalsIgnoreCase(specVal) || "0".equals(specVal)) {
                    return false;
                }
                if (rootObj != null && rootObj.has(specificKey)) {
                    Object val = rootObj.opt(specificKey);
                    if (Boolean.FALSE.equals(val) || "0".equals(String.valueOf(val)) || Integer.valueOf(0).equals(val)) return false;
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

            // 3. Check reward_dialog JSON key (reads from root/master config as well)
            JSONObject rdObj = getScreenAdConfigJson("reward_dialog");
            if (rdObj != null) {
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

            // 1. Check reward_dialog JSON (reads from root/master config as well)
            JSONObject rdObj = getScreenAdConfigJson("reward_dialog");
            if (rdObj != null) {
                if (rdObj.has("native_ad")) {
                    JSONObject nad = rdObj.optJSONObject("native_ad");
                    if (nad != null && nad.has("ad_id")) {
                        String id = nad.optString("ad_id", "").trim();
                        if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
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
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("search_interstitial_count")) {
                    int c = root.optInt("search_interstitial_count", -1);
                    if (c > 0) return c;
                }
                if (root.has("search_count")) {
                    int c = root.optInt("search_count", -1);
                    if (c > 0) return c;
                }
                if (root.has("search")) {
                    Object sObj = root.opt("search");
                    if (sObj instanceof JSONObject) {
                        int c = ((JSONObject) sObj).optInt("count", -1);
                        if (c > 0) return c;
                    } else if (sObj instanceof String) {
                        try {
                            int c = new JSONObject((String) sObj).optInt("count", -1);
                            if (c > 0) return c;
                        } catch (Exception ignored) {}
                    }
                }
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue val = rc.getValue("search_interstitial_count");
                if (val != null && val.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    try {
                        int c = Integer.parseInt(val.asString().trim());
                        if (c > 0) return c;
                    } catch (NumberFormatException ignored) {}
                }
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue sVal = rc.getValue("search");
                if (sVal != null && sVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String jsonStr = sVal.asString();
                    if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                        org.json.JSONObject obj = new org.json.JSONObject(jsonStr);
                        int c = obj.optInt("count", -1);
                        if (c > 0) return c;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getInt("search_interstitial_count", 4);
    }

    // =========================================================================
    // Download / Save Reward Ad & Watermark Remote Config
    // =========================================================================

    public JSONObject getDownloadRewardConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("download_reward_ad")) return root.optJSONObject("download_reward_ad");
                if (root.has("download_config")) return root.optJSONObject("download_config");
                if (root.has("download")) return root.optJSONObject("download");
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String[] rootKeys = new String[]{"ads_config", "screens_config", "editor", "app_ads", "all_data", "app_config"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("download_reward_ad")) return rootObj.optJSONObject("download_reward_ad");
                    if (rootObj.has("download_config")) return rootObj.optJSONObject("download_config");
                    if (rootObj.has("download")) return rootObj.optJSONObject("download");
                }
            }
            String jsonStr = rc.getString("download_reward_ad");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            jsonStr = rc.getString("download_config");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            jsonStr = rc.getString("download");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
            String editorJson = rc.getString("editor");
            if (editorJson != null && editorJson.trim().startsWith("{")) {
                JSONObject edObj = new JSONObject(editorJson);
                if (edObj.has("download")) return edObj.optJSONObject("download");
                if (edObj.has("download_reward_ad")) return edObj.optJSONObject("download_reward_ad");
                if (edObj.has("download_config")) return edObj.optJSONObject("download_config");
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isDownloadRewardAdEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("show_reward_on_save")) {
                    Object val = root.opt("show_reward_on_save");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
                if (root.has("download_reward_enabled")) {
                    Object val = root.opt("download_reward_enabled");
                    if (val instanceof Boolean) return (Boolean) val;
                    if (val instanceof Number) return ((Number) val).intValue() == 1;
                    if (val instanceof String) {
                        String s = ((String) val).trim();
                        if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                        if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                    }
                }
            }

            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null && obj.has("enabled")) {
                return obj.optBoolean("enabled", true);
            }

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
        } catch (Exception ignored) {}
        return getBoolean("show_reward_on_save", true);
    }

    public int getFreeDownloadCount() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"free_download_count", "free_downloads_allowed", "download_free_count", "freeDownloadCount"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Number) return ((Number) val).intValue();
                        if (val instanceof String) {
                            try { return Integer.parseInt(((String) val).trim()); } catch (Exception ignored) {}
                        }
                    }
                }
            }
            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null) {
                if (obj.has("free_downloads_allowed")) return obj.optInt("free_downloads_allowed", 0);
                if (obj.has("free_downloads")) return obj.optInt("free_downloads", 0);
                if (obj.has("free_download_count")) return obj.optInt("free_download_count", 0);
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            for (String k : new String[]{"free_download_count", "free_downloads_allowed", "download_free_count", "freeDownloadCount"}) {
                String rcCount = rc.getString(k);
                if (rcCount != null && !rcCount.trim().isEmpty() && !rcCount.equals("null")) {
                    try { return Integer.parseInt(rcCount.trim()); } catch (NumberFormatException ignored) {}
                }
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
                if (obj.has("ask_watermark_removal")) {
                    return obj.optBoolean("ask_watermark_removal", true);
                }
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
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("download_ad_type")) {
                String t = root.optString("download_ad_type", "").trim();
                if (!t.isEmpty() && !t.equals("null")) return t;
            }
            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null && obj.has("ad_type")) {
                return obj.optString("ad_type", "reward");
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String rcType = rc.getString("download_ad_type");
            if (rcType != null && !rcType.trim().isEmpty() && !rcType.equals("null")) return rcType.trim();
        } catch (Exception ignored) {}
        String prefAdType = getDataType("PremiumAdType");
        return (prefAdType != null && !prefAdType.isEmpty()) ? prefAdType : "Reward";
    }

    // =========================================================================
    // Premium Config (Templates / Backgrounds / Dialogs) Remote Config
    // =========================================================================

    public JSONObject getPremiumConfigJson() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null && root.has("premium_config")) {
                JSONObject obj = root.optJSONObject("premium_config");
                if (obj != null) return obj;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String[] rootKeys = new String[]{"ads_config", "screens_config", "app_ads", "all_data", "app_config"};
            for (String rKey : rootKeys) {
                String rootJson = rc.getString(rKey);
                if (rootJson != null && !rootJson.trim().isEmpty() && rootJson.trim().startsWith("{")) {
                    JSONObject rootObj = new JSONObject(rootJson);
                    if (rootObj.has("premium_config")) {
                        return rootObj.optJSONObject("premium_config");
                    }
                }
            }
            String jsonStr = rc.getString("premium_config");
            if (jsonStr != null && !jsonStr.trim().isEmpty() && jsonStr.trim().startsWith("{")) {
                return new JSONObject(jsonStr);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public boolean isProOpenPopupEnabled(String itemType) {
        try {
            boolean isBg = (itemType != null && itemType.toLowerCase().contains("bg"));
            String specificKey = isBg ? "show_background_open_reward_popup" : "show_template_open_reward_popup";
            String[] keys = new String[]{specificKey, "show_open_reward_popup", "pro_open_popup_enabled", "open_reward_popup_enabled"};

            // 1. Check Root Master Config JSON
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : keys) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
            }

            // 2. Direct Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            for (String k : keys) {
                String s = rc.getString(k);
                if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
            }

            // 3. Nested premium_config JSON
            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = isBg ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("open_popup_enabled")) {
                    return target.optBoolean("open_popup_enabled", false);
                }
            }
        } catch (Exception ignored) {}
        // By default, opening/showing preview is FREE unless specifically enabled in Remote Config
        return false;
    }

    public boolean isProEditPopupEnabled(String itemType) {
        try {
            boolean isBg = (itemType != null && itemType.toLowerCase().contains("bg"));
            String specificKey = isBg ? "show_background_edit_reward_popup" : "show_template_edit_reward_popup";
            String[] keys = new String[]{specificKey, "show_edit_reward_popup", "pro_edit_popup_enabled", "edit_reward_popup_enabled"};

            // 1. Check Root Master Config JSON
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : keys) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
            }

            // 2. Direct Remote Config keys
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            for (String k : keys) {
                String s = rc.getString(k);
                if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
            }

            // 3. Nested premium_config JSON
            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = isBg ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("edit_popup_enabled")) {
                    return target.optBoolean("edit_popup_enabled", true);
                }
            }
        } catch (Exception ignored) {}
        return isPremiumRewardPopupEnabled();
    }

    public int getPremiumPosterInterval(String itemType) {
        try {
            boolean isBg = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background")));
            String[] specificKeys = isBg
                    ? new String[]{"background_premium_interval", "background_pro_interval"}
                    : new String[]{"templates_premium_interval", "template_pro_interval"};

            // 1. Root Master Config JSON (User's live config from remote)
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                for (String k : specificKeys) {
                    if (rootObj.has(k)) {
                        int v = rootObj.optInt(k, -1);
                        if (v >= 0) return v;
                    }
                }
                if (rootObj.has("PremiumPostCount")) {
                    int v = rootObj.optInt("PremiumPostCount", -1);
                    if (v >= 0) return v;
                }
                if (rootObj.has("premium_post_count")) {
                    int v = rootObj.optInt("premium_post_count", -1);
                    if (v >= 0) return v;
                }
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null) {
                        for (String k : specificKeys) {
                            if (adData.has(k)) {
                                int v = adData.optInt(k, -1);
                                if (v >= 0) return v;
                            }
                        }
                        if (adData.has("PremiumPostCount")) {
                            int v = adData.optInt("PremiumPostCount", -1);
                            if (v >= 0) return v;
                        }
                    }
                }
            }

            // 2. Direct Remote Config keys from server (VALUE_SOURCE_REMOTE)
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String specificKey : specificKeys) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(specificKey);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        try {
                            int v = Integer.parseInt(rcVal.asString().trim());
                            if (v >= 0) return v;
                        } catch (NumberFormatException ignored) {}
                    }
                }
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcPostCount = rc.getValue("PremiumPostCount");
                if (rcPostCount != null && rcPostCount.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    try {
                        int v = Integer.parseInt(rcPostCount.asString().trim());
                        if (v >= 0) return v;
                    } catch (NumberFormatException ignored) {}
                }
            }

            // 3. Premium config JSON
            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = isBg ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("interval")) {
                    int val = target.optInt("interval", -1);
                    if (val >= 0) return val;
                }
                String altKey = isBg ? "background_interval" : "template_interval";
                if (premObj.has(altKey)) {
                    int val = premObj.optInt(altKey, -1);
                    if (val >= 0) return val;
                }
                if (premObj.has("interval")) {
                    int val = premObj.optInt("interval", -1);
                    if (val >= 0) return val;
                }
            }
        } catch (Exception ignored) {}
        return getInt("PremiumPostCount", 3);
    }

    public String getPremiumPosterAdType(String itemType) {
        try {
            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background"))) ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("ad_type")) {
                    return target.optString("ad_type", "reward");
                }
            }
        } catch (Exception ignored) {}
        return getPremiumPosterAdType();
    }

    public String getPremiumPosterRewardAdId(String itemType) {
        try {
            boolean isBg = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background")));
            String directKey = isBg ? "background_reward_ad_id" : "templates_reward_ad_id";

            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null && rootObj.has(directKey)) {
                String id = rootObj.optString(directKey, "").trim();
                if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(directKey);
                if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                    String id = rcVal.asString().trim();
                    if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
                }
            }

            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = isBg ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null) {
                    for (String k : new String[]{"reward_ad_id", "ad_id"}) {
                        if (target.has(k)) {
                            String id = target.optString(k, "").trim();
                            if (!id.isEmpty() && !id.equals("null") && !id.contains("xxxx")) return id;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("RewardVideoUnitID");
    }

    public String getPremiumPosterInterstitialAdId(String itemType) {
        try {
            JSONObject premObj = getPremiumConfigJson();
            if (premObj != null) {
                String key = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background"))) ? "background" : "templates";
                JSONObject target = premObj.optJSONObject(key);
                if (target != null && target.has("interstitial_ad_id")) {
                    String id = target.optString("interstitial_ad_id", "").trim();
                    if (!id.isEmpty() && !id.contains("xxxx")) return id;
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("InterstitalAdunitID");
    }

    // =========================================================================
    // Time-Based Unlock Duration & Persistence (24h default / custom / -1 permanent)
    // =========================================================================

    /**
     * Resolves how long an unlocked template/background remains unlocked in milliseconds.
     * Supports Remote Config minutes, hours, or -1 (permanent). Default = 24 hours.
     */
    public long getUnlockDurationMs(String itemType) {
        boolean isBg = (itemType != null && (itemType.toLowerCase().contains("bg") || itemType.toLowerCase().contains("background")));
        String[] minKeys = isBg
                ? new String[]{"background_unlocked_duration_minutes", "unlocked_duration_minutes"}
                : new String[]{"templates_unlocked_duration_minutes", "unlocked_duration_minutes"};
        String[] hourKeys = isBg
                ? new String[]{"background_unlocked_duration_hours", "unlocked_duration_hours"}
                : new String[]{"templates_unlocked_duration_hours", "unlocked_duration_hours"};

        try {
            JSONObject rootObj = getRootAdConfigJson();
            JSONObject premObj = getPremiumConfigJson();
            JSONObject targetPrem = premObj != null ? premObj.optJSONObject(isBg ? "background" : "templates") : null;
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();

            // 1. Check minutes override (root -> ad_data -> premium_config.<type> -> direct RC)
            if (rootObj != null) {
                for (String k : minKeys) {
                    if (rootObj.has(k)) {
                        long m = rootObj.optLong(k, 0);
                        if (m < 0) return -1L;
                        if (m > 0) return m * 60_000L;
                    }
                }
                JSONObject adData = rootObj.optJSONObject("ad_data");
                if (adData != null) {
                    for (String k : minKeys) {
                        if (adData.has(k)) {
                            long m = adData.optLong(k, 0);
                            if (m < 0) return -1L;
                            if (m > 0) return m * 60_000L;
                        }
                    }
                }
            }
            if (targetPrem != null && targetPrem.has("unlocked_duration_minutes")) {
                long m = targetPrem.optLong("unlocked_duration_minutes", 0);
                if (m < 0) return -1L;
                if (m > 0) return m * 60_000L;
            }
            if (rc != null) {
                for (String k : minKeys) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(k);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        try {
                            long m = Long.parseLong(rcVal.asString().trim());
                            if (m < 0) return -1L;
                            if (m > 0) return m * 60_000L;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // 2. Check hours config (root -> ad_data -> premium_config.<type> -> direct RC)
            if (rootObj != null) {
                for (String k : hourKeys) {
                    if (rootObj.has(k)) {
                        long h = rootObj.optLong(k, 24);
                        if (h < 0) return -1L;
                        return h * 3_600_000L;
                    }
                }
                JSONObject adData = rootObj.optJSONObject("ad_data");
                if (adData != null) {
                    for (String k : hourKeys) {
                        if (adData.has(k)) {
                            long h = adData.optLong(k, 24);
                            if (h < 0) return -1L;
                            return h * 3_600_000L;
                        }
                    }
                }
            }
            if (targetPrem != null && targetPrem.has("unlocked_duration_hours")) {
                long h = targetPrem.optLong("unlocked_duration_hours", 24);
                if (h < 0) return -1L;
                return h * 3_600_000L;
            }
            if (rc != null) {
                for (String k : hourKeys) {
                    com.google.firebase.remoteconfig.FirebaseRemoteConfigValue rcVal = rc.getValue(k);
                    if (rcVal != null && rcVal.getSource() == com.google.firebase.remoteconfig.FirebaseRemoteConfig.VALUE_SOURCE_REMOTE) {
                        try {
                            long h = Long.parseLong(rcVal.asString().trim());
                            if (h < 0) return -1L;
                            return h * 3_600_000L;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}

        // Default = 24 Hours
        return 24L * 3_600_000L;
    }

    public void saveTemplateUnlockTime(int postId) {
        if (postId <= 0 || prefs == null) return;
        prefs.edit().putLong("poster_unlock_time_" + postId, System.currentTimeMillis()).apply();
    }

    public boolean isTemplateUnlocked(int postId) {
        if (postId <= 0 || prefs == null) return false;
        String key = "poster_unlock_time_" + postId;
        long unlockTime = prefs.getLong(key, 0L);
        if (unlockTime <= 0L) return false;

        long durationMs = getUnlockDurationMs("templates");
        if (durationMs < 0L) return true; // -1 = Permanent unlock
        if (durationMs == 0L) {
            prefs.edit().remove(key).apply();
            return false;
        }

        long elapsed = System.currentTimeMillis() - unlockTime;
        if (elapsed >= 0L && elapsed < durationMs) {
            return true;
        }

        // Expired -> clear timestamp and re-lock
        prefs.edit().remove(key).apply();
        return false;
    }

    public void saveBackgroundUnlockTime(String bgPathOrId) {
        if (bgPathOrId == null || bgPathOrId.trim().isEmpty() || prefs == null) return;
        prefs.edit().putLong("bg_unlock_time_" + bgPathOrId.trim(), System.currentTimeMillis()).apply();
    }

    public boolean isBackgroundUnlocked(String bgPathOrId) {
        if (bgPathOrId == null || bgPathOrId.trim().isEmpty() || prefs == null) return false;
        String key = "bg_unlock_time_" + bgPathOrId.trim();
        long unlockTime = prefs.getLong(key, 0L);
        if (unlockTime <= 0L) return false;

        long durationMs = getUnlockDurationMs("backgrounds");
        if (durationMs < 0L) return true; // -1 = Permanent unlock
        if (durationMs == 0L) {
            prefs.edit().remove(key).apply();
            return false;
        }

        long elapsed = System.currentTimeMillis() - unlockTime;
        if (elapsed >= 0L && elapsed < durationMs) {
            return true;
        }

        // Expired -> clear timestamp and re-lock
        prefs.edit().remove(key).apply();
        return false;
    }

    public boolean isExitDialogNativeAdEnabled() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            // 1. app_exit_dialog from JSON
            String exitJson = rc.getString("app_exit_dialog");
            if (exitJson != null && exitJson.trim().startsWith("{")) {
                JSONObject obj = new JSONObject(exitJson);
                if (obj.has("enabled") && !obj.optBoolean("enabled", true)) return false;
                if (obj.has("native_ad")) {
                    JSONObject nad = obj.optJSONObject("native_ad");
                    if (nad != null && nad.has("enabled")) return nad.optBoolean("enabled", true);
                }
            }
            // 2. editor.exit_dialog_native from JSON
            String editorJson = rc.getString("editor");
            if (editorJson != null && editorJson.trim().startsWith("{")) {
                JSONObject obj = new JSONObject(editorJson);
                if (obj.has("exit_dialog_native")) {
                    JSONObject nad = obj.optJSONObject("exit_dialog_native");
                    if (nad != null && nad.has("enabled")) return nad.optBoolean("enabled", true);
                }
            }
        } catch (Exception ignored) {}
        return getInt("show_native_exit", 1) == 1;
    }

    public String getExitDialogNativeAdId() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            String exitJson = rc.getString("app_exit_dialog");
            if (exitJson != null && exitJson.trim().startsWith("{")) {
                JSONObject obj = new JSONObject(exitJson);
                if (obj.has("native_ad")) {
                    JSONObject nad = obj.optJSONObject("native_ad");
                    if (nad != null && nad.has("ad_id")) {
                        String id = nad.optString("ad_id", "").trim();
                        if (!id.isEmpty() && !id.contains("xxxx")) return id;
                    }
                }
            }
            String editorJson = rc.getString("editor");
            if (editorJson != null && editorJson.trim().startsWith("{")) {
                JSONObject obj = new JSONObject(editorJson);
                if (obj.has("exit_dialog_native")) {
                    JSONObject nad = obj.optJSONObject("exit_dialog_native");
                    if (nad != null && nad.has("ad_id")) {
                        String id = nad.optString("ad_id", "").trim();
                        if (!id.isEmpty() && !id.contains("xxxx")) return id;
                    }
                }
            }
        } catch (Exception ignored) {}
        return getAdsId("NativeUnitID");
    }

    // =========================================================================
    // Rating & Review System Remote Config
    // =========================================================================

    public boolean isRatingDialogEnabled() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("rating_dialog_enabled");
                if ("false".equalsIgnoreCase(val)) {
                    return false;
                } else if ("true".equalsIgnoreCase(val)) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        try {
            String masterJson = getDataType("master_json");
            if (masterJson != null && !masterJson.isEmpty()) {
                org.json.JSONObject obj = new org.json.JSONObject(masterJson);
                if (obj.has("rating_dialog_enabled")) {
                    return obj.optBoolean("rating_dialog_enabled", true);
                }
            }
        } catch (Exception ignored) {}
        return getBoolean("rating_dialog_enabled", true);
    }

    public static boolean isRatingDialogEnabled(android.content.Context context) {
        if (context == null) return true;
        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
        return pref.isRatingDialogEnabled();
    }

    public boolean isRateSubmitted() {
        return getBoolean("rateSubmitted", false) || getBoolean("rate_submitted", false);
    }

    public void setRateSubmitted(boolean submitted) {
        setBoolean("rateSubmitted", submitted);
        setBoolean("rate_submitted", submitted);
    }

    public int getHomeVisitCount() {
        return getInt("home_visit_count", 0);
    }

    public int incrementHomeVisitCount() {
        int count = getHomeVisitCount() + 1;
        setInt("home_visit_count", count);
        return count;
    }

    public void setLong(String key, long value) {
        if (prefs != null) prefs.edit().putLong(key, value).apply();
    }

    public long getLong(String key, long def) {
        if (prefs != null) return prefs.getLong(key, def);
        return def;
    }

    public boolean isScreenshotDisabled() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("disable_screenshot")) return rootObj.optBoolean("disable_screenshot", false);
                if (rootObj.has("block_screenshot")) return rootObj.optBoolean("block_screenshot", false);
                if (rootObj.has("security")) {
                    JSONObject sec = rootObj.optJSONObject("security");
                    if (sec != null) {
                        if (sec.has("disable_screenshot")) return sec.optBoolean("disable_screenshot", false);
                        if (sec.has("block_screenshot")) return sec.optBoolean("block_screenshot", false);
                    }
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("disable_screenshot");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
                String val2 = rc.getString("block_screenshot");
                if (val2 != null && !val2.trim().isEmpty() && !val2.equals("null")) {
                    if ("true".equalsIgnoreCase(val2.trim()) || "1".equals(val2.trim())) return true;
                    if ("false".equalsIgnoreCase(val2.trim()) || "0".equals(val2.trim())) return false;
                }
            }
            if (rootObj != null) {
                if (rootObj.has("security")) {
                    JSONObject sec = rootObj.optJSONObject("security");
                    if (sec != null) {
                        if (sec.has("disable_screenshot")) return sec.optBoolean("disable_screenshot", false);
                        if (sec.has("block_screenshot")) return sec.optBoolean("block_screenshot", false);
                    }
                }
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null) {
                        if (adData.has("disable_screenshot")) return adData.optBoolean("disable_screenshot", false);
                        if (adData.has("block_screenshot")) return adData.optBoolean("block_screenshot", false);
                    }
                }
                if (rootObj.has("disable_screenshot")) return rootObj.optBoolean("disable_screenshot", false);
                if (rootObj.has("block_screenshot")) return rootObj.optBoolean("block_screenshot", false);
            }
        } catch (Exception ignored) {}
        return false;
    }


    public boolean isAdBlockerDetectionEnabled() {
        try {
            JSONObject rootObj = getRootAdConfigJson();
            if (rootObj != null) {
                if (rootObj.has("ad_blocker_detection_enabled")) {
                    return rootObj.optBoolean("ad_blocker_detection_enabled", false);
                }
                if (rootObj.has("detect_ad_blocker")) {
                    return rootObj.optBoolean("detect_ad_blocker", false);
                }
                if (rootObj.has("ad_blocker_detection")) {
                    JSONObject obj = rootObj.optJSONObject("ad_blocker_detection");
                    if (obj != null && obj.has("enabled")) return obj.optBoolean("enabled", false);
                    return rootObj.optBoolean("ad_blocker_detection", false);
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                String val = rc.getString("ad_blocker_detection_enabled");
                if (val != null && !val.trim().isEmpty() && !val.equals("null")) {
                    if ("true".equalsIgnoreCase(val.trim()) || "1".equals(val.trim())) return true;
                    if ("false".equalsIgnoreCase(val.trim()) || "0".equals(val.trim())) return false;
                }
                String val2 = rc.getString("detect_ad_blocker");
                if (val2 != null && !val2.trim().isEmpty() && !val2.equals("null")) {
                    if ("true".equalsIgnoreCase(val2.trim()) || "1".equals(val2.trim())) return true;
                    if ("false".equalsIgnoreCase(val2.trim()) || "0".equals(val2.trim())) return false;
                }
            }
            if (rootObj != null) {
                if (rootObj.has("ad_blocker_detection")) {
                    JSONObject obj = rootObj.optJSONObject("ad_blocker_detection");
                    if (obj != null) {
                        if (obj.has("enabled")) return obj.optBoolean("enabled", false);
                    }
                    return rootObj.optBoolean("ad_blocker_detection", false);
                }
                if (rootObj.has("ad_blocker_detection_enabled")) {
                    return rootObj.optBoolean("ad_blocker_detection_enabled", false);
                }
                if (rootObj.has("detect_ad_blocker")) {
                    return rootObj.optBoolean("detect_ad_blocker", false);
                }
                if (rootObj.has("ad_data")) {
                    JSONObject adData = rootObj.optJSONObject("ad_data");
                    if (adData != null) {
                        if (adData.has("ad_blocker_detection_enabled")) return adData.optBoolean("ad_blocker_detection_enabled", false);
                        if (adData.has("detect_ad_blocker")) return adData.optBoolean("detect_ad_blocker", false);
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }


    public float getPremiumPosterBlurRadius() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                if (root.has("premium_poster_blur_radius")) {
                    double r = root.optDouble("premium_poster_blur_radius", 0);
                    if (r > 0) return (float) r;
                }
                if (root.has("blur_radius")) {
                    double r = root.optDouble("blur_radius", 0);
                    if (r > 0) return (float) r;
                }
            }
            int r = getInt("premium_poster_blur_radius", 0);
            if (r > 0) return (float) r;
            r = getInt("blur_radius", 0);
            if (r > 0) return (float) r;
        } catch (Exception ignored) {}
        return 2.5f;
    }

    public boolean isWatermarkRemovePopupEnabled() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"watermark_remove_popup_enabled", "show_watermark_remove_popup", "watermark_popup_enabled"}) {
                    if (root.has(k)) {
                        Object val = root.opt(k);
                        if (val instanceof Boolean) return (Boolean) val;
                        if (val instanceof Number) return ((Number) val).intValue() == 1;
                        if (val instanceof String) {
                            String s = ((String) val).trim();
                            if ("0".equals(s) || "false".equalsIgnoreCase(s)) return false;
                            if ("1".equals(s) || "true".equalsIgnoreCase(s)) return true;
                        }
                    }
                }
            }

            JSONObject obj = getDownloadRewardConfigJson();
            if (obj != null) {
                for (String k : new String[]{"watermark_remove_popup_enabled", "show_watermark_remove_popup", "watermark_popup_enabled"}) {
                    if (obj.has(k)) {
                        return obj.optBoolean(k, true);
                    }
                }
            }

            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"watermark_remove_popup_enabled", "show_watermark_remove_popup", "watermark_popup_enabled"}) {
                    String val = rc.getString(k);
                    if ("false".equalsIgnoreCase(val) || "0".equals(val)) return false;
                    if ("true".equalsIgnoreCase(val) || "1".equals(val)) return true;
                }
            }
        } catch (Exception ignored) {}
        return true;
    }

    public void setString(String key, String value) {
        if (prefs != null) prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String def) {
        if (prefs != null) return prefs.getString(key, def);
        return def;
    }

    public long getRemoteConfigTimeoutMs() {
        return 2500L;
    }

    public long getSplashWatchdogTimeoutMs() {
        try {
            JSONObject root = getRootAdConfigJson();
            if (root != null) {
                for (String k : new String[]{"splash_watchdog_timeout_ms", "splash_ad_wait_ms"}) {
                    if (root.has(k)) {
                        long v = root.optLong(k, -1L);
                        if (v >= 500L) return v;
                    }
                }
                if (root.has("ad_data")) {
                    JSONObject adData = root.optJSONObject("ad_data");
                    if (adData != null) {
                        for (String k : new String[]{"splash_watchdog_timeout_ms", "splash_ad_wait_ms"}) {
                            if (adData.has(k)) {
                                long v = adData.optLong(k, -1L);
                                if (v >= 500L) return v;
                            }
                        }
                    }
                }
            }
            JSONObject splashObj = getSplashConfigJson();
            if (splashObj != null && splashObj.has("watchdog_timeout_ms")) {
                long v = splashObj.optLong("watchdog_timeout_ms", -1L);
                if (v >= 500L) return v;
            }
            com.google.firebase.remoteconfig.FirebaseRemoteConfig rc = com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            if (rc != null) {
                for (String k : new String[]{"splash_watchdog_timeout_ms", "splash_ad_wait_ms"}) {
                    String s = rc.getString(k);
                    if (s != null && !s.trim().isEmpty() && !s.equals("null")) {
                        try {
                            long v = Long.parseLong(s.trim());
                            if (v >= 500L) return v;
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}
        return 3500L;
    }

}
