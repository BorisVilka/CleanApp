package com.smartbooster.junkcleaner.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.service.NotificationBattery
import com.smartbooster.junkcleaner.utils.SharePreferenceUtils
import kotlinx.android.synthetic.main.fragment_settings_profile.*


class SettingsProfileFragment : Fragment(){


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings_profile, container, false)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lrDnd.setOnClickListener {
      startActivity(Intent(requireContext(), DoNotDisturbActivity::class.java))
    }

    about_back_profile.setOnClickListener {
      findNavController().navigate(R.id.profileFragment)
    }


    lrTemperture.setOnClickListener {

      if (SharePreferenceUtils.getInstance(activity).getTempFormat()) {
        tvTempertureDes.setText( getString(R.string.celsius))
        swTemp.isChecked = true
        SharePreferenceUtils.getInstance(activity).setTempFormat(true)
      } else {
        tvTempertureDes.setText(getString(R.string.fahrenheit))
        swTemp.isChecked = false
        SharePreferenceUtils.getInstance(activity).setTempFormat(false)
      }
      val i = Intent()
      i.action = NotificationBattery.UPDATE_NOTIFICATION_ENABLE
      requireActivity().sendBroadcast(i)

    }


  }

}