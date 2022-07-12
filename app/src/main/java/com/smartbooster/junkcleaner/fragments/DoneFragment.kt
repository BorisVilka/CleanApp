package com.smartbooster.junkcleaner.fragments

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.AppOpsManager.OnOpChangedListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.utils.Config
import kotlinx.android.synthetic.main.fragment_done.*


class DoneFragment : Fragment() {

   val ADMOB_AD_UNIT_ID = "ca-app-pub-5636049984270410/4345576485"
  var currentNativeAd: NativeAd? = null

  var builder: AlertDialog.Builder? = null

  private val LOCATION_PERMS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
  )
  val WRITE_EXTERNAL_STORAGE_REQUEST = 1337

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val callback: OnBackPressedCallback =
      object : OnBackPressedCallback(true)
      {
        override fun handleOnBackPressed() {
          // Leave empty do disable back press or
          // write your code which you want
          startActivity(Intent(requireContext(), MainActivity::class.java))
        }
      }
    requireActivity().onBackPressedDispatcher.addCallback(
      this,
      callback
    )
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    return inflater.inflate(R.layout.fragment_done, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    MobileAds.initialize(requireContext())


    back_main.setOnClickListener {
      findNavController().navigate(R.id.homeFragment)
    }
    back_main_text.setOnClickListener {
      findNavController().navigate(R.id.homeFragment)

    }

    done_cpu.setOnClickListener {
      findNavController().navigate(R.id.action_doneFragment_to_CPUFragment)
    }

    clean_click.setOnClickListener {

    findNavController().navigate(R.id.action_doneFragment_to_junkFileFragment)

    }

    refreshAd()
  }

  private fun hasPermissionToReadNetworkHistory(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return true
    }
    val appOps = requireActivity().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
    val mode = appOps!!.checkOpNoThrow(
      AppOpsManager.OPSTR_GET_USAGE_STATS,
      Process.myUid(), requireActivity().packageName
    )
    if (mode == AppOpsManager.MODE_ALLOWED) {
      findNavController().navigate(R.id.action_doneFragment_to_сleanFragment)
      return true
    }
    appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
      requireContext().getPackageName(),
      object : OnOpChangedListener {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        override fun onOpChanged(op: String, packageName: String) {
          val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(), requireActivity().packageName
          )
          if (mode != AppOpsManager.MODE_ALLOWED) {
            return
          }


          val intent = Intent(
            requireContext(),
            MainActivity::class.java
          )
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
          startActivity(intent)
        }
      })
    requestReadNetworkHistoryAccess()
    return false
  }

  private fun requestReadNetworkHistoryAccess() {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    startActivity(intent)
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

      }
    }).build()

    adLoader.loadAd(AdRequest.Builder().build())

  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    super.onDestroy()
  }

//  private fun canAccessLocation(): Boolean {
//    return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//  }
//
//  private fun hasPermission(permission: String): Boolean {
//    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//      && PackageManager.PERMISSION_GRANTED == requireActivity().checkSelfPermission(permission))
//  }
//
//  private fun requestPermissions() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//      startActivityForResult(
//        Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
//        Config.PERMISSIONS_USAGE
//      )
//    }
//  }

}