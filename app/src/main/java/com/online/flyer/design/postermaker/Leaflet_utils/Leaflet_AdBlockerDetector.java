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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.online.flyer.design.postermaker.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Leaflet_AdBlockerDetector {
    private static final String TAG = "AdBlockerDetector";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Dialog activeDialog = null;

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

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            boolean isBlocked = checkIsBlocked(context);
            Log.d(TAG, "AdBlocker Detection Finished: isBlocked = " + isBlocked);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onResult(isBlocked);
                }
            });
        });
    }

    private static boolean checkIsBlocked(Context context) {
        // 1. Check Android Private DNS Specifier (Android 9+ / API 28+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
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
            } catch (Exception e) {
                Log.e(TAG, "Error checking Private DNS: " + e.getMessage());
            }
        }

        // 2. Control host test (Verify internet is working first)
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

        // If no internet at all, don't falsely flag as ad blocker
        if (!isInternetWorking) {
            Log.d(TAG, "Internet is offline, skipping DNS adblock check");
            return false;
        }

        // 3. DNS resolution test for standard Google ad servers
        String[] adHosts = new String[]{
                "pagead2.googlesyndication.com",
                "googleads.g.doubleclick.net",
                "adservice.google.com"
        };

        int blockedCount = 0;
        for (String host : adHosts) {
            try {
                InetAddress address = InetAddress.getByName(host);
                String ip = address.getHostAddress();
                if (ip == null || ip.equals("127.0.0.1") || ip.equals("0.0.0.0") || address.isLoopbackAddress()) {
                    Log.w(TAG, "Host blackholed to loopback: " + host + " -> " + ip);
                    blockedCount++;
                }
            } catch (UnknownHostException e) {
                Log.w(TAG, "Ad host failed to resolve (sinkholed/blocked): " + host);
                blockedCount++;
            } catch (Exception e) {
                Log.w(TAG, "Ad host resolution exception: " + host + " -> " + e.getMessage());
                blockedCount++;
            }
        }

        // If 2 or more ad domains were sinkholed/blocked while internet is confirmed working:
        return blockedCount >= 2;
    }

    /**
     * Shows the Ad Blocker popup. User must disable blocker and click 'Check Again' or 'Exit'.
     */
    public static void showAdBlockerDialog(final Activity activity, final Runnable onResolved) {
        if (activity == null || activity.isFinishing()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) return;

        if (activeDialog != null && activeDialog.isShowing()) {
            return;
        }

        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.leaflet_dialog_ad_blocker);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            final Button btnCheckAgain = dialog.findViewById(R.id.btnCheckAgain);
            final Button btnOpenSettings = dialog.findViewById(R.id.btnOpenSettings);
            final TextView btnExitApp = dialog.findViewById(R.id.btnExitApp);

            if (btnCheckAgain != null) {
                btnCheckAgain.setOnClickListener(v -> {
                    btnCheckAgain.setEnabled(false);
                    btnCheckAgain.setText("Checking... ⏳");
                    detect(activity, isStillBlocked -> {
                        btnCheckAgain.setEnabled(true);
                        btnCheckAgain.setText("Check Again 🔄");
                        if (!isStillBlocked) {
                            Toast.makeText(activity, "Thank you! Ad Blocker disabled.", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            activeDialog = null;
                            if (onResolved != null) {
                                onResolved.run();
                            }
                        } else {
                            Toast.makeText(activity, "Ad Blocker or Private DNS is still active! Please turn it off.", Toast.LENGTH_LONG).show();
                        }
                    });
                });
            }

            if (btnOpenSettings != null) {
                btnOpenSettings.setOnClickListener(v -> {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    try {
                        activity.startActivity(intent);
                    } catch (Exception e) {
                        try {
                            activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                        } catch (Exception ignored) {}
                    }
                });
            }

            if (btnExitApp != null) {
                btnExitApp.setOnClickListener(v -> {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    activeDialog = null;
                    activity.finishAffinity();
                });
            }

            dialog.setOnKeyListener((d, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    activity.finishAffinity();
                    return true;
                }
                return false;
            });

            activeDialog = dialog;
            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error displaying Ad Blocker Dialog", e);
        }
    }

    public static void dismissDialog() {
        if (activeDialog != null && activeDialog.isShowing()) {
            try {
                activeDialog.dismiss();
            } catch (Exception ignored) {}
            activeDialog = null;
        }
    }
}
