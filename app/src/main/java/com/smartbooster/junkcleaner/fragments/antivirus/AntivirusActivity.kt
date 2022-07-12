package com.smartbooster.junkcleaner.fragments.antivirus

import android.animation.Animator
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.smartbooster.junkcleaner.DoneActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.task.SampleApplication
import kotlinx.android.synthetic.main.fragment_antivirus.*
import java.util.*

class AntivirusActivity : AppCompatActivity() {

  var packages: List<ApplicationInfo>? = null
  var pm: PackageManager? = null
  var T2: Timer? = null
  var prog = 0

  lateinit var r: Runnable
  lateinit var mHandler: Handler
  private var mInterstitialAd: InterstitialAd? = null
  private var mAdIsLoading: Boolean = false
  val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_antivirus)

    killAppsInBg()

    val model = Build.MODEL
    phoneName.setText(model)

    pm = getPackageManager()

    packages = pm!!.getInstalledApplications(0)

    T2 = Timer()
    SampleApplication.manager?.showRewardedAd(this, object : AdCallback {


      override fun onComplete() {
        super.onComplete()


      }

      override fun onClosed() {
        super.onClosed()

        T2!!.scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
            runOnUiThread(Runnable {
              if (prog < packages!!.size) {
                anti_scan.text = "" + packages!![prog].sourceDir
                prog++
              } else {
                T2!!.cancel()
                T2!!.purge()


                lottie_antivirus_done.visibility = View.VISIBLE
                lottie_clean_result_anti.visibility = View.GONE
                anti_scan.visibility = View.GONE
              }
            })
          }
        }, 100, 100)

        lottie_clean_result_anti.addAnimatorListener(object : Animator.AnimatorListener {
          override fun onAnimationStart(animation: Animator) {



          }

          override fun onAnimationEnd(animation: Animator) {
            Log.e("Animation:", "end")

            T2!!.cancel()
            T2!!.purge()

            phoneName.visibility = View.GONE
            lottie_antivirus_done.visibility = View.VISIBLE
            lottie_clean_result_anti.visibility = View.GONE

            anti_scan.visibility = View.GONE
            //findNavController(R.id.nav_fragment).navigate(R.id.doneFragment)


          }

          override fun onAnimationCancel(animation: Animator) {
            Log.e("Animation:", "cancel")
          }

          override fun onAnimationRepeat(animation: Animator) {
            Log.e("Animation:", "repeat")
          }
        })

        lottie_antivirus_done.addAnimatorListener(object : Animator.AnimatorListener {
          override fun onAnimationStart(animation: Animator) {

            // lottie_clean_result.visibility = View.GONE


          }

          override fun onAnimationEnd(animation: Animator) {
            Log.e("Animation:", "end")

          }

          override fun onAnimationCancel(animation: Animator) {
            Log.e("Animation:", "cancel")
          }

          override fun onAnimationRepeat(animation: Animator) {
            Log.e("Animation:", "repeat")

            //lottie_clean_result.visibility = View.GONE

            mHandler = Handler()
            r = Runnable {
              startActivity(Intent(this@AntivirusActivity, DoneActivity::class.java))

              //startActivity(Intent(requireContext(), DoneActivity::class.java))

              lottie_antivirus_done.visibility = View.GONE

            }
            mHandler.postDelayed(r, 300)
          }
        })

      }


    })

  }

  private fun killAppsInBg() {

    val packages: List<ApplicationInfo>
    val pm: PackageManager
    pm = getPackageManager()
    //get a list of installed apps.
    //get a list of installed apps.
    packages = pm.getInstalledApplications(0)

    val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    for (packageInfo in packages) {
      if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
      if (packageInfo.packageName == "mypackage") continue
      mActivityManager.killBackgroundProcesses(packageInfo.packageName)
    }


  }
}