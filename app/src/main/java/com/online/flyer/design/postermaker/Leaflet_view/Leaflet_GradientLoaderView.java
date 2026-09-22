package com.online.flyer.design.postermaker.Leaflet_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class Leaflet_GradientLoaderView extends ProgressBar {

    public Leaflet_GradientLoaderView(Context context) {
        super(context);
        init();
    }

    public Leaflet_GradientLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Leaflet_GradientLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setIndeterminate(true);
        setIndeterminateDrawable(new Leaflet_GradientCircle());
    }
}
