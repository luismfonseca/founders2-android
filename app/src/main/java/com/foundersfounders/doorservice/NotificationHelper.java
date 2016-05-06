package com.foundersfounders.doorservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
   private NotificationHelper() {}

   public static final int NOTIFICATION_ID = 0;

   public static void buildNotification(Context context, NotificationManager notificationManager) {
      PendingIntent emptyPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
      PendingIntent doorOpenService =
            PendingIntent.getService(
                  context,
                  0,
                  OpenDoorService.buildIntentOpenDoor(context),
                  PendingIntent.FLAG_ONE_SHOT);

      Notification.Action openFrontDoor =
            new Notification.Action.Builder(
                  android.R.drawable.sym_action_chat, "Open door", doorOpenService).build();

      Notification.Action openGarageDoor =
            new Notification.Action.Builder(
                  android.R.drawable.sym_action_chat, "Open garage", doorOpenService).build();

      Notification.Builder mBuilder =
            new Notification.Builder(context)
                  .setSmallIcon(android.R.drawable.sym_def_app_icon)
                  .setContentTitle("FoundersFounders door service")
                  .setContentText("It looks as though you are nearby...")
                  .setAutoCancel(true)
                  .addAction(openFrontDoor)
                  .addAction(openGarageDoor)
                  .setContentIntent(emptyPendingIntent);

      notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
   }
}
