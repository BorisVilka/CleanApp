package com.smartbooster.junkcleaner

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.smartbooster.junkcleaner.service.ServiceManager
import com.smartbooster.junkcleaner.service.notification.AlarmReceiverCPU
import com.smartbooster.junkcleaner.service.notification.AlarmReceiverClean
import com.smartbooster.junkcleaner.service.notification.AlarmReceiverFiles
import java.util.*


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)

    val intent = Intent(this, ServiceManager::class.java)

    startService(intent)
    val bottomNavigationView = findViewById<BottomNavigationView
      >(R.id.bottom_navigatin_view)
    val navController = findNavController(R.id.nav_fragment)
    bottomNavigationView.setupWithNavController(navController)

    navController.addOnDestinationChangedListener { _, destination, _ ->

      when (destination.id) {
        R.id.сleanFragment -> bottomNavigationView.visibility = View.GONE
        R.id.cleanResultFragment -> bottomNavigationView.visibility = View.GONE
        R.id.doneFragment -> bottomNavigationView.visibility = View.GONE
        R.id.batterySaverFragment -> bottomNavigationView.visibility = View.GONE
        R.id.normBatteryFragment -> bottomNavigationView.visibility = View.GONE
        R.id.ultraBatteryFragment -> bottomNavigationView.visibility = View.GONE
        R.id.CPUFragment -> bottomNavigationView.visibility = View.GONE
        R.id.junkFileFragment -> bottomNavigationView.visibility = View.GONE
        R.id.antivirusFragment -> bottomNavigationView.visibility = View.GONE
        R.id.phoneBoostFragment -> bottomNavigationView.visibility = View.GONE
        R.id.settingsProfileFragment -> bottomNavigationView.visibility = View.GONE
        R.id.CPUDoneFragment -> bottomNavigationView.visibility = View.GONE
        R.id.premiumAntivirusFragment -> bottomNavigationView.visibility = View.GONE
        R.id.antivirusResultFragment -> bottomNavigationView.visibility = View.GONE






        else -> {
          bottomNavigationView.visibility = View.VISIBLE
        }
      }
    }

    myAlarmCPU()
    myAlarmClean()
    myAlarmFiles()

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

  }

  override fun onBackPressed() {
    val count = supportFragmentManager.backStackEntryCount
    if (count == 0) {
      super.onBackPressed()
      //additional code
    } else {
      supportFragmentManager.popBackStack()
    }
  }

  fun myAlarmCPU() {
    val calendar = Calendar.getInstance()
    calendar[Calendar.HOUR_OF_DAY] = 12
    calendar[Calendar.MINUTE] = 30
    calendar[Calendar.SECOND] = 0
    if (calendar.time.compareTo(Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1)
    val intent = Intent(applicationContext, AlarmReceiverCPU::class.java)
    val pendingIntent =
      PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      AlarmManager.INTERVAL_DAY,
      pendingIntent
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent)
      startService(intent)
    }
  }

  fun myAlarmClean() {
    val calendar = Calendar.getInstance()
    calendar[Calendar.HOUR_OF_DAY] = 18
    calendar[Calendar.MINUTE] = 30
    calendar[Calendar.SECOND] = 0
    if (calendar.time.compareTo(Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1)
    val intent = Intent(applicationContext, AlarmReceiverClean::class.java)
    val pendingIntent =
      PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      AlarmManager.INTERVAL_DAY,
      pendingIntent
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent)
      startService(intent)
    }
  }

  fun myAlarmFiles() {
    val calendar = Calendar.getInstance()
    calendar[Calendar.HOUR_OF_DAY] = 20
    calendar[Calendar.MINUTE] = 30
    calendar[Calendar.SECOND] = 0
    if (calendar.time.compareTo(Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1)
    val intent = Intent(applicationContext, AlarmReceiverFiles::class.java)
    val pendingIntent =
      PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
    alarmManager.setRepeating(
      AlarmManager.RTC,
      calendar.timeInMillis,
      AlarmManager.INTERVAL_DAY,
      pendingIntent
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(intent)
      startService(intent)
    }
  }

}