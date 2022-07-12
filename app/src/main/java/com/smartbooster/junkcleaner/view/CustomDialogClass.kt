package com.smartbooster.junkcleaner.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.core.content.ContextCompat.startActivity
import com.smartbooster.junkcleaner.R
import kotlinx.android.synthetic.main.custom_dialog.*


class CustomDialogClass(context: Context) : Dialog(context) {

  init {
    setCancelable(false)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.custom_dialog)

    rating_bar.onRatingBarChangeListener = OnRatingBarChangeListener { arg0, rateValue, arg2 ->
      if (rateValue == 1f) {
        lottie_one_star.visibility = View.VISIBLE
        lottie_two_star.visibility = View.GONE
        lottie_three_star.visibility = View.GONE
        lottie_four_star.visibility = View.GONE
        lottie_five_star.visibility = View.GONE

        rate_ok.setOnClickListener {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "tatianadeveloper07@gmail.com"))
          intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
          intent.putExtra(Intent.EXTRA_TEXT, "your_text")
          context.startActivity(intent)
          onBackPressed()
        }
      }

      if (rateValue == 2f) {
        lottie_one_star.visibility = View.GONE
        lottie_two_star.visibility = View.VISIBLE
        lottie_three_star.visibility = View.GONE
        lottie_four_star.visibility = View.GONE
        lottie_five_star.visibility = View.GONE

        rate_ok.setOnClickListener {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "tatianadeveloper07@gmail.com"))
          intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
          intent.putExtra(Intent.EXTRA_TEXT, "your_text")
          context.startActivity(intent)
          onBackPressed()
        }
      }

      if (rateValue == 3f) {

        lottie_one_star.visibility = View.GONE
        lottie_two_star.visibility = View.GONE
        lottie_three_star.visibility = View.VISIBLE
        lottie_four_star.visibility = View.GONE
        lottie_five_star.visibility = View.GONE

        rate_ok.setOnClickListener {
          val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "tatianadeveloper07@gmail.com"))
          intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
          intent.putExtra(Intent.EXTRA_TEXT, "your_text")
          context.startActivity(intent)
          onBackPressed()
        }
      }

      if (rateValue == 4f) {

        lottie_one_star.visibility = View.GONE
        lottie_two_star.visibility = View.GONE
        lottie_three_star.visibility = View.GONE
        lottie_four_star.visibility = View.VISIBLE
        lottie_five_star.visibility = View.GONE

        rate_ok.setOnClickListener {
          val browse = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cool.cleaner.phonebooster"))

          context.startActivity(browse)
        }
      }

      if (rateValue == 5f) {

        lottie_one_star.visibility = View.GONE
        lottie_two_star.visibility = View.GONE
        lottie_three_star.visibility = View.GONE
        lottie_four_star.visibility = View.GONE
        lottie_five_star.visibility = View.VISIBLE

        rate_ok.setOnClickListener {
          val browse = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cool.cleaner.phonebooster"))

          context.startActivity(browse)
        }
      }
    }
  }
}