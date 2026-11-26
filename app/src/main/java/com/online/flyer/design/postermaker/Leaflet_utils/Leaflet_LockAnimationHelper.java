package com.online.flyer.design.postermaker.Leaflet_utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.online.flyer.design.postermaker.R;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Helper class that provides a smooth, eye-catching "Lock Open -> Lock Close" animation
 * for ad-locked posters, templates, and backgrounds.
 */
public class Leaflet_LockAnimationHelper {

    private static final long CYCLE_INTERVAL_MS = 2800L;
    private static final long OPEN_DURATION_MS = 650L;

    // Track active loop runnables per container view to prevent duplicate loops and memory leaks
    private static final Map<View, Runnable> activeRunnables = new WeakHashMap<>();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Starts the periodic Lock Open / Close animation on the specified lock container and icon.
     *
     * @param container The background container/circle badge (e.g., fl_center_lock).
     * @param lockIcon  The ImageView displaying the padlock (e.g., iv_center_lock).
     */
    public static void startLockAnimation(@Nullable final View container, @Nullable final ImageView lockIcon) {
        if (container == null || lockIcon == null) return;

        // Stop any previous animation loop on this view
        stopLockAnimation(container, lockIcon);

        // Ensure default closed state
        lockIcon.setImageResource(R.drawable.ic_poster_center_lock);
        container.setScaleX(1.0f);
        container.setScaleY(1.0f);
        container.setRotation(0f);

        final Runnable cycleRunnable = new Runnable() {
            @Override
            public void run() {
                if (!ViewCompat.isAttachedToWindow(container) || container.getVisibility() != View.VISIBLE) {
                    // Reschedule check
                    mainHandler.postDelayed(this, CYCLE_INTERVAL_MS);
                    return;
                }

                // 1. POP OPEN: Switch to Open Lock + scale up + slight wiggle
                lockIcon.setImageResource(R.drawable.ic_poster_center_unlock);

                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(container, View.SCALE_X, 1.0f, 1.15f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(container, View.SCALE_Y, 1.0f, 1.15f);
                ObjectAnimator rotateWiggle = ObjectAnimator.ofFloat(container, View.ROTATION, 0f, -6f, 6f, 0f);

                AnimatorSet openSet = new AnimatorSet();
                openSet.playTogether(scaleUpX, scaleUpY, rotateWiggle);
                openSet.setDuration(220);
                openSet.setInterpolator(new DecelerateInterpolator());
                openSet.start();

                // 2. HOLD OPEN for OPEN_DURATION_MS, THEN SNAP SHUT (Closed Lock)
                mainHandler.postDelayed(() -> {
                    if (!ViewCompat.isAttachedToWindow(container)) return;

                    lockIcon.setImageResource(R.drawable.ic_poster_center_lock);

                    ObjectAnimator snapDownX = ObjectAnimator.ofFloat(container, View.SCALE_X, 1.15f, 0.94f, 1.0f);
                    ObjectAnimator snapDownY = ObjectAnimator.ofFloat(container, View.SCALE_Y, 1.15f, 0.94f, 1.0f);

                    AnimatorSet closeSet = new AnimatorSet();
                    closeSet.playTogether(snapDownX, snapDownY);
                    closeSet.setDuration(220);
                    closeSet.setInterpolator(new OvershootInterpolator(1.8f));
                    closeSet.start();

                }, OPEN_DURATION_MS);

                // Schedule next cycle
                mainHandler.postDelayed(this, CYCLE_INTERVAL_MS);
            }
        };

        activeRunnables.put(container, cycleRunnable);
        // Start first cycle after a short initial stagger (800ms) so user can see it right away
        mainHandler.postDelayed(cycleRunnable, 800L);
    }

    /**
     * Stops the animation loop and resets the view to its initial state.
     */
    public static void stopLockAnimation(@Nullable final View container, @Nullable final ImageView lockIcon) {
        if (container == null) return;

        Runnable r = activeRunnables.remove(container);
        if (r != null) {
            mainHandler.removeCallbacks(r);
        }

        container.animate().cancel();
        container.setScaleX(1.0f);
        container.setScaleY(1.0f);
        container.setRotation(0f);

        if (lockIcon != null) {
            lockIcon.setImageResource(R.drawable.ic_poster_center_lock);
        }
    }
}
