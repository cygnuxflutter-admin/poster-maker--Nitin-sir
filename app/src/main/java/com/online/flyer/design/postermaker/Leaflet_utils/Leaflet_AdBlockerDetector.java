package com.online.flyer.design.postermaker.Leaflet_utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;

import com.online.flyer.design.postermaker.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Leaflet_AdBlockerDetector {
    private static final String TAG = "AdBlockerDetector";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Dialog activeDialog = null;

    static {
        try {
            // Prevent JVM from caching negative DNS lookups so "Check Again" always gets fresh DNS results
            java.security.Security.setProperty("networkaddress.cache.negative.ttl", "0");
            java.security.Security.setProperty("networkaddress.cache.ttl", "1");
        } catch (Exception ignored) {}
    }

    public interface AdBlockerCallback {
        void onResult(boolean isAdBlockerActive);
    }

    /**
     * Asynchronously detects if an Ad Blocker or ad-blocking Private DNS is active.
     */
    public static void detect(final Context context, final AdBlockerCallback callback) {
        if (context == null) {
            if (callback != null) callback.onResult(false);
            return;
        }

        executor.execute(() -> {
            boolean isBlocked = false;
            try {
                isBlocked = checkIsBlocked(context);
            } catch (Throwable t) {
                Log.e(TAG, "Detection error: " + t.getMessage());
            }
            final boolean finalResult = isBlocked;
            Log.d(TAG, "AdBlocker Detection Finished: isBlocked = " + finalResult);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onResult(finalResult);
                }
            });
        });
    }

    private static boolean checkIsBlocked(Context context) {
        if (context == null) return false;

        // 1. Check Android Private DNS Specifier (Android 9+ / API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                String mode = Settings.Global.getString(context.getContentResolver(), "private_dns_mode");
                Log.d(TAG, "Active Private DNS mode: " + mode);

                if ("hostname".equalsIgnoreCase(mode)) {
                    String privateDns = Settings.Global.getString(context.getContentResolver(), "private_dns_specifier");
                    if (privateDns != null && !privateDns.trim().isEmpty()) {
                        String s = privateDns.toLowerCase().trim();
                        Log.d(TAG, "Active Private DNS specifier: " + s);
                        if (s.contains("adguard") || s.contains("adblock") || s.contains("nextdns")
                                || s.contains("dnsforge") || s.contains("ahadns") || s.contains("mullvad")
                                || s.contains("controld") || s.contains("rethink") || s.contains("cleanbrowsing")
                                || s.contains("block")) {
                            Log.w(TAG, "Ad-blocking Private DNS detected: " + s);
                            return true;
                        }
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, "Error checking Private DNS: " + e.getMessage());
            }
        }

        // 2. Control host test (Verify internet is genuinely working and stable)
        boolean isInternetWorking = false;
        try {
            InetAddress control = InetAddress.getByName("www.google.com");
            if (control != null && control.getHostAddress() != null && !control.getHostAddress().isEmpty()) {
                isInternetWorking = true;
            }
        } catch (Exception ignored) {
            try {
                InetAddress control2 = InetAddress.getByName("www.cloudflare.com");
                if (control2 != null && control2.getHostAddress() != null && !control2.getHostAddress().isEmpty()) {
                    isInternetWorking = true;
                }
            } catch (Exception ignored2) {}
        }

        // If control hosts failed (no internet or unstable), don't falsely flag as ad blocker
        if (!isInternetWorking) {
            Log.d(TAG, "Internet is offline or control host unreachable, skipping DNS adblock check");
            return false;
        }

        // 3. DNS resolution test for standard Google ad servers
        String[] adHosts = new String[]{
                "pagead2.googlesyndication.com",
                "googleads.g.doubleclick.net",
                "adservice.google.com"
        };

        int blackholedCount = 0;
        int failedCount = 0;
        for (String host : adHosts) {
            try {
                InetAddress address = InetAddress.getByName(host);
                String ip = address.getHostAddress();
                if (ip == null || ip.equals("127.0.0.1") || ip.equals("0.0.0.0") || address.isLoopbackAddress()) {
                    Log.w(TAG, "Host blackholed to loopback: " + host + " -> " + ip);
                    blackholedCount++;
                }
            } catch (UnknownHostException e) {
                Log.w(TAG, "Ad host failed to resolve (sinkholed/blocked): " + host);
                failedCount++;
            } catch (Throwable e) {
                Log.w(TAG, "Ad host resolution exception: " + host + " -> " + e.getMessage());
                failedCount++;
            }
        }

        // Definite ad blocker if ad domains are actively redirected to 127.0.0.1/0.0.0.0,
        // OR all 3 ad domains completely fail while control host resolved cleanly
        if (blackholedCount >= 1) {
            return true;
        }
        return failedCount >= 3;
    }

    /**
     * Shows the Ad Blocker popup. User must disable blocker and click 'Check Again' or 'Exit'.
     * Dialog is dynamically scaled to fit perfectly on all screen sizes and densities.
     */
    public static void showAdBlockerDialog(final Activity activity, final Runnable onResolved) {
        if (activity == null || activity.isFinishing()) {
            if (onResolved != null) onResolved.run();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            if (onResolved != null) onResolved.run();
            return;
        }

        if (activeDialog != null && activeDialog.isShowing()) {
            return;
        }

        try {
            final AppCompatDialog dialog = new AppCompatDialog(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
            dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.leaflet_dialog_ad_blocker);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    dialog.getWindow().setElevation(0f);
                }

                DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
                int screenWidth = metrics.widthPixels;

                // Responsive width: 88% of screen width, max 380dp, min 280dp
                int maxWidth = (int) (380 * metrics.density);
                int minWidth = (int) (280 * metrics.density);
                int targetWidth = (int) (screenWidth * 0.88f);
                if (targetWidth > maxWidth) targetWidth = maxWidth;
                if (targetWidth < minWidth && screenWidth > minWidth) targetWidth = minWidth;

                dialog.getWindow().setLayout(targetWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setGravity(Gravity.CENTER);
            }

            final Button btnCheckAgain = dialog.findViewById(R.id.btnCheckAgain);
            final Button btnOpenSettings = dialog.findViewById(R.id.btnOpenSettings);
            final View btnExitApp = dialog.findViewById(R.id.btnExitApp);

            if (btnCheckAgain != null) {
                btnCheckAgain.setOnClickListener(v -> {
                    btnCheckAgain.setEnabled(false);
                    btnCheckAgain.setText("Checking... ⏳");
                    detect(activity, isStillBlocked -> {
                        btnCheckAgain.setEnabled(true);
                        btnCheckAgain.setText("Check Again 🔄");
                        if (!isStillBlocked) {
                            try {
                                Toast.makeText(activity, "Thank you! Ad Blocker disabled.", Toast.LENGTH_SHORT).show();
                            } catch (Exception ignored) {}
                            safeDismissDialog(dialog);
                            activeDialog = null;
                            if (onResolved != null) {
                                onResolved.run();
                            }
                        } else {
                            try {
                                Toast.makeText(activity, "Ad Blocker or Private DNS is still active! Please turn it off in Settings.", Toast.LENGTH_LONG).show();
                            } catch (Exception ignored) {}
                        }
                    });
                });
            }

            if (btnOpenSettings != null) {
                btnOpenSettings.setOnClickListener(v -> {
                    Intent intent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        intent = new Intent("android.settings.NETWORK_PROVIDER_SETTINGS");
                    } else {
                        intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    }
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        try {
                            activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        } catch (Exception e2) {
                            try {
                                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                            } catch (Exception ignored) {}
                        }
                    }
                });
            }

            if (btnExitApp != null) {
                btnExitApp.setOnClickListener(v -> {
                    safeDismissDialog(dialog);
                    activeDialog = null;
                    try {
                        activity.finishAffinity();
                    } catch (Exception ignored) {
                        activity.finish();
                    }
                });
            }

            // Auto re-check when user returns from Settings to the app
            if (dialog.getWindow() != null && dialog.getWindow().getDecorView() != null) {
                dialog.getWindow().getDecorView().getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() {
                    @Override
                    public void onWindowFocusChanged(boolean hasFocus) {
                        if (hasFocus && dialog.isShowing()) {
                            detect(activity, isStillBlocked -> {
                                if (!isStillBlocked && dialog.isShowing()) {
                                    try {
                                        Toast.makeText(activity, "Thank you! Ad Blocker disabled.", Toast.LENGTH_SHORT).show();
                                    } catch (Exception ignored) {}
                                    safeDismissDialog(dialog);
                                    activeDialog = null;
                                    if (onResolved != null) {
                                        onResolved.run();
                                    }
                                }
                            });
                        }
                    }
                });
            }

            dialog.setOnKeyListener((d, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    try {
                        activity.finishAffinity();
                    } catch (Exception ignored) {
                        activity.finish();
                    }
                    return true;
                }
                return false;
            });

            activeDialog = dialog;
            dialog.show();

        } catch (Throwable e) {
            Log.e(TAG, "Error displaying Ad Blocker Dialog", e);
            activeDialog = null;
            // CRUCIAL: Never allow a display failure to permanently freeze splash screen!
            if (onResolved != null) {
                onResolved.run();
            }
        }
    }

    public static void dismissDialog() {
        if (activeDialog != null) {
            safeDismissDialog(activeDialog);
            activeDialog = null;
        }
    }

    private static void safeDismissDialog(Dialog dialog) {
        if (dialog != null) {
            try {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Exception ignored) {}
        }
    }
}
