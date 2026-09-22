package com.online.flyer.design.postermaker.Leaflet_utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.online.flyer.design.postermaker.Leaflet_view.Leaflet_GridLine;

public class Leaflet_RepeatListener implements OnTouchListener {
    private final Leaflet_GridLine guideline;
    private final Handler handler = new Handler();
    private final int initialInterval;
    private final int normalInterval;
    private RepeatInterface repeatInterface;
    private View downView;
    private final Runnable handlerRunnable = new Runnable() {
        public void run() {
            Leaflet_RepeatListener.this.handler.postDelayed(this, (long) Leaflet_RepeatListener.this.normalInterval);
            Leaflet_RepeatListener.this.repeatInterface.onClick(Leaflet_RepeatListener.this.downView);
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
            case 0:
                if (this.guideline.getVisibility() == View.GONE) {
                    this.guideline.setVisibility(View.VISIBLE);
                }
                this.handler.removeCallbacks(this.handlerRunnable);
                this.handler.postDelayed(this.handlerRunnable, (long) this.initialInterval);
                this.downView = view;
                this.downView.setPressed(true);
                this.repeatInterface.onClick(view);
                return true;
            case 1:
                this.repeatInterface.onFinish();
                this.guideline.setVisibility(View.GONE);
                break;
            case 3:
                break;
            default:
                return false;
        }
        this.handler.removeCallbacks(this.handlerRunnable);
        this.downView.setPressed(false);
        this.downView = null;
        return true;
    }

    public interface RepeatInterface {
        void onClick(View view);

        void onFinish();
    }
}
