package com.smartbooster.junkcleaner.fragments.battary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartbooster.junkcleaner.R
import kotlinx.android.synthetic.main.fragment_norm_battery.*

import android.os.Handler
import android.util.Log
import android.animation.Animator
import android.content.Context
import android.os.Build
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.smartbooster.junkcleaner.task.AdContentListener
import com.smartbooster.junkcleaner.task.SampleApplication
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import kotlinx.android.synthetic.main.fragment_antivirus.*
import kotlinx.android.synthetic.main.fragment_clean_result.*


class NormBatteryFragment : Fragment() {

  var status = 0
  var handler: Handler = Handler()

  lateinit var r: Runnable
  lateinit var mHandler: Handler

  private var mInterstitialAd: InterstitialAd? = null

  private var statusAdViews: Map<AdType, TextView> = emptyMap()
  private var isActiveAppReturn: Boolean = false

  private lateinit var checkNetworkConnection: CheckNetworkConnection

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val callback: OnBackPressedCallback =
      object : OnBackPressedCallback(true)
      {
        override fun handleOnBackPressed() {
          // Leave empty do disable back press or
          // write your code which you want
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
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_norm_battery, container, false)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Check ad available
    SampleApplication.manager?.let {
      statusAdViews.forEach { it1 ->
        it1.value.text =
          if ((it.isAdReady(it1.key))) "Ready" else "Loading"
      }
    }

    // Validate Integration
    CAS.validateIntegration(requireActivity())

    checkNetworkConnection = CheckNetworkConnection(requireActivity().application)

    val model = Build.MODEL
    phoneNameNorm.setText(model)

    Thread {
      while (status < 100) {
        status += 1
        try {
          Thread.sleep(60)
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }
        handler.post {
          battery_percent.text = status.toString() + "%"
          if (status == 50) {
            analizing_text.text = "Optimizing..."

          }
          if (status == 100) {

            lottie_battery.visibility = View.GONE

            lottie_battery_done.visibility = View.VISIBLE

          }

        }
      }
    }.start()


    lottie_battery_done.addAnimatorListener(object : Animator.AnimatorListener {
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
          checkNetworkConnection.observe(requireActivity()) { isConnected ->

            if (isConnected) {

              SampleApplication.manager?.showInterstitial(requireActivity(), AdContentListener(requireActivity(), AdType.Interstitial))
            } else {
              findNavController().navigate(R.id.doneFragment)
            }

          }
          if (mInterstitialAd == null) {
            findNavController().navigate(R.id.doneFragment)
          }



        }
        mHandler.postDelayed(r, 80)
      }
    })


  }
}