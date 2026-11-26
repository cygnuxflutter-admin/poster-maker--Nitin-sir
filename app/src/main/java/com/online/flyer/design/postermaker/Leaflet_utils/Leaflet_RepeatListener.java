package com.online.flyer.design.postermaker.Leaflet_utils;

import android.os.Handler;
import android.os.Looper;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_GridLine;

public class Leaflet_RepeatListener implements OnTouchListener {
    private final Leaflet_GridLine guideline;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int initialInterval;
    private final int normalInterval;
    private RepeatInterface repeatInterface;
    private View downView;
    private int repeatCount = 0;

    private final Runnable handlerRunnable = new Runnable() {
        public void run() {
            if (Leaflet_RepeatListener.this.downView != null) {
                repeatCount++;

                // Subtle haptic pulse on movement ticks
                try {
                    Leaflet_RepeatListener.this.downView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                } catch (Exception ignored) {}

                // Micro-pulse animation while holding
                Leaflet_RepeatListener.this.downView.animate().cancel();
                Leaflet_RepeatListener.this.downView.setScaleX(0.82f);
                Leaflet_RepeatListener.this.downView.setScaleY(0.82f);
                Leaflet_RepeatListener.this.downView.animate()
                        .scaleX(0.86f).scaleY(0.86f)
                        .setDuration(50)
                        .start();

                if (Leaflet_RepeatListener.this.repeatInterface != null) {
                    Leaflet_RepeatListener.this.repeatInterface.onClick(Leaflet_RepeatListener.this.downView);
                }

                // Progressive acceleration: as user holds, interval shortens down to 45ms
                long nextInterval = Math.max(45, Leaflet_RepeatListener.this.normalInterval - (repeatCount * 5));
                Leaflet_RepeatListener.this.handler.postDelayed(this, nextInterval);
            }
        }
    };

    public Leaflet_RepeatListener(int initialInterval, int normalInterval, Leaflet_GridLine guideline, RepeatInterface repeatInterface) {
        if (repeatInterface == null) {
            throw new IllegalArgumentException("null runnable");
        } else if (initialInterval < 0 || normalInterval < 0) {
            throw new IllegalArgumentException("negative interval");
        } else {
            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.repeatInterface = repeatInterface;
            this.guideline = guideline;
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.downView = view;
                this.repeatCount = 0;

                // 1. Button Press Micro-Bounce Animation
                view.animate().cancel();
                view.animate()
                        .scaleX(0.85f).scaleY(0.85f)
                        .setDuration(90)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();

                // 2. Tactile Haptic Feedback
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                } catch (Exception ignored) {}

                // 3. Show alignment guideline if available
                if (this.guideline != null && this.guideline.getVisibility() == View.GONE) {
                    this.guideline.setVisibility(View.VISIBLE);
                }

                // 4. Start auto-repeat
                this.handler.removeCallbacks(this.handlerRunnable);
                this.handler.postDelayed(this.handlerRunnable, (long) this.initialInterval);
                this.downView.setPressed(true);

                // 5. Trigger initial move
                this.repeatInterface.onClick(view);
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 1. Button Spring Bounce Back
                view.animate().cancel();
                view.animate()
                        .scaleX(1.0f).scaleY(1.0f)
                        .setDuration(180)
                        .setInterpolator(new OvershootInterpolator(2.2f))
                        .start();

                // 2. Stop auto-repeat
                this.handler.removeCallbacks(this.handlerRunnable);

                // 3. Hide guideline
                if (this.guideline != null) {
                    this.guideline.setVisibility(View.GONE);
                }

                if (motionEvent.getAction() == MotionEvent.ACTION_UP && this.repeatInterface != null) {
                    this.repeatInterface.onFinish();
                }

                if (this.downView != null) {
                    this.downView.setPressed(false);
                    this.downView = null;
                }
                return true;

            default:
                return false;
        }
    }

    public interface RepeatInterface {
        void onClick(View view);

        void onFinish();
    }
}
