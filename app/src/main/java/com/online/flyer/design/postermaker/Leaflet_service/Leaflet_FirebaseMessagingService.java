package com.online.flyer.design.postermaker.Leaflet_service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.online.flyer.design.postermaker.Leaflet_activities.Leaflet_SplashScreen;
import com.online.flyer.design.postermaker.Leaflet_utils.Leaflet_NotificationDB;
import com.online.flyer.design.postermaker.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Leaflet_FirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";
    public static final String CHANNEL_ID = "postermaker_general";
    public static final String CHANNEL_NAME = "Poster Maker Updates";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Device Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = null;
        String body = null;
        String imageUrl = null;
        String launchUrl = null;

        // 1. Check Notification payload
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            title = notification.getTitle();
            body = notification.getBody();
            if (notification.getImageUrl() != null) {
                imageUrl = notification.getImageUrl().toString();
            }
        }

        // 2. Check Data payload (supports custom keys & data-only messages)
        Map<String, String> data = remoteMessage.getData();
        if (data != null && !data.isEmpty()) {
            if (title == null || title.isEmpty()) {
                title = data.get("title");
            }
            if (body == null || body.isEmpty()) {
                if (data.containsKey("body")) {
                    body = data.get("body");
                } else if (data.containsKey("message")) {
                    body = data.get("message");
                }
            }
            if (imageUrl == null || imageUrl.isEmpty()) {
                if (data.containsKey("image")) {
                    imageUrl = data.get("image");
                } else if (data.containsKey("image_url")) {
                    imageUrl = data.get("image_url");
                } else if (data.containsKey("imageUrl")) {
                    imageUrl = data.get("imageUrl");
                } else if (data.containsKey("picture")) {
                    imageUrl = data.get("picture");
                }
            }
            if (data.containsKey("launch_url")) {
                launchUrl = data.get("launch_url");
            } else if (data.containsKey("url")) {
                launchUrl = data.get("url");
            } else if (data.containsKey("link")) {
                launchUrl = data.get("link");
            }
        }

        if (title == null || title.isEmpty()) {
            title = getString(R.string.app_name);
        }
        if (body == null) {
            body = "";
        }

        // Save to local notification database so it appears in the Notification center
        try {
            Leaflet_NotificationDB db = new Leaflet_NotificationDB(this);
            db.addNotification(title, body, imageUrl, launchUrl);
        } catch (Exception e) {
            Log.e(TAG, "Error saving notification to local DB", e);
        }

        // Display push notification
        showNotification(title, body, imageUrl, launchUrl);
    }

    private void showNotification(String title, String body, String imageUrl, String launchUrl) {
        createNotificationChannel(this);

        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (intent == null) {
            intent = new Intent(this, Leaflet_SplashScreen.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("from_notification", true);
        if (launchUrl != null && !launchUrl.isEmpty()) {
            intent.putExtra("launch_url", launchUrl);
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        int notificationId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, flags);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        Bitmap bitmap = null;
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            bitmap = getBitmapFromUrl(imageUrl.trim());
        }

        if (bitmap != null) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .setSummaryText(body);
            notificationBuilder.setStyle(bigPictureStyle);
            notificationBuilder.setLargeIcon(bitmap);
        } else {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(body));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e(TAG, "Error downloading notification image: " + e.getMessage());
            return null;
        }
    }

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Poster Maker Updates and Announcements");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
