package com.smartbooster.junkcleaner.fragments

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.smartbooster.junkcleaner.BatteryMode.BatteryInfo
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.utils.SharePreferenceUtils
import com.unity3d.services.core.properties.ClientProperties.getApplicationContext
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.DecimalFormat


class HomeFragment : Fragment() {

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

  private val LOCATION_PERMS = arrayOf(
    Manifest.permission.WRITE_EXTERNAL_STORAGE
  )
  val WRITE_EXTERNAL_STORAGE_REQUEST = 1337

  lateinit var  batteryInfo: BatteryInfo
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_home, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    requestForPermission()

   // startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.System.canWrite(requireContext())) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:" + requireActivity().packageName)
        startActivity(intent)

      }
    }

    val actManager = requireActivity().getSystemService(ACTIVITY_SERVICE) as ActivityManager

    // Declaring MemoryInfo object
    val memInfo = ActivityManager.MemoryInfo()

    // Fetching the data from the ActivityManager
    actManager.getMemoryInfo(memInfo)

    // Fetching the available and total memory and converting into Giga Bytes
    val availMemory = memInfo.availMem.toDouble()/(1000*1000*1000)
    val totalMemory= memInfo.totalMem.toDouble()/(1000*1000*1000)



    val result = Math.ceil(totalMemory)






//    val prefs: SharedPreferences = requireActivity().getSharedPreferences("Press", MODE_PRIVATE)
//    val name = prefs.getBoolean("onPress", true)


    var value: String? = requireActivity().intent.getStringExtra("change")

     if (value == "true") {
       Log.d("TOTALMEMORY", "true")
       var availMemoryRes = availMemory * 1.3
       val decimalFormat = DecimalFormat("#.##")
       val availResult = decimalFormat.format(availMemoryRes)
       val total = totalMemory - availMemory
       val resTotal = total / totalMemory * 100
       val resPercent = resTotal / 1.3
       val memoryOccupiedResult = Math.round(resPercent).toInt()
       waveLoadingView.bottomTitle = availResult + " GB"
       memory_percent.text = memoryOccupiedResult.toString() + "%"
     } else {
       Log.d("TOTALMEMORY", "false")
       val total = totalMemory - availMemory
       val resTotal = total / totalMemory * 100
       val decimalFormat = DecimalFormat("#.##")
       val availResult = decimalFormat.format(availMemory)
       val memoryOccupiedResult = Math.round(resTotal).toInt()
       waveLoadingView.bottomTitle = availResult + " GB"
       memory_percent.text = memoryOccupiedResult.toString() + "%"
     }
      //The key argument here must match that used in the other activity

    total_storage.text = result.toString() + " GB"


    batteryInfo = BatteryInfo()

    waveLoadingView.setAnimDuration(2000)

    val mi = ActivityManager.MemoryInfo()
    val activityManager = activity?.getSystemService(ACTIVITY_SERVICE) as ActivityManager?
    activityManager!!.getMemoryInfo(mi)


    phone_boost.setOnClickListener {

      findNavController().navigate(R.id.action_homeFragment_to_phoneBoostFragment)
    }

    cpu_btn.setOnClickListener {


      findNavController().navigate(R.id.action_homeFragment_to_CPUFragment)


    }
    clean_click.setOnClickListener {

      if (!canAccessLocation()) {
        requestPermissions()
      } else {
        findNavController().navigate(R.id.action_homeFragment_to_junkFileFragment)
      }


    }

    battery_saver_click.setOnClickListener {

      findNavController().navigate(R.id.action_homeFragment_to_batterySaverFragment)
    }

    lottie_antivirus.setOnClickListener {

     findNavController().navigate(R.id.action_homeFragment_to_myDialog)

    }


    var sail: String? = requireActivity().intent.getStringExtra("change_cpu")

    if (sail == "true") {
      tvTemperaturePin.setText("32" + getString(R.string.celsius))

    } else {
      tvTemperaturePin.setText("51" + getString(R.string.celsius))

    }

  }

  fun getTemp(i: Double): String? {
    return if (!SharePreferenceUtils.getInstance(activity).getTempFormat()) {
      val b = Math.ceil(((i / 10 * 9 / 5 + 32) * 100)) / 100 * 1.8
      val r = b.toString()
      r + this.getString(R.string.celsius)
    } else {
      val a = Math.ceil(((i / 10 * 9 / 5 + 42) * 100)) / 100 * 1.8
      val d = a.toString()

      d + this.getString(R.string.fahrenheit)
    }
  }


  private fun canAccessLocation(): Boolean {
    return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
  }

  private fun hasPermission(permission: String): Boolean {
    return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
      && PackageManager.PERMISSION_GRANTED == requireActivity().checkSelfPermission(permission))
  }

  private fun requestPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requireActivity().requestPermissions(
       LOCATION_PERMS,
       WRITE_EXTERNAL_STORAGE_REQUEST
      )
    }
  }

  fun rqVolley() {
    val requestQueue = Volley.newRequestQueue(requireContext())
    val sr: StringRequest = object : StringRequest(
      Method.POST, "http://92.205.104.237",
      Response.Listener { response ->

        //Toast.makeText(requireActivity(), "GOOD", Toast.LENGTH_SHORT).show()
        //Log.d("ORDERRESPONSE", response)
        //your response
      },
      Response.ErrorListener { error ->
       // Toast.makeText(requireActivity(), "BAD", Toast.LENGTH_SHORT).show()
       // Log.d("ORDERERRor", error.message.toString())
        //your error
      }) {
      override fun getParams(): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()

        var strUser: String? = requireActivity().intent.getStringExtra("value") // 2

        val separated: List<String> = strUser!!.split("utm_source=")

        params["reff_id"] = separated[0]
        return params
      }

      @Throws(AuthFailureError::class)
      override fun getHeaders(): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["Content-Type"] = "application/x-www-form-urlencoded"
        return params
      }
    }
    requestQueue.add(sr)
  }

  fun requestForPermission(): Boolean {
    var isPermissionOn = true
    if (VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
      try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data =
          Uri.parse(String.format("package:%s", getApplicationContext().getPackageName()))
        startActivityForResult(intent, 2296)
      } catch (e: Exception) {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
        startActivityForResult(intent, 2296)
      }
    }

    return isPermissionOn
  }

}