package com.enginecal.kioskapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Cancel the notification
        cancelNotification(sbn.getKey());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Notification removed
    }
}
