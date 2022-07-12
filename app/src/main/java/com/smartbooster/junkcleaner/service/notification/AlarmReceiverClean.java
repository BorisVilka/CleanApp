package com.smartbooster.junkcleaner.service.notification;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.smartbooster.junkcleaner.MainActivity;
import com.smartbooster.junkcleaner.R;
import com.smartbooster.junkcleaner.SplashActivity;

public class AlarmReceiverClean extends BroadcastReceiver {
  private static final String CHANNEL_ID = "exampleServiceChannel";


  @Override
  public void onReceive(Context context, Intent intent) {

//    FireBaseRemoteConfig();
//    getData();


    Intent notificationIntent = new Intent(context, SplashActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
    notificationIntent.putExtra("Notification", notificationIntent);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(notificationIntent);


    RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.optimize_phone_notice);

//    notificationLayout.setTextViewText(R.id.tvNotiTitle, "Clean");

    Notification.Builder builder = new Notification.Builder(context);

    Notification notification = builder.setContentTitle("Notification")
        .setContent(notificationLayout)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent).build();

//    .setContentTitle(remoteConfig.getString("notice_saver")) //set title of notification
//        .setContentText("This is a notification message")//this is notification message
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setChannelId(CHANNEL_ID);
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(
          CHANNEL_ID,
          "Notification",
          IMPORTANCE_DEFAULT
      );
      notificationManager.createNotificationChannel(channel);
    }

    notificationManager.notify(200, notification);

  }

}
