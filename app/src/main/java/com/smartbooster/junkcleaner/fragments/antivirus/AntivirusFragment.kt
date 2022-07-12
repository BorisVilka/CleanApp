package com.smartbooster.junkcleaner.fragments.antivirus

import android.animation.Animator
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdCallback
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.task.SampleApplication
import com.trustlook.sdk.Constants
import com.trustlook.sdk.cloudscan.CloudScanClient
import com.trustlook.sdk.cloudscan.CloudScanListener
import com.trustlook.sdk.data.AppInfo
import com.trustlook.sdk.data.Error
import com.trustlook.sdk.data.Region
import kotlinx.android.synthetic.main.fragment_antivirus.*
import java.util.*


class AntivirusFragment : Fragment() {

  var packages: List<ApplicationInfo>? = null
  var pm: PackageManager? = null
  var T2: Timer? = null
  var prog = 0
  lateinit var result: AntivirusResult;
  var ended = false;

  var cloudScanClient: CloudScanClient? = null


  lateinit var r: Runnable
  lateinit var mHandler: Handler

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    result = AntivirusResult();
    cloudScanClient = CloudScanClient.Builder(context)
      .setRegion(Region.INTL)
      .setConnectionTimeout(30000)
      .setSocketTimeout(30000)
      .build()

    onQuickScan()
  }

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
    return inflater.inflate(R.layout.fragment_antivirus, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    killAppsInBg()

    val model = Build.MODEL
    phoneName.setText(model)

    pm = requireActivity().getPackageManager()

    packages = pm!!.getInstalledApplications(0)

    T2 = Timer()
    SampleApplication.manager?.showRewardedAd(requireActivity(), object : AdCallback {

      override fun onClosed() {
        super.onClosed()
        Log.d("TL","CLOSed");
        T2!!.scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
            requireActivity().runOnUiThread(Runnable {
              if (prog < packages!!.size) {
                anti_scan.text = "" + packages!![prog].sourceDir
                prog++
              } else {
                T2!!.cancel()
                T2!!.purge()


                lottie_antivirus_done.visibility = View.VISIBLE
                no_antivirus.visibility = View.VISIBLE
                cool_antivirus.visibility = View.VISIBLE
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

            no_antivirus.visibility = View.VISIBLE
            cool_antivirus.visibility = View.VISIBLE
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
            if(ended) {
              mHandler = Handler()
              r = Runnable {
                val args = Bundle();
                args.putSerializable("result",result);
                findNavController().navigate(R.id.action_antivirusFragment_to_antivirusResultFragment,args)

                //startActivity(Intent(requireContext(), DoneActivity::class.java))

                lottie_antivirus_done.visibility = View.GONE

              }
              mHandler.postDelayed(r, 300)
            }
          }
        })
      }

      override fun onShowFailed(message: String) {
        super.onShowFailed(message)
        Log.d("TL","FAIL");
        T2!!.scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
            requireActivity().runOnUiThread(Runnable {
              if (prog < packages!!.size) {
                anti_scan.text = "" + packages!![prog].sourceDir
                prog++
              } else {
                T2!!.cancel()
                T2!!.purge()


                lottie_antivirus_done.visibility = View.VISIBLE
                no_antivirus.visibility = View.VISIBLE
                cool_antivirus.visibility = View.VISIBLE
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

            no_antivirus.visibility = View.VISIBLE
            cool_antivirus.visibility = View.VISIBLE
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

            if(ended) {
              mHandler = Handler()
              r = Runnable {
                val args = Bundle();
                args.putSerializable("result",result);
                findNavController().navigate(R.id.action_antivirusFragment_to_antivirusResultFragment,args)

                //startActivity(Intent(requireContext(), DoneActivity::class.java))

                lottie_antivirus_done.visibility = View.GONE

              }
              mHandler.postDelayed(r, 300)
            }
          }
        })
      }
    })



  }

  private fun onQuickScan() {

    //val fragment = AntivirusResultFragment()
    cloudScanClient!!.startQuickScan(object : CloudScanListener() {
      override fun onScanStarted() {
        Log.d(Constants.TAG, "startQuickScan onScanStarted")
      }

      override fun onScanProgress(curr: Int, total: Int, result: AppInfo) {
        Log.d(
          Constants.TAG,
          "startQuickScan onScanProgress $curr/$total"
        )
        if (!result.packageName.isEmpty()) {
          //fragment.tvProgress?.setText(result.packageName)
        } else if (!result.apkPath.isEmpty()) {
          //fragment.tvProgress?.setText(result.apkPath)
        } else {
          //fragment.tvProgress?.setText(R.string.scanning)
        }
      }

      override fun onScanError(errCode: Int, message: String) {
        Log.d(Constants.TAG, "startQuickScan onScanError $errCode")
        var ErrorMessage = ""
        ErrorMessage = if (errCode == Error.HOST_NOT_DEFINED) {
          "Error $errCode: HOST_NOT_DEFINED"
        } else if (errCode == Error.INVALID_INPUT) {
          "Error  $errCode: INVALID_INPUT, no samples to scan"
        } else if (errCode == Error.SERVER_NOT_AVAILABLE) {
          "Error $errCode: SERVER_NOT_AVAILABLE, please check the key in AndroidManifest.xml"
        } else if (errCode == Error.JSON_EXCEPTION) {
          "Error $errCode: JSON_EXCEPTION"
        } else if (errCode == Error.IO_EXCEPTION) {
          "Error $errCode: IO_EXCEPTION"
        } else if (errCode == Error.NO_NETWORK) {
          "Error $errCode: NO_NETWORK"
        } else if (errCode == Error.SOCKET_TIMEOUT_EXCEPTION) {
          "Error $errCode: SOCKET_TIMEOUT_EXCEPTION"
        } else if (errCode == Error.INVALID_KEY) {
          "Error $errCode: INVALID_KEY, please check the key in AndroidManifest.xml"
        } else if (errCode == Error.UNSTABLE_NETWORK) {
          "Error $errCode: UNSTABLE_NETWORT"
        } else if (errCode == Error.INVALID_SIGNATURE) {
          "Error $errCode: INVALID_SIGNATURE"
        } else {
          "Error $errCode $message"
        }
        result.error_message = message;
        result.isSuccess = false;
        ended = true;
        //fragment.tvProgress?.setText(ErrorMessage)
        //fragment.rlScanning?.setVisibility(View.GONE)
      }

      override fun onScanCanceled() {
        Toast.makeText(context, "startQuickScan onScanCanceled", Toast.LENGTH_LONG).show()
      }

      override fun onScanInterrupt() {}
      override fun onScanFinished(results: List<AppInfo>) {
        Log.d(Constants.TAG, "startQuickScan onScanFinished " + results.size + " APPs")
        //fragment.update(results, results.size)
        result.apps = results;
        result.isSuccess = true;
        ended = true;
        //                packagenameScan();
      }
    })
  }


  private fun killAppsInBg() {

    val packages: List<ApplicationInfo>
    val pm: PackageManager
    pm = requireActivity().getPackageManager()
    //get a list of installed apps.
    //get a list of installed apps.
    packages = pm.getInstalledApplications(0)

    val mActivityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    for (packageInfo in packages) {
      if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1) continue
      if (packageInfo.packageName == "mypackage") continue
      mActivityManager.killBackgroundProcesses(packageInfo.packageName)
    }


  }
}