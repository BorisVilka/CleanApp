package com.smartbooster.junkcleaner

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cleversolutions.ads.AdCallback
import com.cleversolutions.ads.AdStatusHandler
import com.cleversolutions.ads.CASAppOpen
import com.cleversolutions.ads.LoadAdCallback
import com.cleversolutions.ads.android.CAS
import com.smartbooster.junkcleaner.model.RequestModel
import com.smartbooster.junkcleaner.utils.CheckNetworkConnection
import com.smartbooster.junkcleaner.utils.SPrefHelper
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONObject
import java.io.UnsupportedEncodingException


class SplashActivity : AppCompatActivity() {

  private lateinit var referrerClient: InstallReferrerClient
  lateinit var requestModel: RequestModel

  private var loadingAppResInProgress = false
  private var appOpenAdVisible = false

  lateinit var mHandler: Handler
  lateinit var r: Runnable

  var sPrefHelper: SPrefHelper? = null


  private lateinit var checkNetworkConnection: CheckNetworkConnection

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(
      WindowManager.LayoutParams.FLAG_FULLSCREEN,
      WindowManager.LayoutParams.FLAG_FULLSCREEN
    );
    setContentView(R.layout.activity_splash)



    checkNetworkConnection = CheckNetworkConnection(application)

    sPrefHelper = SPrefHelper()

    val firstSession = sPrefHelper!!.IsFirstSession(this@SplashActivity)

    if (firstSession) {
      startActivity(Intent(this@SplashActivity, PrivacyActivity::class.java))
      finish()
    } else {

      lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
          Log.e("Animation:", "start")

        }

        override fun onAnimationEnd(animation: Animator) {
          Log.e("Animation:", "end")

          mHandler = Handler()
          r = Runnable {
            referrerClient = InstallReferrerClient.newBuilder(this@SplashActivity).build()

            referrerClient.startConnection(object : InstallReferrerStateListener {
              override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                  InstallReferrerClient.InstallReferrerResponse.OK -> {
                    val referrer: String
                    val response: ReferrerDetails = referrerClient.installReferrer
                    referrer = response.installReferrer
                    intent.putExtra("value", referrer)
                    referrerClient.endConnection()

                    // Connection established.
                  }
                  InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    // API not available on the current Play Store app.
                  }
                  InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                    // Connection couldn't be established.
                  }

                }

              }

              override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
              }
            })
            createAppOpenAd()

          }
          mHandler.postDelayed(r, 1000)


        }

        override fun onAnimationCancel(animation: Animator) {
          Log.e("Animation:", "cancel")
        }

        override fun onAnimationRepeat(animation: Animator) {
          Log.e("Animation:", "repeat")
        }
      })


    }
  }

  @SuppressLint("SetTextI18n")
  private fun createAppOpenAd() {
    // Try get last initialized MediationManager
    val initializedManager = CAS.manager

    // Create an Ad
    val appOpenAd = if (initializedManager == null) {
      CASAppOpen.create("demo")
    } else {
      CASAppOpen.create("demo")
    }


    // Handle fullscreen callback events
    appOpenAd.contentCallback = object : AdCallback {
      override fun onShown(ad: AdStatusHandler) {
        Log.d("AdOpen", "App Open Ad shown")
//        appOpenAdStatusView.text = "Show AppOpenAd"
      }

      override fun onShowFailed(message: String) {
        Log.d("AdOpen", "App Open Ad show failed: $message")
//        appOpenAdStatusView.text = "Show AppOpenAd failed: $message"
        appOpenAdVisible = false
        startNextActivity()
      }

      override fun onClosed() {
        Log.d("AdOpen", "App Open Ad closed")
//        appOpenAdStatusView.text = "AppOpenAd closed"
        appOpenAdVisible = false
        startNextActivity()
      }
    }

    // Load the Ad
    //  appOpenAdStatusView.text = "Loading AppOpenAd..."
    appOpenAd.loadAd(
      this,
      resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
      object : LoadAdCallback {
        override fun onAdFailedToLoad(error: com.cleversolutions.ads.AdError) {
          Log.e("AdOpen", "App Open Ad failed to load: ${error.message}")
//          appOpenAdStatusView.text = "Load AppOpenAd failed: ${error.message}"
          startNextActivity()
        }

        override fun onAdLoaded() {
//          Log.d(SampleActivity.TAG, "App Open Ad loaded")
//          appOpenAdStatusView.text = "AppOpenAd loaded"
          appOpenAdVisible = true
          appOpenAd.show(this@SplashActivity)
        }
      })
  }

  private fun startNextActivity() {
    if (loadingAppResInProgress || appOpenAdVisible)
      return
    val intent = Intent(this, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    startActivity(intent)
  }
}