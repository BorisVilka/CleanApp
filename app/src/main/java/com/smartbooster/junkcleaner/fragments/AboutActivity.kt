package com.smartbooster.junkcleaner.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smartbooster.junkcleaner.BuildConfig
import com.smartbooster.junkcleaner.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_about)

    about_back.setOnClickListener {
      onBackPressed()
    }

    val versionName: String = BuildConfig.VERSION_NAME

    name_app.setText("Version "+ versionName)
  }
}