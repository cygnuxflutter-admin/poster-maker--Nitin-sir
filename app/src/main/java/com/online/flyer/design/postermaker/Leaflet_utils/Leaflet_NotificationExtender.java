package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.Context;
import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal;

public class Leaflet_NotificationExtender implements OneSignal.OSRemoteNotificationReceivedHandler {
    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent notificationReceivedEvent) {
        OSNotification notification = notificationReceivedEvent.getNotification();
        
        String title = notification.getTitle();
        String body = notification.getBody();
        String image = notification.getBigPicture(); // or largeIcon
        String launchUrl = notification.getLaunchURL();
        
        Log.d("OneSignal", "Received: " + title + " - " + body);
        
        Leaflet_NotificationDB db = new Leaflet_NotificationDB(context);
        db.addNotification(title, body, image, launchUrl);
        
        // Allow the notification to display
        notificationReceivedEvent.complete(notification);
    }
}

