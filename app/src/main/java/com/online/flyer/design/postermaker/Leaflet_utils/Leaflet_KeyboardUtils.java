package com.online.flyer.design.postermaker.Leaflet_utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Utility to reliably observe soft keyboard (IME) open/close events across Android 7.0 to 15+.
 */
public class Leaflet_KeyboardUtils {

    public interface OnKeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean isVisible);
    }

    public static boolean isKeyboardVisible(Activity activity) {
        if (activity == null || activity.isFinishing()) return false;
        View contentView = activity.findViewById(android.R.id.content);
        if (contentView == null) return false;

        // 1. Check WindowInsetsCompat if available (Android 11+ / API 30+)
        WindowInsetsCompat rootInsets = ViewCompat.getRootWindowInsets(contentView);
        if (rootInsets != null) {
            boolean imeVisible = rootInsets.isVisible(WindowInsetsCompat.Type.ime());
            if (imeVisible) return true;
        }

        // 2. Check visible display frame
        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        return keypadHeight > screenHeight * 0.15;
    }

    public static void attachKeyboardListener(@NonNull final Activity activity, @NonNull final OnKeyboardVisibilityListener listener) {
        final View contentView = activity.findViewById(android.R.id.content);
        if (contentView == null) return;

        final boolean[] lastReported = new boolean[]{false};

        // 1. Modern API 30+ Window Insets Listener (Handles Android 11, 12, 13, 14, 15)
        ViewCompat.setOnApplyWindowInsetsListener(contentView, (v, insets) -> {
            boolean isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            if (isImeVisible != lastReported[0]) {
                lastReported[0] = isImeVisible;
                contentView.post(() -> {
                    if (!activity.isFinishing()) {
                        listener.onKeyboardVisibilityChanged(isImeVisible);
                    }
                });
            }
            return ViewCompat.onApplyWindowInsets(v, insets);
        });

        // 2. GlobalLayoutListener for all Android versions (including older APIs and as a fallback)
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect r = new Rect();

            @Override
            public void onGlobalLayout() {
                if (activity.isFinishing()) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                    return;
                }

                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Threshold of 15% screen height safely distinguishes keyboard from system bars
                boolean isKeyboardOpen = keypadHeight > screenHeight * 0.15;
                if (isKeyboardOpen != lastReported[0]) {
                    lastReported[0] = isKeyboardOpen;
                    contentView.post(() -> {
                        if (!activity.isFinishing()) {
                            listener.onKeyboardVisibilityChanged(isKeyboardOpen);
                        }
                    });
                }
            }
        });
    }
}
