package com.smartbooster.junkcleaner.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.smartbooster.junkcleaner.BatteryMode.BatteryInfo;
import com.smartbooster.junkcleaner.MainActivity;
import com.smartbooster.junkcleaner.R;
import com.smartbooster.junkcleaner.utils.SharePreferenceUtils;

import java.text.DecimalFormat;

public class NotificationBattery extends NotificationCompat.Builder {
  public static final int NOTIFYCATION_BATTERY_ID = 1000;
  public static final String UPDATE_NOTIFICATION_ENABLE = "update_notification_enable" ;

  NotificationManager notificationManager;
  private static NotificationBattery notifycationBattery;
  Context mContext;

  BatteryInfo batteryInfo;

  CpuValue cpuValue;

  public NotificationBattery(@NonNull Context context, @NonNull String channelId) {
    super(context, channelId);
    this.mContext = context.getApplicationContext();
    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
  }

  public static NotificationBattery getInstance(Context context) {
    if (notifycationBattery == null)
      notifycationBattery = new NotificationBattery(context,"notification_channel_id");
    return notifycationBattery;
  }


  public void updateNotify(int temp) {
    Intent intent = new Intent(mContext, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

//    Intent iOptimize = new Intent(mContext, BoostActivity.class);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    PendingIntent pOptipimize = PendingIntent.getActivity(mContext, 0, iOptimize, 0);


    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext);
    notificationBuilder.setTicker(null);
    notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
    notificationBuilder.setOnlyAlertOnce(true);
    notificationBuilder.setContentTitle("fff");
    notificationBuilder.setContentText("fff");
    notificationBuilder.setContentIntent(pendingIntent);
    notificationBuilder.setDefaults(0);

   RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
    remoteViews.setOnClickPendingIntent(R.id.img_clean, pendingIntent);
   // remoteViews.setTextViewText(R.id.tvBattery, lv + "%");
    //temperature


    cpuValue = new CpuValue();

    batteryInfo = new BatteryInfo();


    DecimalFormat decimalFormat = new DecimalFormat("#.###");
    decimalFormat.applyPattern("#.#");
    double avaiResult = Double.parseDouble(String.valueOf(Math.round(batteryInfo.temperature)));


    String cpu = intent.getStringExtra("change_cpu");

    if (cpu == "true") {
      remoteViews.setTextViewText(R.id.tvTemp, "32" + mContext.getString(R.string.celsius));
    } else  {
      remoteViews.setTextViewText(R.id.tvTemp, "51" + mContext.getString(R.string.celsius));
    }



    if (Build.VERSION.SDK_INT >= 26) {
      notificationBuilder.setChannelId("notification_channel_id");
    }

    notificationBuilder.setCustomContentView(remoteViews);
    Notification notification = notificationBuilder.build();

    this.notificationManager.notify(NOTIFYCATION_BATTERY_ID, notification);
  }


  public String getTemp(Double i) {
    if (!SharePreferenceUtils.getInstance(mContext).getTempFormat()) {
      double b = Math.ceil(((i / 10) * 9 / 5 + 32) * 100* 1.6) / 100 ;
      String r = String.valueOf(b);
      return (r + mContext.getString(R.string.celsius));
    } else {

      String str = Double.toString(Math.ceil((i / 10)*100* 1.6)/100) ;
      return (str + mContext.getString(R.string.fahrenheit));
    }
  }
}
