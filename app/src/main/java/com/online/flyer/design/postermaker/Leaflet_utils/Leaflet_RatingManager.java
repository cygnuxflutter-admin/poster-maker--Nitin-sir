package com.online.flyer.design.postermaker.Leaflet_utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.online.flyer.design.postermaker.R;

public class Leaflet_RatingManager {

    private static final String TAG = "LeafletRatingManager";

    /**
     * Triggered automatically on Home or Share screen.
     * Respects Remote Config and ensures user is not disturbed if already submitted.
     */
    public static void checkAndShowRatingDialog(Activity activity, String trigger) {
        if (activity == null || activity.isFinishing()) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return;
        }

        Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(activity);

        // 1. Check if disabled via Remote Config
        if (!pref.isRatingDialogEnabled()) {
            Log.d(TAG, "Rating dialog disabled by Remote Config. Skipping dialog.");
            return;
        }

        // 2. Check if user already submitted rating
        if (pref.isRateSubmitted()) {
            Log.d(TAG, "User has already submitted rating. Skipping dialog.");
            return;
        }

        // 3. Minimum Visit Count check (default 3 visits)
        int minVisits = pref.getInt("rate_dialog_min_visit_count", 3);
        if (pref.getHomeVisitCount() < minVisits) {
            Log.d(TAG, "Visit count (" + pref.getHomeVisitCount() + ") < minimum (" + minVisits + "). Skipping dialog.");
            return;
        }

        // 4. Cooldown check (default 5 days if user previously clicked 'Maybe Later')
        long lastDismiss = pref.getLong("rating_dialog_dismissed_time", 0);
        int cooldownDays = pref.getInt("rating_cooldown_days", 5);
        if (lastDismiss > 0 && (System.currentTimeMillis() - lastDismiss) < (cooldownDays * 24L * 60L * 60L * 1000L)) {
            Log.d(TAG, "Rating dialog is within " + cooldownDays + "-day cooldown period. Skipping dialog.");
            return;
        }

        showRatingDialogInternal(activity, trigger, false);
    }

    /**
     * Triggered manually by user (e.g. from Settings or Rate Us button).
     * Opens regardless of previous submission.
     */
    public static void showRatingDialog(Activity activity, String trigger) {
        if (activity == null || activity.isFinishing()) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            return;
        }
        showRatingDialogInternal(activity, trigger, true);
    }

    private static void showRatingDialogInternal(Activity activity, String trigger, boolean isManual) {
        try {
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.leaflet_dialog_rating);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            dialog.setCancelable(true);

            Leaflet_PreferenceClass pref = new Leaflet_PreferenceClass(activity);

            ImageView[] stars = new ImageView[]{
                    dialog.findViewById(R.id.iv_star_1),
                    dialog.findViewById(R.id.iv_star_2),
                    dialog.findViewById(R.id.iv_star_3),
                    dialog.findViewById(R.id.iv_star_4),
                    dialog.findViewById(R.id.iv_star_5)
            };

            TextView tvFeedback = dialog.findViewById(R.id.tv_rating_feedback);
            TextView btnSubmit = dialog.findViewById(R.id.btn_rate_submit);
            TextView btnMaybeLater = dialog.findViewById(R.id.btn_maybe_later);
            ImageView ivClose = dialog.findViewById(R.id.iv_rating_close);

            final int[] selectedStars = {5}; // Default 5 stars

            Runnable updateStarsUI = () -> {
                int count = selectedStars[0];
                for (int i = 0; i < stars.length; i++) {
                    ImageView starView = stars[i];
                    if (starView == null) continue;
                    if (i < count) {
                        starView.setImageResource(R.drawable.ic_star_filled);
                    } else {
                        starView.setImageResource(R.drawable.ic_star_empty);
                    }
                }

                if (tvFeedback != null && btnSubmit != null) {
                    switch (count) {
                        case 5:
                            tvFeedback.setText("Loved it! ⭐⭐⭐⭐⭐");
                            tvFeedback.setTextColor(Color.parseColor("#D97706"));
                            btnSubmit.setText("Rate on Google Play ⭐");
                            break;
                        case 4:
                            tvFeedback.setText("Great app! ⭐⭐⭐⭐");
                            tvFeedback.setTextColor(Color.parseColor("#D97706"));
                            btnSubmit.setText("Rate on Google Play ⭐");
                            break;
                        case 3:
                            tvFeedback.setText("It's okay ⭐⭐⭐");
                            tvFeedback.setTextColor(Color.parseColor("#475569"));
                            btnSubmit.setText("Submit");
                            break;
                        case 2:
                            tvFeedback.setText("Needs improvement ⭐⭐");
                            tvFeedback.setTextColor(Color.parseColor("#DC2626"));
                            btnSubmit.setText("Submit");
                            break;
                        case 1:
                        default:
                            tvFeedback.setText("Poor experience ⭐");
                            tvFeedback.setTextColor(Color.parseColor("#DC2626"));
                            btnSubmit.setText("Submit");
                            break;
                    }
                }
            };

            // Set default 5 stars
            updateStarsUI.run();

            // Star click listeners with bounce animation
            for (int i = 0; i < stars.length; i++) {
                final int starIndex = i + 1;
                ImageView starView = stars[i];
                if (starView != null) {
                    starView.setOnClickListener(v -> {
                        selectedStars[0] = starIndex;
                        v.animate().scaleX(1.3f).scaleY(1.3f).setDuration(100).withEndAction(() ->
                                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                        ).start();
                        updateStarsUI.run();
                    });
                }
            }

            // Maybe Later / Close button listener
            View.OnClickListener dismissListener = v -> {
                pref.setLong("rating_dialog_dismissed_time", System.currentTimeMillis());
                Leaflet_AnalyticsUtils.logRatingDialogAction(activity, "maybe_later", selectedStars[0]);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            };

            if (btnMaybeLater != null) btnMaybeLater.setOnClickListener(dismissListener);
            if (ivClose != null) ivClose.setOnClickListener(dismissListener);

            // Submit Button Listener
            if (btnSubmit != null) {
                btnSubmit.setOnClickListener(v -> {
                    int finalStars = selectedStars[0];

                    if (finalStars >= 4) {
                        // 4 or 5 Stars -> Open Google Play Store
                        pref.setRateSubmitted(true);
                        Leaflet_AnalyticsUtils.logRatingSubmitted(activity, finalStars, "play_store", trigger);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Leaflet_ShareUtils.rateUs(activity);
                    } else {
                        // 1, 2, or 3 Stars -> Thank user, log analytics, dismiss dialog & don't disturb again
                        pref.setRateSubmitted(true);
                        Leaflet_AnalyticsUtils.logRatingSubmitted(activity, finalStars, "internal_feedback", trigger);
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(activity, "Thank you for your valuable feedback! 🙏", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            dialog.show();
            Leaflet_AnalyticsUtils.logRatingDialogShown(activity, trigger);

        } catch (Exception e) {
            Log.e(TAG, "Error showing rating dialog: " + e.getMessage());
        }
    }
}
