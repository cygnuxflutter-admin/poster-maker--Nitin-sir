package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterModel;
import com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgModel;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetTemplateData;
import com.online.flyer.design.postermaker.Leaflet_threadTask.Leaflet_GetBgData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    private ArrayList<Leaflet_PosterModel> memoryTemplatesCache;
    private ArrayList<Leaflet_BgModel> memoryBackgroundsCache;

    public synchronized ArrayList<Leaflet_PosterModel> getMemoryTemplatesCache() {
        return memoryTemplatesCache;
    }

    public synchronized void setMemoryTemplatesCache(ArrayList<Leaflet_PosterModel> list) {
        this.memoryTemplatesCache = list;
    }

    public synchronized ArrayList<Leaflet_BgModel> getMemoryBackgroundsCache() {
        return memoryBackgroundsCache;
    }

    public synchronized void setMemoryBackgroundsCache(ArrayList<Leaflet_BgModel> list) {
        this.memoryBackgroundsCache = list;
    }

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
     * Warm up Glide disk/memory cache for the first visible templates thumbnails.
     */
    public void warmupTemplatesImages(Context context, String jsonResponse, Leaflet_PreferenceClass preferenceClass) {
        if (context == null || jsonResponse == null || preferenceClass == null) return;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray categories = jsonObject.optJSONArray(preferenceClass.getDataType("field_4"));
            if (categories == null || categories.length() == 0) return;

            String posterArrayKey = preferenceClass.getDataType("field_7");
            String thumbKey = preferenceClass.getDataType("field_11");

            // Warm up first 2 categories' first 10 posters each
            int maxCats = Math.min(2, categories.length());
            for (int c = 0; c < maxCats; c++) {
                JSONObject catObj = categories.optJSONObject(c);
                if (catObj == null) continue;
                JSONArray posters = catObj.optJSONArray(posterArrayKey);
                if (posters == null) continue;

                int maxPosters = Math.min(10, posters.length());
                for (int p = 0; p < maxPosters; p++) {
                    JSONObject posterObj = posters.optJSONObject(p);
                    if (posterObj == null) continue;
                    String thumbUrl = posterObj.optString(thumbKey, "");
                    if (thumbUrl != null && !thumbUrl.trim().isEmpty()) {
                        try {
                            Glide.with(context.getApplicationContext())
                                    .load(thumbUrl)
                                    .priority(com.bumptech.glide.Priority.LOW)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .preload();
                        } catch (Exception ignored) {}
                    }
                }
            }
            Log.d(TAG, "Warmed up templates thumbnails in Glide cache!");
        } catch (Exception e) {
            Log.e(TAG, "warmupTemplatesImages error: " + e.getMessage());
        }
    }

    /**
     * Warm up Glide disk/memory cache for the first visible backgrounds thumbnails.
     */
    public void warmupBackgroundsImages(Context context, String jsonResponse, Leaflet_PreferenceClass preferenceClass) {
        if (context == null || jsonResponse == null || preferenceClass == null) return;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray categories = jsonObject.optJSONArray(preferenceClass.getDataType("field_39"));
            if (categories == null || categories.length() == 0) return;

            String bgArrayKey = preferenceClass.getDataType("field_42");
            String thumbKey = preferenceClass.getDataType("field_44");

            // Warm up first 2 categories' first 10 backgrounds each
            int maxCats = Math.min(2, categories.length());
            for (int c = 0; c < maxCats; c++) {
                JSONObject catObj = categories.optJSONObject(c);
                if (catObj == null) continue;
                JSONArray bgs = catObj.optJSONArray(bgArrayKey);
                if (bgs == null) continue;

                int maxBgs = Math.min(10, bgs.length());
                for (int b = 0; b < maxBgs; b++) {
                    JSONObject bgObj = bgs.optJSONObject(b);
                    if (bgObj == null) continue;
                    String thumbUrl = bgObj.optString(thumbKey, "");
                    if (thumbUrl != null && !thumbUrl.trim().isEmpty()) {
                        try {
                            Glide.with(context.getApplicationContext())
                                    .load(thumbUrl)
                                    .priority(com.bumptech.glide.Priority.LOW)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .preload();
                        } catch (Exception ignored) {}
                    }
                }
            }
            Log.d(TAG, "Warmed up backgrounds thumbnails in Glide cache!");
        } catch (Exception e) {
            Log.e(TAG, "warmupBackgroundsImages error: " + e.getMessage());
        }
    }

    /**
     * Parse Templates JSON into in-memory Java models in the background.
     */
    public void parseTemplatesToMemory(final Context context, final String jsonResponse, final Leaflet_PreferenceClass preferenceClass) {
        if (jsonResponse == null || preferenceClass == null) return;
        if (memoryTemplatesCache != null && !memoryTemplatesCache.isEmpty()) return;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.optJSONArray(preferenceClass.getDataType("field_4"));
            if (jsonArray != null && jsonArray.length() > 0) {
                new Leaflet_GetTemplateData(preferenceClass, jsonArray, new Leaflet_GetTemplateData.OnGetCatDataListener() {
                    @Override
                    public void onGetDataComplete(ArrayList<Leaflet_PosterModel> posterModels) {
                        setMemoryTemplatesCache(posterModels);
                        Log.d(TAG, "Templates parsed into memory cache: " + posterModels.size() + " categories ready!");
                    }

                    @Override
                    public void onError() {}
                }).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseTemplatesToMemory error: " + e.getMessage());
        }
    }

    /**
     * Parse Backgrounds JSON into in-memory Java models in the background.
     */
    public void parseBackgroundsToMemory(final Context context, final String jsonResponse, final Leaflet_PreferenceClass preferenceClass) {
        if (jsonResponse == null || preferenceClass == null) return;
        if (memoryBackgroundsCache != null && !memoryBackgroundsCache.isEmpty()) return;
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.optJSONArray(preferenceClass.getDataType("field_39"));
            if (jsonArray != null && jsonArray.length() > 0) {
                new Leaflet_GetBgData(preferenceClass, jsonArray, new Leaflet_GetBgData.OnGetCatDataListener() {
                    @Override
                    public void onGetDataComplete(ArrayList<Leaflet_BgModel> bgModels) {
                        setMemoryBackgroundsCache(bgModels);
                        Log.d(TAG, "Backgrounds parsed into memory cache: " + bgModels.size() + " categories ready!");
                    }

                    @Override
                    public void onError() {}
                }).executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseBackgroundsToMemory error: " + e.getMessage());
        }
    }

    /**
     * Preload Templates in the background (e.g. from MainActivity / Splash).
     * Warms up cache so when user enters Templates, it opens with 0 delay.
     */
    public void preloadTemplatesSilently(final Context context, final Leaflet_PreferenceClass preferenceClass) {
        if (context == null || preferenceClass == null) return;

        // Immediate disk cache warmup & memory parse
        String existingCache = getCache(context, KEY_TEMPLATES);
        if (existingCache != null && !existingCache.trim().isEmpty()) {
            warmupTemplatesImages(context, existingCache, preferenceClass);
            parseTemplatesToMemory(context, existingCache, preferenceClass);
        }

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
                            warmupTemplatesImages(context, response, preferenceClass);
                            parseTemplatesToMemory(context, response, preferenceClass);
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

        // Immediate disk cache warmup & memory parse
        String existingCache = getCache(context, KEY_BACKGROUNDS);
        if (existingCache != null && !existingCache.trim().isEmpty()) {
            warmupBackgroundsImages(context, existingCache, preferenceClass);
            parseBackgroundsToMemory(context, existingCache, preferenceClass);
        }

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
                            warmupBackgroundsImages(context, response, preferenceClass);
                            parseBackgroundsToMemory(context, response, preferenceClass);
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
