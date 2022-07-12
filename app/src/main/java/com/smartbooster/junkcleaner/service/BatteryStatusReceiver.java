package com.smartbooster.junkcleaner.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import androidx.core.app.NotificationCompat;

import com.smartbooster.junkcleaner.BatteryMode.BatteryInfo;
import com.smartbooster.junkcleaner.utils.SharePreferenceUtils;

public class BatteryStatusReceiver extends BroadcastReceiver {

  Context mContext = null;
  NotificationManager mNotificationManager;
  NotificationCompat.Builder mBuilder;

  BatteryInfo mBatteryInfo = new BatteryInfo();

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {

      if(SharePreferenceUtils.getInstance(mContext).getNotification()){
        NotificationBattery.getInstance(mContext)
            .updateNotify(mBatteryInfo.temperature);
      }

    }
  }

  public final void OnCreate(Context context) {
    mContext = context.getApplicationContext();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
    intentFilter.addAction(Intent.ACTION_SCREEN_ON);
    intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
    intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
    intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    intentFilter.addAction(ServiceManager.ACTION_MAX_BATTERY_NEED_UPDATE);
    intentFilter.addAction(NotificationBattery.UPDATE_NOTIFICATION_ENABLE);

    mContext.registerReceiver(this, intentFilter);
  }

  public final void OnDestroy(Context context) {
    if (context != null) {
      context.unregisterReceiver(this);
    }
  }
}
