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
        return new Gson().fromJson(json, type);
    }

    public static void addLikedPoster(Context context, com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage poster) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_PosterImage> liked = getLikedPosters(context);
        if (!isPosterLiked(context, String.valueOf(poster.getPost_id()))) {
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

    // --- Backgrounds ---

    public static ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> getLikedBackgrounds(Context context) {
        String json = getPrefs(context).getString(KEY_LIKED_BACKGROUNDS, "[]");
        Type type = new TypeToken<ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void addLikedBackground(Context context, com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage bg) {
        ArrayList<com.online.flyer.design.postermaker.Leaflet_model.Leaflet_BgImage> liked = getLikedBackgrounds(context);
        if (!isBackgroundLiked(context, String.valueOf(bg.getId()))) {
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
}
