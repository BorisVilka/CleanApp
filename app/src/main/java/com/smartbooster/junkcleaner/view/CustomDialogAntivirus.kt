package com.smartbooster.junkcleaner.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdCallback
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.fragments.antivirus.AntivirusActivity
import com.smartbooster.junkcleaner.task.SampleApplication
import kotlinx.android.synthetic.main.custom_dialog_antivirus.*

class CustomDialogAntivirus(context: Context) : Dialog(context), AdCallback {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.custom_dialog_antivirus)


    btn_watchVideoBtn.setOnClickListener {

      context.startActivity(Intent(context, AntivirusActivity::class.java))

    }

    btn_watchVideo.setOnClickListener {

      context.startActivity(Intent(context, AntivirusActivity::class.java))

    }

  }

}