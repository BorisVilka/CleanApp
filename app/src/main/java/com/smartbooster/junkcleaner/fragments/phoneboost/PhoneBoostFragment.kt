package com.smartbooster.junkcleaner.fragments.phoneboost

import android.animation.Animator
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.task.AdContentListener
import com.smartbooster.junkcleaner.task.SampleApplication
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import kotlinx.android.synthetic.main.fragment_phone_boost.*


class PhoneBoostFragment : Fragment() {

  private val settingsTAG = "AppNameSettings"
  private var prefs: SharedPreferences? = null

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
    return inflater.inflate(R.layout.fragment_phone_boost, container, false)
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


    lottie_phone_boost.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {



      }

      override fun onAnimationEnd(animation: Animator) {
        Log.e("Animation:", "end")


        lottie_phone_boost_done.visibility = View.VISIBLE

        lottie_phone_boost.visibility = View.GONE





      }

      override fun onAnimationCancel(animation: Animator) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator) {
        Log.e("Animation:", "repeat")


      }
    })

    lottie_phone_boost_done.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator) {



      }

      override fun onAnimationEnd(animation: Animator) {
        Log.e("Animation:", "end")


        lottie_phone_boost.visibility = View.GONE

      }

      override fun onAnimationCancel(animation: Animator) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator) {
        Log.e("Animation:", "repeat")

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

          val editor: SharedPreferences.Editor = requireActivity().getSharedPreferences("Press", MODE_PRIVATE).edit()
          editor.putBoolean("onPress", true)
          editor.apply()

          requireActivity().intent.putExtra("change", "true")


//          lottie_clean_done.visibility = View.GONE


        }
        mHandler.postDelayed(r, 80)


      }
    })
  }


}