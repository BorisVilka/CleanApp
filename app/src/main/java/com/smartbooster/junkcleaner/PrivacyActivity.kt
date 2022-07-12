package com.smartbooster.junkcleaner

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.smartbooster.junkcleaner.utils.SPrefHelper
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyActivity : AppCompatActivity() {

  private var sPrefHelper: SPrefHelper? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_privacy)

    sPrefHelper = SPrefHelper()

    start_btn.setOnClickListener(View.OnClickListener {
      Toast.makeText(this, "Agree to privacy policy", Toast.LENGTH_SHORT).show()
    })

    checkPolice.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
      if (isChecked) {
        start_btn.isEnabled = true
        start_btn.setOnClickListener(View.OnClickListener {
          sPrefHelper!!.setFirstSession(this@PrivacyActivity, false)
          startActivity(Intent(this@PrivacyActivity, SplashActivity::class.java))
        })
      } else {
        start_btn.setOnClickListener(View.OnClickListener {
          Toast.makeText(this, "Agree to privacy policy", Toast.LENGTH_SHORT).show()
        })
      }
      if(!isChecked) {

        start_btn.setClickable(false);
        start_btn.setOnClickListener {
          Toast.makeText(this, "Agree to privacy policy", Toast.LENGTH_SHORT).show()
        }
//
      }
    })

    privacy_click.setOnClickListener {

      val browse = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/cool-cleaner-privacy-policy/"))

      startActivity(browse)

    }



    terms_click.setOnClickListener {

      val browse = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/cool-cleaner-privacy-policy/"))

      startActivity(browse)

    }
  }
}