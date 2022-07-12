package com.smartbooster.junkcleaner.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.smartbooster.junkcleaner.MainActivity;
import com.smartbooster.junkcleaner.R;
import com.smartbooster.junkcleaner.utils.SharePreferenceUtils;

import java.security.Provider;

public class ServiceManager extends Service {

  public static final String ACTION_MAX_BATTERY_CHANGED = "ACTION_MAX_BATTERY_CHANGED";

  public static final String ACTION_MAX_BATTERY_CHANGED_SEND = "ACTION_MAX_BATTERY_CHANGED_SEND";

  public static final String ACTION_MAX_BATTERY_NEED_UPDATE = "ACTION_MAX_BATTERY_NEED_UPDATE";

  public static final String NOTIFY_HOME = "com.smartbooster.junkcleaner";

  BatteryStatusReceiver mBatteryStatusReceiver;

  @Override
  public void onCreate() {
    super.onCreate();
//    if (Build.VERSION.SDK_INT >= 26) {
//      String str = "notification_channel_id";
//      ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel(str, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT));
//      startForeground(1, new NotificationCompat.Builder(this, "Channel human readable title").setOngoing(true).build());
//    }

    if (SharePreferenceUtils.getInstance(this).getNotification()) {
      startForeground(NotificationBattery.NOTIFYCATION_BATTERY_ID, NotificationBattery.getInstance(ServiceManager.this).build());
    } else {
      String str = "notification_channel_id";
      if (Build.VERSION.SDK_INT >= 26) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "notification_channel_id");
        Notification mNotification = mBuilder.setOngoing(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSmallIcon(android.R.color.transparent)
            .build();
        //  BatteryService.this.startForeground(1100, new Builder(BatteryService.this.getApplicationContext(), "notification_channel_id").build());
        ServiceManager.this.startForeground(1100, mNotification);


      }
    }
    resScreen();
    mBatteryStatusReceiver = new BatteryStatusReceiver();
    mBatteryStatusReceiver.OnCreate(this);

  }

  public void cancelNotification(int i) {
    try {
      NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      stopForeground(true);
      notificationManager.cancel(i);
    } catch (Exception e) {
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    //AlarmUtils.setAlarm(this,AlarmUtils.ACTION_REPEAT_SERVICE,AlarmUtils.TIME_REPREAT_SERVICE);
    super.onTaskRemoved(rootIntent);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public boolean onUnbind(Intent intent) {
    return false;
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(final Context context, Intent intent) {

      if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
        if (SharePreferenceUtils.getInstance(context).getKillApp()) {
//          mTaskScreenOff = new TaskScreenOff(context);
//          mTaskScreenOff.execute();

        }
        // AlarmUtils.cancel(context,AlarmUtils.ACTION_CHECK_DEVICE_STATUS);

      } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

//        SharePreferenceUtils.getInstance(context).setLevelScreenOn(Utils.getBatteryLevel(context));
//        AlarmUtils.setAlarm(context,AlarmUtils.ACTION_CHECK_DEVICE_STATUS,AlarmUtils.TIME_CHECK_DEVICE_STATUS);
      } else if (intent.getAction().equals(NotificationBattery.UPDATE_NOTIFICATION_ENABLE)) {

        switch (intent.getIntExtra("NOTIFICATION_UPDATE_MODE", 0)) {
          case 0:
            startForeground(NotificationBattery.NOTIFYCATION_BATTERY_ID, NotificationBattery.getInstance(context).build());
            Intent i = new Intent();
            intent.setAction(ServiceManager.ACTION_MAX_BATTERY_NEED_UPDATE);
            sendBroadcast(i);
            break;
          case 1:
           // cancelNotification(NotificationBattery.NOTIFYCATION_BATTERY_ID);

            break;
          default:
            break;
        }
      }

    }
  };

  public void resScreen() {
    IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
    filter.addAction("android.intent.action.SCREEN_OFF");
    filter.addAction(NotificationBattery.UPDATE_NOTIFICATION_ENABLE);

    this.registerReceiver(mReceiver, filter);
  }

}
