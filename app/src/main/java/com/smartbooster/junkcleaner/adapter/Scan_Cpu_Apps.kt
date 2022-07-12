package com.smartbooster.junkcleaner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.model.AppsModel

class Scan_Cpu_Apps(  /// Get List of Apps Causing Junk Files
  var apps: List<AppsModel>
) :
  RecyclerView.Adapter<Scan_Cpu_Apps.MyViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val itemView =
      LayoutInflater.from(parent.context).inflate(R.layout.scan_cpu_apps, parent, false)
    return MyViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    val app = apps[position]
    holder.size.text = ""
    holder.image.setImageDrawable(app.image)
  }

  override fun getItemCount(): Int {
    return apps.size
  }

  inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var size: TextView
    var image: ImageView

    init {
      size = view.findViewById<View>(R.id.apptext) as TextView
      image = view.findViewById<View>(R.id.appimage) as ImageView
    }
  }
}