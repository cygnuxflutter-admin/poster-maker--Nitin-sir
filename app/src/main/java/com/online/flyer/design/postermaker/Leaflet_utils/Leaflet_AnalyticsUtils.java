package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Leaflet_AnalyticsUtils {

    private static final String TAG = "LeafletAnalytics";
    private static FirebaseAnalytics sAnalytics;

    private static synchronized FirebaseAnalytics getAnalytics(@Nullable Context context) {
        if (sAnalytics == null && context != null) {
            try {
                sAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize FirebaseAnalytics: " + e.getMessage());
            }
        }
        return sAnalytics;
    }

    private static void logEvent(@Nullable Context context, @NonNull String eventName, @Nullable Bundle params) {
        try {
            FirebaseAnalytics analytics = getAnalytics(context);
            if (analytics != null) {
                if (params == null) {
                    params = new Bundle();
                }
                analytics.logEvent(eventName, params);
                Log.d(TAG, "Logged event: [" + eventName + "] with params: " + params.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error logging event [" + eventName + "]: " + e.getMessage());
        }
    }

    private static String safeStr(String str) {
        if (str == null) return "unknown";
        str = str.trim();
        if (str.length() > 99) {
            return str.substring(0, 99);
        }
        return str;
    }

    // =========================================================================
    // 1. SCREEN VISITS & NAVIGATION
    // =========================================================================

    public static void logScreenView(@Nullable Context context, String screenName) {
        logScreenView(context, screenName, "direct");
    }

    public static void logScreenView(@Nullable Context context, String screenName, String source) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", safeStr(screenName));
        bundle.putString("source", safeStr(source));
        logEvent(context, "screen_visit", bundle);
    }

    public static void logBackPress(@Nullable Context context, String screenName, String action) {
        Bundle bundle = new Bundle();
        bundle.putString("screen_name", safeStr(screenName));
        bundle.putString("action", safeStr(action));
        logEvent(context, "screen_back_press", bundle);
    }

    // =========================================================================
    // 2. CATEGORY & EXPLORATION
    // =========================================================================

    public static void logCategorySelected(@Nullable Context context, String categoryName, String categoryId, String source) {
        Bundle bundle = new Bundle();
        bundle.putString("category_name", safeStr(categoryName));
        bundle.putString("category_id", safeStr(categoryId));
        bundle.putString("source", safeStr(source));
        logEvent(context, "category_selected", bundle);
    }

    public static void logSearchQuery(@Nullable Context context, String query) {
        Bundle bundle = new Bundle();
        bundle.putString("search_query", safeStr(query));
        logEvent(context, "search_performed", bundle);
    }

    // =========================================================================
    // 3. POSTER / TEMPLATE INTERACTIONS
    // =========================================================================

    public static void logTemplateViewed(@Nullable Context context, String templateId, String categoryName, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("template_id", safeStr(templateId));
        bundle.putString("category_name", safeStr(categoryName));
        bundle.putInt("position", position);
        logEvent(context, "template_viewed", bundle);
    }

    public static void logTemplateSelected(@Nullable Context context, String templateId, String categoryName, boolean isFree, String action) {
        Bundle bundle = new Bundle();
        bundle.putString("template_id", safeStr(templateId));
        bundle.putString("category_name", safeStr(categoryName));
        bundle.putBoolean("is_free", isFree);
        bundle.putString("action", safeStr(action)); // "edit", "use_now", "preview"
        logEvent(context, "template_selected", bundle);
    }

    // =========================================================================
    // 4. BACKGROUND INTERACTIONS
    // =========================================================================

    public static void logBackgroundSourceSelected(@Nullable Context context, String sourceType) {
        // sourceType: "gallery", "camera", "solid_color", "online"
        Bundle bundle = new Bundle();
        bundle.putString("source_type", safeStr(sourceType));
        logEvent(context, "background_source_selected", bundle);
    }

    public static void logBackgroundSelected(@Nullable Context context, String bgId, String categoryOrColor) {
        Bundle bundle = new Bundle();
        bundle.putString("bg_id", safeStr(bgId));
        bundle.putString("detail", safeStr(categoryOrColor));
        logEvent(context, "background_selected", bundle);
    }

    // =========================================================================
    // 5. POSTER EDITOR & EDITING TOOLS
    // =========================================================================

    public static void logEditorOpened(@Nullable Context context, String templateType, boolean hasWatermark) {
        Bundle bundle = new Bundle();
        bundle.putString("template_type", safeStr(templateType));
        bundle.putBoolean("has_watermark", hasWatermark);
        logEvent(context, "editor_opened", bundle);
    }

    public static void logEditorToolUsed(@Nullable Context context, String toolName, String detail) {
        // toolName: "text_add", "text_edit", "text_color", "text_font", "sticker_add", 
        //           "effects", "background_change", "ratio_change", "layer_manage", "undo", "redo"
        Bundle bundle = new Bundle();
        bundle.putString("tool_name", safeStr(toolName));
        bundle.putString("detail", safeStr(detail));
        logEvent(context, "editor_tool_used", bundle);
    }

    // =========================================================================
    // 6. SAVE & DOWNLOAD ACTIONS
    // =========================================================================

    public static void logPosterSaveClick(@Nullable Context context, String actionType, boolean isFreeQuota) {
        // actionType: "save_template", "download_poster"
        Bundle bundle = new Bundle();
        bundle.putString("action_type", safeStr(actionType));
        bundle.putBoolean("is_free_quota", isFreeQuota);
        logEvent(context, "poster_save_click", bundle);
    }

    public static void logPosterSaveSuccess(@Nullable Context context, String actionType, boolean hasWatermark) {
        Bundle bundle = new Bundle();
        bundle.putString("action_type", safeStr(actionType));
        bundle.putBoolean("has_watermark", hasWatermark);
        logEvent(context, "poster_save_success", bundle);
    }

    // =========================================================================
    // 7. REWARDED ADS & WATERMARK INTERACTIONS
    // =========================================================================

    public static void logRewardDialogShown(@Nullable Context context, String rewardType) {
        // rewardType: "download", "save", "watermark", "undo_redo", "free_time_ended"
        Bundle bundle = new Bundle();
        bundle.putString("reward_type", safeStr(rewardType));
        logEvent(context, "reward_dialog_shown", bundle);
    }

    public static void logRewardDialogAction(@Nullable Context context, String rewardType, String userAction) {
        // userAction: "watch_video", "cancel", "no_thanks"
        Bundle bundle = new Bundle();
        bundle.putString("reward_type", safeStr(rewardType));
        bundle.putString("user_action", safeStr(userAction));
        logEvent(context, "reward_dialog_action", bundle);
    }

    public static void logRewardAdCompleted(@Nullable Context context, String rewardType) {
        Bundle bundle = new Bundle();
        bundle.putString("reward_type", safeStr(rewardType));
        logEvent(context, "reward_ad_completed", bundle);
    }

    // =========================================================================
    // 8. RATING & REVIEW SYSTEM
    // =========================================================================

    public static void logRatingDialogShown(@Nullable Context context, String trigger) {
        Bundle bundle = new Bundle();
        bundle.putString("trigger", safeStr(trigger)); // "home", "share", "manual"
        logEvent(context, "rating_dialog_shown", bundle);
    }

    public static void logRatingDialogAction(@Nullable Context context, String action, int stars) {
        Bundle bundle = new Bundle();
        bundle.putString("action", safeStr(action)); // "maybe_later", "close"
        bundle.putInt("stars", stars);
        logEvent(context, "rating_dialog_action", bundle);
    }

    public static void logRatingSubmitted(@Nullable Context context, int stars, String target, String trigger) {
        Bundle bundle = new Bundle();
        bundle.putInt("stars", stars);
        bundle.putString("target", safeStr(target)); // "play_store" (4-5 stars) or "internal_feedback" (1-3 stars)
        bundle.putString("trigger", safeStr(trigger)); // "home", "share", "manual"
        logEvent(context, "rating_dialog_submitted", bundle);
    }
}
