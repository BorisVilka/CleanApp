package com.smartbooster.junkcleaner.fragments.cpu

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.smartbooster.junkcleaner.R
import kotlinx.android.synthetic.main.fragment_c_p_u.*
import android.content.pm.PackageManager

import android.content.pm.ApplicationInfo
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.task.AdContentListener
import com.smartbooster.junkcleaner.task.SampleApplication
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import kotlinx.android.synthetic.main.fragment_antivirus.*
import kotlinx.android.synthetic.main.fragment_clean_result.*


class CPUFragment : Fragment() {

  var status = 0
  var handler: Handler = Handler()

  var packages: List<ApplicationInfo>? = null
  var pm: PackageManager? = null


  lateinit var r: Runnable
  lateinit var mHandler: Handler
  private var mInterstitialAd: InterstitialAd? = null
  private var mAdIsLoading: Boolean = false
  val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

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
    return inflater.inflate(R.layout.fragment_c_p_u, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    val model = Build.MODEL
    phoneNameCPU.setText(model)
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


    Thread {
      while (status < 100) {
        status += 1
        try {
          Thread.sleep(60)
        } catch (e: InterruptedException) {
          e.printStackTrace()
        }
        handler.post {
          cpu_percent.text = status.toString() + "%"
          if (status == 50) {
            cpu_text.text = "Cooling..."

           // lottie_cpu.visibility = View.GONE
            //lottie_cpu_snow.visibility = View.VISIBLE

           // lottie_snow.visibility = View.VISIBLE
          }
          if (status == 100) {

            lottie_cpu.visibility = View.GONE
//            lottie_snow.visibility = View.GONE
            //lottie_cpu_snow.visibility = View.GONE

            lottie_cpu_done.visibility = View.VISIBLE

            lottie_cpu_done.addAnimatorListener(object : Animator.AnimatorListener {
              override fun onAnimationStart(animation: Animator) {



              }

              override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "end")





              }

              override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
              }

              override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")

                mHandler = Handler()
                r = Runnable {

                  checkNetworkConnection.observe(requireActivity()) { isConnected ->


                    requireActivity().intent.putExtra("change_cpu", "true")

                    if (isConnected) {

                      SampleApplication.manager?.showInterstitial(requireActivity(), AdContentListener(requireActivity(), AdType.Interstitial))

                    } else {
                      findNavController().navigate(R.id.CPUDoneFragment)
                    }

                  }
                  if (mInterstitialAd == null) {
                    findNavController().navigate(R.id.doneFragment)
                  }

                  lottie_cpu_done.visibility = View.GONE


                }
                mHandler.postDelayed(r, 80)

              }
            })

          }

        }
      }
    }.start()

    killAllProccess()

  }

//  private fun showInterstitial() {
//    if (mInterstitialAd != null) {
//      mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
//        override fun onAdDismissedFullScreenContent() {
//          Log.d("TAG", "Ad was dismissed.")
//          // Don't forget to set the ad reference to null so you
//          // don't show the ad a second time.
//          mInterstitialAd = null
//          loadAd()
//        }
//
//        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//          Log.d("TAG", "Ad failed to show.")
//          // Don't forget to set the ad reference to null so you
//          // don't show the ad a second time.
//          mInterstitialAd = null
//        }
//
//        override fun onAdShowedFullScreenContent() {
//          Log.d("TAG", "Ad showed fullscreen content.")
//          // Called when ad is dismissed.
//
//          startActivity(Intent(requireContext(), MainActivity::class.java))
//
//        }
//      }
//      mInterstitialAd?.show(requireActivity())
//    } else {
//      //Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
//      startAnimation()
//    }
//  }
//
//  fun startAnimation() {
//
//    if (!mAdIsLoading && mInterstitialAd == null) {
//      mAdIsLoading = true
//      loadAd()
//    }
//
//
//  }
//
//  private fun loadAd() {
//    var adRequest = AdRequest.Builder().build()
//
//    InterstitialAd.load(
//      requireContext(), AD_UNIT_ID, adRequest,
//      object : InterstitialAdLoadCallback() {
//        override fun onAdFailedToLoad(adError: LoadAdError) {
//          Log.d("TAG", adError?.message)
//          mInterstitialAd = null
//          mAdIsLoading = false
//          val error = "domain: ${adError.domain}, code: ${adError.code}, " +
//            "message: ${adError.message}"
//          Toast.makeText(
//            requireActivity(),
//            "onAdFailedToLoad() with error $error",
//            Toast.LENGTH_SHORT
//          ).show()
//        }
//
//        override fun onAdLoaded(interstitialAd: InterstitialAd) {
//          Log.d("TAG", "Ad was loaded.")
//          mInterstitialAd = interstitialAd
//          mAdIsLoading = false
//          mInterstitialAd?.show(requireActivity())
//
//          interstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//              super.onAdDismissedFullScreenContent()
//              findNavController().navigate(R.id.CPUDoneFragment)
//
//            }
//          }
//          //Toast.makeText(this@SplashActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
//        }
//      }
//    )
//  }

  fun killAllProccess() {
    pm = requireActivity().getPackageManager()
    //get a list of installed apps.
    //get a list of installed apps.
    packages = pm!!.getInstalledApplications(0)

    val mActivityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    for (packageInfo in packages!!) {
      if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
      if (packageInfo.packageName == "mypackage") continue
      mActivityManager.killBackgroundProcesses(packageInfo.packageName)
    }
  }
}