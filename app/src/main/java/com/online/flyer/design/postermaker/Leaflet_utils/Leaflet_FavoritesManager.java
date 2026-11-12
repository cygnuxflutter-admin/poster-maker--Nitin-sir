package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Leaflet_FavoritesManager {

    private static final String PREF_NAME = "Leaflet_Favorites";
    private static final String KEY_LIKED_POSTERS = "liked_posters";
    private static final String KEY_LIKED_BACKGROUNDS = "liked_backgrounds";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- Posters ---

    public static ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> getLikedPosters(Context context) {
        String json = getPrefs(context).getString(KEY_LIKED_POSTERS, "[]");
        Type type = new TypeToken<ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage>>() {}.getType();
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        checkAndUpdatePremiumPosters(context, list);
        return list;
    }

    public static void addLikedPoster(Context context, com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage poster) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> liked = getLikedPosters(context);
        boolean found = false;
        for (int i = 0; i < liked.size(); i++) {
            if (liked.get(i).getPost_id() == poster.getPost_id()) {
                found = true;
                if (poster.isPremium() && !liked.get(i).isPremium()) {
                    liked.get(i).setPremium(true);
                }
                if (poster.getCat_id() != 0 && liked.get(i).getCat_id() == 0) {
                    liked.get(i).setCat_id(poster.getCat_id());
                }
                saveLikedPosters(context, liked);
                break;
            }
        }
        if (!found) {
            liked.add(poster);
            saveLikedPosters(context, liked);
        }
    }

    public static void removeLikedPoster(Context context, String posterId) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> liked = getLikedPosters(context);
        for (int i = 0; i < liked.size(); i++) {
            if (String.valueOf(liked.get(i).getPost_id()).equals(posterId)) {
                liked.remove(i);
                saveLikedPosters(context, liked);
                break;
            }
        }
    }

    public static boolean isPosterLiked(Context context, String posterId) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> liked = getLikedPosters(context);
        for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage poster : liked) {
            if (String.valueOf(poster.getPost_id()).equals(posterId)) return true;
        }
        return false;
    }

    private static void saveLikedPosters(Context context, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> list) {
        getPrefs(context).edit().putString(KEY_LIKED_POSTERS, new Gson().toJson(list)).apply();
    }

    private static void checkAndUpdatePremiumPosters(Context context, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> list) {
        if (context == null || list == null || list.isEmpty()) return;
        boolean needsUpdate = false;
        try {
            Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
            int interval = pref.getPremiumPosterInterval("templates");
            if (interval <= 0) return;

            String cache = Leaflet_DataCacheManager.getInstance().getCache(context, Leaflet_DataCacheManager.KEY_TEMPLATES);
            if (cache == null || cache.isEmpty()) return;

            org.json.JSONArray rootArray = new org.json.JSONArray(cache);
            String field7 = pref.getDataType("field_7");
            String field10 = pref.getDataType("field_10");

            java.util.Map<Integer, Boolean> premiumMap = new java.util.HashMap<>();
            for (int i = 0; i < rootArray.length(); i++) {
                org.json.JSONObject catObj = rootArray.getJSONObject(i);
                if (catObj.has(field7)) {
                    org.json.JSONArray items = catObj.getJSONArray(field7);
                    for (int j = 0; j < items.length(); j++) {
                        org.json.JSONObject item = items.getJSONObject(j);
                        int pId = item.optInt(field10, -1);
                        if (pId != -1) {
                            boolean isPrem = (interval > 0 && (j + 1) % interval == 0);
                            if (isPrem) {
                                premiumMap.put(pId, true);
                            }
                        }
                    }
                }
            }

            for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage poster : list) {
                if (!poster.isPremium() && premiumMap.containsKey(poster.getPost_id())) {
                    poster.setPremium(true);
                    needsUpdate = true;
                }
            }
        } catch (Exception ignored) {}

        if (needsUpdate) {
            saveLikedPosters(context, list);
        }
    }

    // --- Backgrounds ---

    public static ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> getLikedBackgrounds(Context context) {
        String json = getPrefs(context).getString(KEY_LIKED_BACKGROUNDS, "[]");
        Type type = new TypeToken<ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage>>() {}.getType();
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> list = new Gson().fromJson(json, type);
        if (list == null) list = new ArrayList<>();
        checkAndUpdatePremiumBackgrounds(context, list);
        return list;
    }

    public static void addLikedBackground(Context context, com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage bg) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> liked = getLikedBackgrounds(context);
        boolean found = false;
        for (int i = 0; i < liked.size(); i++) {
            if (liked.get(i).getId() == bg.getId()) {
                found = true;
                if (bg.isPremium() && !liked.get(i).isPremium()) {
                    liked.get(i).setPremium(true);
                }
                saveLikedBackgrounds(context, liked);
                break;
            }
        }
        if (!found) {
            liked.add(bg);
            saveLikedBackgrounds(context, liked);
        }
    }

    public static void removeLikedBackground(Context context, String bgId) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> liked = getLikedBackgrounds(context);
        for (int i = 0; i < liked.size(); i++) {
            if (String.valueOf(liked.get(i).getId()).equals(bgId)) {
                liked.remove(i);
                saveLikedBackgrounds(context, liked);
                break;
            }
        }
    }

    public static boolean isBackgroundLiked(Context context, String bgId) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> liked = getLikedBackgrounds(context);
        for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage bg : liked) {
            if (String.valueOf(bg.getId()).equals(bgId)) return true;
        }
        return false;
    }

    private static void saveLikedBackgrounds(Context context, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> list) {
        getPrefs(context).edit().putString(KEY_LIKED_BACKGROUNDS, new Gson().toJson(list)).apply();
    }

    private static void checkAndUpdatePremiumBackgrounds(Context context, ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> list) {
        if (context == null || list == null || list.isEmpty()) return;
        boolean needsUpdate = false;
        try {
            Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(context);
            int interval = pref.getPremiumPosterInterval("backgrounds");
            if (interval <= 0) return;

            String cache = Leaflet_DataCacheManager.getInstance().getCache(context, Leaflet_DataCacheManager.KEY_BACKGROUNDS);
            if (cache == null || cache.isEmpty()) return;

            org.json.JSONArray rootArray = new org.json.JSONArray(cache);
            String field42 = pref.getDataType("field_42");
            String field43 = pref.getDataType("field_43");

            java.util.Map<Integer, Boolean> premiumMap = new java.util.HashMap<>();
            for (int i = 0; i < rootArray.length(); i++) {
                org.json.JSONObject catObj = rootArray.getJSONObject(i);
                if (catObj.has(field42)) {
                    org.json.JSONArray items = catObj.getJSONArray(field42);
                    for (int j = 0; j < items.length(); j++) {
                        org.json.JSONObject item = items.getJSONObject(j);
                        int bgId = item.optInt(field43, -1);
                        if (bgId != -1) {
                            boolean isPrem = (interval > 0 && (j + 1) % interval == 0);
                            if (isPrem) {
                                premiumMap.put(bgId, true);
                            }
                        }
                    }
                }
            }

            for (com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage bg : list) {
                if (!bg.isPremium() && premiumMap.containsKey(bg.getId())) {
                    bg.setPremium(true);
                    needsUpdate = true;
                }
            }
        } catch (Exception ignored) {}

        if (needsUpdate) {
            saveLikedBackgrounds(context, list);
        }
    }
}
