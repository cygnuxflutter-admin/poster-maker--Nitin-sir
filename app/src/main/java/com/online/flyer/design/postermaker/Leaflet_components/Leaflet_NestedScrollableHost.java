package com.online.flyer.design.postermaker.Leaflet_components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class Leaflet_NestedScrollableHost extends FrameLayout {

    private int touchSlop;
    private float initialX;
    private float initialY;

    public Leaflet_NestedScrollableHost(Context context) {
        super(context);
        init(context);
    }

    public Leaflet_NestedScrollableHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Leaflet_NestedScrollableHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private View getScrollableChild() {
        View child = getChildCount() > 0 ? getChildAt(0) : null;
        if (child instanceof ViewPager2 || child instanceof RecyclerView) {
            return child;
        }
        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    private void handleInterceptTouchEvent(MotionEvent ev) {
        View scrollableChild = getScrollableChild();
        if (scrollableChild == null) return;

        boolean isHorizontal = false;
        if (scrollableChild instanceof ViewPager2) {
            isHorizontal = ((ViewPager2) scrollableChild).getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL;
        } else if (scrollableChild instanceof RecyclerView) {
            RecyclerView.LayoutManager lm = ((RecyclerView) scrollableChild).getLayoutManager();
            if (lm instanceof LinearLayoutManager) {
                isHorizontal = ((LinearLayoutManager) lm).getOrientation() == RecyclerView.HORIZONTAL;
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = ev.getX();
            initialY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = Math.abs(ev.getX() - initialX);
            float dy = Math.abs(ev.getY() - initialY);

            if (isHorizontal) {
                if (dx > touchSlop && dx > dy) {
                    if (canChildScroll(scrollableChild, ev.getX() - initialX)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else if (dy > touchSlop && dy > dx) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            }
        }
    }

    private boolean canChildScroll(View scrollableChild, float dx) {
        if (scrollableChild instanceof ViewPager2) {
            ViewPager2 vp = (ViewPager2) scrollableChild;
            if (dx > 0) return vp.getCurrentItem() > 0;
            else return vp.getCurrentItem() < (vp.getAdapter() != null ? vp.getAdapter().getItemCount() - 1 : 0);
        } else if (scrollableChild instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) scrollableChild;
            int direction = dx > 0 ? -1 : 1;
            return rv.canScrollHorizontally(direction);
        }
        return false;
    }
}
