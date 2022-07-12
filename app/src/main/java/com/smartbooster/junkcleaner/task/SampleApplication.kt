package com.smartbooster.junkcleaner.task

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.OnInitializationListener
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.basement.CASAnalytics
import com.smartbooster.junkcleaner.utils.PreferenceUtils

class SampleApplication : Application() {

    companion object {
        var manager: MediationManager? = null
        const val CHANNEL_ID = "notification_channel_id"

    }

    override fun onCreate() {
        super.onCreate()

        PreferenceUtils.init(this)

        createNotificationChannel()

        // (Optional) Apply Analytics receiver
        CASAnalytics.handler = object : CASAnalytics.Handler {
            override fun log(eventName: String, content: Bundle) {
                Log.i("AdOpen", "Analytics log event $eventName:")
                for (key in content.keySet()) {
                    Log.i("AdOpen", "\t $key:${content.get(key)}")
                }
            }
        }

        // Set Ads Settings
        CAS.settings.debugMode = true
        CAS.settings.analyticsCollectionEnabled = true
        CAS.settings.allowInterstitialAdsWhenVideoCostAreLower = true

        // Initialize SDK
        manager = CAS.buildManager()
            .withManagerId("com.cool.cleaner.phonebooster")
            .withTestAdMode(false)
            .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
            .withInitListener(OnInitializationListener { success, error ->
                Log.i("AdOpen", "CAS initialize success: $success with error: $error")
            })
            .initialize(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}