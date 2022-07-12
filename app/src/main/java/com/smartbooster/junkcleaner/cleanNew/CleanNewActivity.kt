package com.smartbooster.junkcleaner.cleanNew

import android.app.Activity
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.content.pm.ResolveInfo
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.adapter.CleanAdapter
import com.smartbooster.junkcleaner.adapter.CleanAdapterOld
import com.smartbooster.junkcleaner.model.ChildItem
import com.smartbooster.junkcleaner.model.ChildItemOld
import com.smartbooster.junkcleaner.model.GroupItem
import com.smartbooster.junkcleaner.task.TaskJunkClean
import com.smartbooster.junkcleaner.utils.*
import com.smartbooster.junkcleaner.view.AnimationListener
import kotlinx.android.synthetic.main.activity_clean_new.*
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

class CleanNewActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_clean_new)

  }

}