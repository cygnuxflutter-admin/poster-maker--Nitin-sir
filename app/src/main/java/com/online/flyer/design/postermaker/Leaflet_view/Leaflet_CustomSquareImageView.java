package com.online.flyer.design.postermaker.Leaflet_view;

import android.content.Context;
import android.util.AttributeSet;

public class Leaflet_CustomSquareImageView extends androidx.appcompat.widget.AppCompatImageView {
    public Leaflet_CustomSquareImageView(Context context) {
        super(context);
    }

    public Leaflet_CustomSquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
    }
}
