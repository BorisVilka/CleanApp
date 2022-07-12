package com.smartbooster.junkcleaner.fragments.battary

import android.animation.Animator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdType
import com.smartbooster.junkcleaner.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.smartbooster.junkcleaner.task.AdContentListener
import com.smartbooster.junkcleaner.task.SampleApplication
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import kotlinx.android.synthetic.main.fragment_battery_saver.*
import kotlinx.android.synthetic.main.fragment_norm_battery.*

class BatterySaverFragment : Fragment() {

  val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
  var currentNativeAd: NativeAd? = null

  var status = 0
  var handler: Handler = Handler()

  lateinit var r: Runnable
  lateinit var mHandler: Handler

  private var mInterstitialAd: InterstitialAd? = null

  private var statusAdViews: Map<AdType, TextView> = emptyMap()
  private var isActiveAppReturn: Boolean = false

  private lateinit var checkNetworkConnection: CheckNetworkConnection

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_battery_saver, container, false)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val model = Build.MODEL
    phoneNameNormF.text = model

    Thread {
      while (status < 100) {
        status += 1
        try {
          Thread.sleep(30)
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }
        handler.post {
          battery_percentF.text = status.toString() + "%"
          if (status == 50) {

          }
          if (status == 100) {

            lottie_batteryF.visibility = View.GONE

            lottie_battery_doneF.visibility = View.VISIBLE

          }

        }
      }
    }.start()

    lottie_battery_doneF.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {



      }

      override fun onAnimationEnd(animation: Animator) {





      }

      override fun onAnimationCancel(animation: Animator) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator) {
        mHandler = Handler()
        r = Runnable {

          battery_first.visibility = View.GONE
          battery_second.visibility = View.VISIBLE

        }
        mHandler.postDelayed(r, 80)
      }
    })

    refreshAd()
    val rnds = (2..5).random()

    val rnds_norm = (3..6).random()

    val rnds_norm_minute = (23..33).random()
    val rnds_ultra = (4..8).random()
    val rnds_ultra_minute = (33..43).random()

    hour_of_norm.text = rnds_norm.toString() + "h " + rnds_norm_minute.toString() + "min"
    hour_of_ultra.text = rnds_ultra.toString() + "h " + rnds_ultra_minute.toString() + "min"

    duration_of_battery.text = rnds.toString() + " hours"

    back_saver.setOnClickListener {
      requireActivity().onBackPressed()
    }

    back_saver_text.setOnClickListener {
      requireActivity().onBackPressed()
    }


    norm_btn.setOnClickListener {
      findNavController().navigate(R.id.normBatteryFragment)
    }

    ultra_btn.setOnClickListener {
      findNavController().navigate(R.id.ultraBatteryFragment)
    }
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
    adView.mediaView!!.setMediaContent(nativeAd.mediaContent!!)

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
      adView.bodyView!!.visibility = View.INVISIBLE
    } else {
      adView.bodyView!!.visibility = View.VISIBLE
      (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
      adView.callToActionView!!.visibility = View.INVISIBLE
    } else {
      adView.callToActionView!!.visibility = View.VISIBLE
      (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
      adView.iconView!!.visibility = View.GONE
    } else {
      (adView.iconView as ImageView).setImageDrawable(
        nativeAd.icon!!.drawable
      )
      adView.iconView!!.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adView.priceView!!.visibility = View.INVISIBLE
    } else {
      adView.priceView!!.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView!!.visibility = View.INVISIBLE
    } else {
      adView.storeView!!.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView!!.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView!!.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adView.advertiserView!!.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView!!.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeAd.mediaContent!!.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    if (vc.hasVideoContent()) {
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

  private fun refreshAd() {

    val builder = AdLoader.Builder(requireContext(), ADMOB_AD_UNIT_ID)

    builder.forNativeAd { nativeAd ->
      currentNativeAd?.destroy()
      currentNativeAd = nativeAd
      val adView = layoutInflater
        .inflate(R.layout.ad_unified, null) as NativeAdView
      populateNativeAdView(nativeAd, adView)
      //ad_frame_battery.removeAllViews()
      ad_frame_battery.addView(adView)
    }

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        val error =
          """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"

      }
    }).build()

    adLoader.loadAd(AdRequest.Builder().build())

  }
}