package com.smartbooster.junkcleaner.fragments.antivirus

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.adapter.AppInfoAdaptor
import com.trustlook.sdk.data.AppInfo
import java.util.*
import java.util.concurrent.TimeUnit


class AntivirusResultFragment : Fragment() {

    var rlScanning: RelativeLayout? = null
    var rlResult:RelativeLayout? = null
    var tvProgress: TextView? = null
    var tvTotal:TextView? = null
    var tvInvalid:TextView? = null
    var tvMalware:TextView? = null
    var tvPUA:TextView? = null
    var tvBenign:TextView? = null
    var tvTimeCost:TextView? = null
    var appInfoList: List<AppInfo> = ArrayList()
    var lvApp: ListView? = null
    var adaptor: AppInfoAdaptor? = null
    var startTime: Long = 0
    var millis: Long = 0
    lateinit var result: AntivirusResult;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        result = arguments?.getSerializable("result") as AntivirusResult;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_antivirus_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rlScanning = view.findViewById<RelativeLayout>(R.id.rl_scanning)
        tvProgress = view.findViewById<TextView>(R.id.tv_progressing)

        rlResult = view.findViewById<RelativeLayout>(R.id.rl_result)
        lvApp = view.findViewById<ListView>(R.id.list)
        setListViewHeader()
        Log.d("TL",result.isSuccess.toString());
        Log.d("TL",result.apps.size.toString());
        if(result.isSuccess) {
            update(result.apps,result.apps.size);
        } else {
            tvProgress?.setText(result.error_message)
            rlScanning?.setVisibility(View.INVISIBLE)
        }
    }

    private fun setListViewHeader() {
        val header = View.inflate(requireContext(), R.layout.listview_header, null)
        tvTotal = header.findViewById(R.id.tv_scan_total_count)
        tvInvalid = header.findViewById(R.id.tv_scan_invalid_count)
        tvMalware = header.findViewById(R.id.tv_scan_malware_count)
        tvPUA = header.findViewById(R.id.tv_scan_pua_count)
        tvBenign = header.findViewById(R.id.tv_scan_benign_count)
        tvTimeCost = header.findViewById(R.id.tv_scan_time_count)
        lvApp!!.addHeaderView(header)
    }

    private fun convertToTime(millis: Long): String? {
        return if (millis < 1000) {
            "00:00:01"
        } else String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        )
    }

    fun update(appInfoList: List<AppInfo>, totalFile: Int) {
        millis = System.currentTimeMillis() - startTime
        val invalid = totalFile - appInfoList.size
        var malware = 0
        var pua = 0
        var benign = 0
        if (appInfoList.isNotEmpty()) {
            Collections.sort(appInfoList)
            for (appInfo in appInfoList) {
                if (appInfo.score >= 8) {
                    malware++
                } else if (appInfo.score >= 6) {
                    pua++
                } else {
                    benign++
                }
            }
        }
        Log.d("TL",(lvApp==null).toString());
        adaptor = AppInfoAdaptor(context)
        adaptor?.setAppInfoList(appInfoList)
        Log.d("TL", adaptor!!.count.toString());
        lvApp?.adapter = adaptor
        tvProgress?.visibility = View.INVISIBLE
        rlScanning?.visibility = View.INVISIBLE
        rlResult?.visibility = View.VISIBLE
        tvTotal?.text = totalFile.toString()
        tvInvalid?.text = invalid.toString()
        tvMalware?.text = malware.toString()
        tvPUA?.text = pua.toString()
        tvBenign?.text = benign.toString()
        tvTimeCost?.text = convertToTime(millis)
    }
}