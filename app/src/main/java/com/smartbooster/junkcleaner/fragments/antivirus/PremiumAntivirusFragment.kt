package com.smartbooster.junkcleaner.fragments.antivirus

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdCallback
import com.cleversolutions.ads.AdLoadCallback
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.task.AdContentListener
import com.smartbooster.junkcleaner.task.SampleApplication
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import kotlinx.android.synthetic.main.fragment_premium_antivirus.*

class PremiumAntivirusFragment : Fragment(), AdCallback {


  lateinit var r: Runnable
  lateinit var mHandler: Handler
  private var mInterstitialAd: InterstitialAd? = null

  private var statusAdViews: Map<AdType, TextView> = emptyMap()
  private var isActiveAppReturn: Boolean = false
  private lateinit var checkNetworkConnection: CheckNetworkConnection

   lateinit var adType: AdType

  override fun onAttach(context: Context) {
    super.onAttach(context)
    val callback: OnBackPressedCallback =
      object : OnBackPressedCallback(true)
      {
        override fun handleOnBackPressed() {
          findNavController().navigate(R.id.homeFragment)
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
    return inflater.inflate(R.layout.fragment_premium_antivirus, container, false)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    //adType = AdType()
    about_back_antivirus_main.setOnClickListener {
      findNavController().navigate(R.id.homeFragment)
    }

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


    watchVideo.setOnClickListener {

      //SampleApplication.manager?.showInterstitial(requireActivity(), AdContentListener(requireActivity(), AdType.Rewarded))

      SampleApplication.manager?.showRewardedAd(requireActivity(), object : AdCallback {


        override fun onComplete() {
          super.onComplete()

          findNavController().navigate(R.id.action_premiumAntivirusFragment_to_antivirusFragment)
        }

        override fun onClosed() {
          super.onClosed()

          findNavController().navigate(R.id.action_premiumAntivirusFragment_to_antivirusFragment)
        }


      })

    }





//    cancel.setOnClickListener {
//      findNavController().navigate(R.id.homeFragment)
//    }

  }


  override fun onClosed() {


  }

  override fun onComplete() {
    super.onComplete()


    if (adType != AdType.Rewarded)
      findNavController().navigate(R.id.action_premiumAntivirusFragment_to_antivirusFragment)

  }



}