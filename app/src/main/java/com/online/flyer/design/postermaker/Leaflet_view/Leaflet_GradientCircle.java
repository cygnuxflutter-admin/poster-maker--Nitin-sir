package com.online.flyer.design.postermaker.Leaflet_view;

import android.graphics.Color;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

public class Leaflet_GradientCircle extends Circle {
    @Override
    public Sprite[] onCreateChild() {
        Sprite[] sprites = super.onCreateChild();
        
        // Image shows a gradient of dots from Cyan/Light Blue -> Dark Blue -> Purple
        String[] colorHex = {
                "#00FFFF", // Cyan
                "#00D4FF",
                "#00AAFF",
                "#0080FF",
                "#0055FF", // Dark Blue
                "#3333FF",
                "#661AFF", // Purple
                "#9900FF",
                "#B233FF",
                "#CC66FF",
                "#E599FF",
                "#FFCCFF"  // Light purple/pink
        };

        if (sprites != null) {
            for (int i = 0; i < sprites.length; i++) {
                if (i < colorHex.length) {
                    sprites[i].setColor(Color.parseColor(colorHex[i]));
                }
            }
        }
        
        return sprites;
    }
}
