package com.online.flyer.design.postermaker.Leaflet_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Leaflet_CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;
    private boolean mDeclined;
    private int mEdgeSlop;
    private boolean mWasEnabled;

    public Leaflet_CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 80dp from the top edge for system UI gestures (notification bar)
        mEdgeSlop = (int) (80 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // If touch is at the very top edge of the screen, completely disable SwipeRefreshLayout
                if (ev.getRawY() < mEdgeSlop) {
                    mWasEnabled = isEnabled();
                    setEnabled(false);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Restore enabled state when the gesture ends
                if (!isEnabled() && mWasEnabled) {
                    setEnabled(true);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Prevent refresh when scrolling horizontally (e.g., ViewPager, horizontal lists)
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mDeclined = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                
                if (mDeclined || xDiff > mTouchSlop) {
                    mDeclined = true;
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}
