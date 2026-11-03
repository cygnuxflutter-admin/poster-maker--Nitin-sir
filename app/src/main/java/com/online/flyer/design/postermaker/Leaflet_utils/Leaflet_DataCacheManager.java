package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * High-performance Cache Manager for Poster Maker.
 * Implements Cache-First with Background Sync architecture.
 * Ensures 0-second instant screen rendering without blocking loaders.
 */
public class Leaflet_DataCacheManager {

    private static final String TAG = "Leaflet_DataCache";
    public static final String KEY_TEMPLATES = "templates_cache";
    public static final String KEY_BACKGROUNDS = "backgrounds_cache";

    private static Leaflet_DataCacheManager instance;

    public static synchronized Leaflet_DataCacheManager getInstance() {
        if (instance == null) {
            instance = new Leaflet_DataCacheManager();
        }
        return instance;
    }

    private File getCacheFile(Context context, String key) {
        if (context == null) return null;
        File dir = new File(context.getFilesDir(), "app_api_cache");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, key + ".json");
    }

    public synchronized void saveCache(Context context, String key, String jsonResponse) {
        if (context == null || key == null || jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return;
        }
        try {
            File file = getCacheFile(context, key);
            if (file == null) return;
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            Log.d(TAG, "Cache saved: " + key + " (" + file.length() + " bytes)");
        } catch (Exception e) {
            Log.e(TAG, "Error saving cache for " + key + ": " + e.getMessage());
        }
    }

    public synchronized String getCache(Context context, String key) {
        if (context == null || key == null) return null;
        try {
            File file = getCacheFile(context, key);
            if (file == null || !file.exists() || file.length() == 0) return null;

            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            fis.close();
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error reading cache for " + key + ": " + e.getMessage());
            return null;
        }
    }

    public boolean hasCache(Context context, String key) {
        if (context == null || key == null) return false;
        try {
            File file = getCacheFile(context, key);
            return file != null && file.exists() && file.length() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void clearCache(Context context, String key) {
        if (context == null || key == null) return;
        try {
            File file = getCacheFile(context, key);
            if (file != null && file.exists()) {
                file.delete();
            }
        } catch (Exception ignored) {}
    }

    /**
     * Preload Templates in the background (e.g. from MainActivity / Splash).
     * Warms up cache so when user enters Templates, it opens with 0 delay.
     */
    public void preloadTemplatesSilently(final Context context, final Leaflet_PreferenceClass preferenceClass) {
        if (context == null || preferenceClass == null) return;
        try {
            String baseUrl = preferenceClass.getDataType("field_1");
            String subPath = preferenceClass.getDataType("field_34");
            String endpoint = preferenceClass.getDataType("field_35");
            final String key = preferenceClass.getDataType("field_0");

            if (baseUrl == null || endpoint == null || baseUrl.isEmpty() || endpoint.isEmpty()) return;

            String requestUrl = baseUrl + subPath + endpoint;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
                if (response != null && !response.trim().isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int error = jsonObject.optInt(preferenceClass.getDataType("field_3"), -1);
                        if (error == 1) {
                            saveCache(context, KEY_TEMPLATES, response);
                            Log.d(TAG, "Preloaded Templates successfully!");
                        }
                    } catch (Exception ignored) {}
                }
            }, error -> {
                Log.d(TAG, "Preload Templates silent error: " + error.getMessage());
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postMap = new HashMap<>();
                    postMap.put(preferenceClass.getDataType("field_47"), key);
                    postMap.put(preferenceClass.getDataType("field_48"), "1");
                    postMap.put(preferenceClass.getDataType("field_46"), "0");
                    postMap.put(preferenceClass.getDataType("field_8"), "0");
                    return postMap;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context.getApplicationContext()).add(stringRequest);
        } catch (Exception e) {
            Log.e(TAG, "Preload Templates exception: " + e.getMessage());
        }
    }

    /**
     * Preload Backgrounds in the background.
     * Warms up cache so when user enters Backgrounds, it opens with 0 delay.
     */
    public void preloadBackgroundsSilently(final Context context, final Leaflet_PreferenceClass preferenceClass) {
        if (context == null || preferenceClass == null) return;
        try {
            String baseUrl = preferenceClass.getDataType("field_1");
            String subPath = preferenceClass.getDataType("field_34");
            String endpoint = preferenceClass.getDataType("field_38");
            final String key = preferenceClass.getDataType("field_0");

            if (baseUrl == null || endpoint == null || baseUrl.isEmpty() || endpoint.isEmpty()) return;

            String requestUrl = baseUrl + subPath + endpoint;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, response -> {
                if (response != null && !response.trim().isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has(preferenceClass.getDataType("field_39"))) {
                            saveCache(context, KEY_BACKGROUNDS, response);
                            Log.d(TAG, "Preloaded Backgrounds successfully!");
                        }
                    } catch (Exception ignored) {}
                }
            }, error -> {
                Log.d(TAG, "Preload Backgrounds silent error: " + error.getMessage());
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> postMap = new HashMap<>();
                    postMap.put(preferenceClass.getDataType("field_47"), key);
                    postMap.put(preferenceClass.getDataType("field_48"), "1");
                    return postMap;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Volley.newRequestQueue(context.getApplicationContext()).add(stringRequest);
        } catch (Exception e) {
            Log.e(TAG, "Preload Backgrounds exception: " + e.getMessage());
        }
    }
}
