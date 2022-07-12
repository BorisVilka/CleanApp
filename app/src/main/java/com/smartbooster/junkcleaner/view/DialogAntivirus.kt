package com.smartbooster.junkcleaner.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.cleversolutions.ads.AdCallback
import com.smartbooster.junkcleaner.R
import kotlinx.android.synthetic.main.custom_dialog_antivirus.*

class DialogAntivirus: DialogFragment(), AdCallback {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(
      R.layout.custom_dialog_antivirus,
      container, false)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    close_dialog.setOnClickListener {
      dialog?.dismiss()
    }

    btn_watchVideoBtn.setOnClickListener {

    findNavController().navigate(R.id.action_myDialog_to_antivirusFragment)

    }

    btn_watchVideo.setOnClickListener {

      findNavController().navigate(R.id.action_myDialog_to_antivirusFragment)

    }

  }
}