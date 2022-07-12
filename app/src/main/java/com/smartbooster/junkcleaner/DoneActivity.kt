package com.smartbooster.junkcleaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.android.synthetic.main.activity_done.*
import kotlinx.android.synthetic.main.fragment_done.*
import kotlinx.android.synthetic.main.fragment_done.ad_frame
import kotlinx.android.synthetic.main.fragment_done.back_main
import kotlinx.android.synthetic.main.fragment_done.back_main_text

class DoneActivity : AppCompatActivity() {
  val ADMOB_AD_UNIT_ID = "ca-app-pub-5636049984270410/4345576485"
  var currentNativeAd: NativeAd? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_done)

    MobileAds.initialize(this)


    back_main.setOnClickListener {
//      findNavController().navigate(R.id.homeFragment)

      startActivity(Intent(this, MainActivity::class.java))

    }
    back_main_text.setOnClickListener {
      //findNavController().navigate(R.id.homeFragment)

      startActivity(Intent(this, MainActivity::class.java))

    }

    done_antivirus_cpu.setOnClickListener {

    }

    refreshAd()

  }

  override fun onBackPressed() {
    super.onBackPressed()
    startActivity(Intent(this, MainActivity::class.java))
  }

  private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Set the media view.
    adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

    // Set other ad assets.
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.priceView = adView.findViewById(R.id.ad_price)
    adView.starRatingView = adView.findViewById(R.id.ad_stars)
    adView.storeView = adView.findViewById(R.id.ad_store)
    adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
      adView.bodyView?.visibility = View.INVISIBLE
    } else {
      adView.bodyView?.visibility = View.VISIBLE
      (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
      adView.callToActionView?.visibility = View.INVISIBLE
    } else {
      adView.callToActionView?.visibility = View.VISIBLE
      (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
      adView.iconView?.visibility = View.GONE
    } else {
      (adView.iconView as ImageView).setImageDrawable(
        nativeAd.icon?.drawable
      )
      adView.iconView?.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adView.priceView?.visibility = View.INVISIBLE
    } else {
      adView.priceView?.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView?.visibility = View.INVISIBLE
    } else {
      adView.storeView?.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView?.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView?.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adView.advertiserView?.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView?.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeAd.mediaContent?.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    if (vc!!.hasVideoContent()) {
      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
      // VideoController will call methods on this object when events occur in the video
      // lifecycle.
      vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
        override fun onVideoEnd() {
          super.onVideoEnd()
        }
      }
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   *
   */
  private fun refreshAd() {

    val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

    builder.forNativeAd { nativeAd ->
      // OnUnifiedNativeAdLoadedListener implementation.
      // If this callback occurs after the activity is destroyed, you must call
      // destroy and return or you may get a memory leak.
      // You must call destroy on old ads when you are done with them,
      // otherwise you will have a memory leak.
      currentNativeAd?.destroy()
      currentNativeAd = nativeAd
      val adView = layoutInflater
        .inflate(R.layout.ad_unified, null) as NativeAdView
      populateNativeAdView(nativeAd, adView)
      ad_frame.removeAllViews()
      ad_frame.addView(adView)
    }

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        val error =
          """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
        Toast.makeText(
         this@DoneActivity, "Failed to load native ad with error $error",
          Toast.LENGTH_SHORT
        ).show()
      }
    }).build()

    adLoader.loadAd(AdRequest.Builder().build())

  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    super.onDestroy()
  }
}