package com.smartbooster.junkcleaner.utils

import android.content.Context

class SPrefHelper {

  fun IsFirstSession(context: Context): Boolean {
    val sPref = context.getSharedPreferences("firstSession", Context.MODE_PRIVATE)
    return sPref.getBoolean("firstSession", true)
  }

  fun setFirstSession(context: Context, firstSession: Boolean?) {
    val sPref = context.getSharedPreferences("firstSession", Context.MODE_PRIVATE)
    val ed = sPref.edit()
    ed.putBoolean("firstSession", firstSession!!)
    ed.apply()
  }

}