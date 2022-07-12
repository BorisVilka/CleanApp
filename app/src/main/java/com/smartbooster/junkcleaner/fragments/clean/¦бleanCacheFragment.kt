package com.smartbooster.junkcleaner.fragments.clean

import android.Manifest
import android.app.usage.StorageStatsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartbooster.junkcleaner.R
import java.util.*

import android.util.Log

import android.content.Intent
import android.content.pm.*
import android.os.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.smartbooster.junkcleaner.adapter.CleanAdapter
import com.smartbooster.junkcleaner.model.ChildItem
import com.smartbooster.junkcleaner.model.GroupItem
import com.smartbooster.junkcleaner.task.TaskClean
import com.smartbooster.junkcleaner.utils.AnimatedExpandableListView
import com.smartbooster.junkcleaner.utils.PreferenceUtils
import com.smartbooster.junkcleaner.utils.Utils
import com.smartbooster.junkcleaner.view.RotateLoading
import kotlinx.android.synthetic.main.fragment_clean.*
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class СleanCacheFragment : Fragment() {

  private var mRecyclerView: AnimatedExpandableListView? = null
  private var mTvTotalCache: TextView? = null
  private var mTvType: TextView? = null
  private var mTvTotalFound: TextView? = null
  private var mTvNoJunk: TextView? = null
  private var mBtnCleanUp: Button? = null
  private var mViewLoading: LinearLayout? = null
  private var mRotateloadingApks: RotateLoading? = null
  private var mRotateloadingCache: RotateLoading? = null
  private var mRotateloadingDownloadFiles: RotateLoading? = null
  private var mRotateloadingLargeFiles: RotateLoading? = null
  private var mRotateloadingSmallFiles: RotateLoading? = null
  private var mTotalSizeSystemCache: Long = 0
  private var mTotalSizeFiles: Long = 0
  private var mTotalSizeApk: Long = 0
  private var mTotalSizeLargeFiles: Long = 0
  private var mTotalSizeSmallFiles: Long = 0
  private val mFileListLarge = ArrayList<File>()
  private val mGroupItems = ArrayList<GroupItem>()
  private var mAdapter: CleanAdapter? = null
  private var mScanApkFiles: ScanApkFiles? = null
  private var mTaskScan: TaskScan? = null
  private var mScanDownLoadFiles: ScanDownLoadFiles? = null
  private var mScanLargeFiles: ScanLargeFiles? = null
  private var mScanSmallFiles: ScanSmallFiles? = null
  private var mIsFragmentPause = false

  var packages: List<ApplicationInfo>? = null
  var pm: PackageManager? = null
  var T2: Timer? = null
  var prog = 0


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_clean, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    requestPerMission()


  }

  fun requestPerMission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
      && ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.READ_EXTERNAL_STORAGE
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
      )
    } else {
      intView()
    }
  }

  fun intView() {
    mViewLoading = requireActivity().findViewById<View>(R.id.viewLoadingCache) as LinearLayout
    mRotateloadingApks =
      requireActivity().findViewById<View>(R.id.rotateloadingApksCache) as RotateLoading
    mRotateloadingCache =
      requireActivity().findViewById<View>(R.id.rotateloadingCacheCache) as RotateLoading
    mRotateloadingDownloadFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingDownloadCache) as RotateLoading
    mRotateloadingLargeFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingLargeFilesCache) as RotateLoading
    mRotateloadingSmallFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingSmallFilesCache) as RotateLoading
    mRecyclerView =
      requireActivity().findViewById<View>(R.id.recyclerViewCache) as AnimatedExpandableListView
    mTvTotalCache = requireActivity().findViewById<View>(R.id.tvTotalCacheCache) as TextView
    mTvType = requireActivity().findViewById<View>(R.id.tvTypeCache) as TextView
    mTvTotalFound = requireActivity().findViewById<View>(R.id.tvTotalFoundCache) as TextView
    mTvNoJunk = requireActivity().findViewById<View>(R.id.tvNoJunkCache) as TextView
    mBtnCleanUp = requireActivity().findViewById<View>(R.id.btnCleanUpCache) as Button
    mBtnCleanUp!!.visibility = View.GONE
    mBtnCleanUp!!.setOnClickListener {
      cleanUp()

      val bundle = Bundle()
      bundle.putString("getTotal", mBtnCleanUp!!.text.toString())

      var junkFileFragment = JunkFileFragment()
      junkFileFragment.arguments = bundle
    }
    initAdapter()

    if (mGroupItems.size == 0) {
      mTvTotalFound!!.text = String.format(
        getString(R.string.total_found),
        getString(R.string.calculating)
      )
      Utils.setTextFromSize(0, mTvTotalCache, mTvType, mBtnCleanUp)
      mViewLoading!!.visibility = View.VISIBLE
      startImageLoading()
      //filesFromDirApkOld
      cacheFile

      pm = requireActivity().getPackageManager()

      packages = pm!!.getInstalledApplications(0)

      T2 = Timer()
      T2!!.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          requireActivity().runOnUiThread(Runnable {
            if (prog < packages!!.size) {
              files_cleanCache.setText("" + packages!![prog].sourceDir)
              prog++
            } else {
              T2!!.cancel()
              T2!!.purge()
            }
          })
        }
      }, 100, 100)
    } else {
      mAdapter!!.notifyDataSetChanged()
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
      if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        intView()
      } else {
        intView()
      }
    }
  }

  fun cleanUp() {
    for (i in 0 until mGroupItems.size + 1) {
      if (i == mGroupItems.size) {

        findNavController().navigate(R.id.action_сleanFragment_to_cleanResultFragment)

        onDestroy()
        return
      }
      val groupItem = mGroupItems[i]
      if (groupItem.type == GroupItem.TYPE_FILE) {
        for (childItem in groupItem.items) {
          if (childItem.isCheck) {
            val file = File(childItem.path)
            file.delete()
            if (file.exists()) {
              try {
                file.canonicalFile.delete()
                if (file.exists()) {
                  requireActivity().deleteFile(file.name)
                }
              } catch (e: IOException) {
                Log.e("JunkFile", "error delete file $e")
              }
            }
          }
        }
      } else {
        if (groupItem.isCheck) {
          TaskClean(activity) { result ->
            Log.i(
              "TaskClean",
              "===--onCleanCompleted-->$result"
            )
          }.execute()
        }
      }
    }
  }

  private fun startImageLoading() {
    mRotateloadingApks!!.start()
    mRotateloadingCache!!.start()
    mRotateloadingDownloadFiles!!.start()
    mRotateloadingLargeFiles!!.start()
    mRotateloadingSmallFiles!!.start()
  }

  private fun initAdapter() {
    mAdapter = CleanAdapter(activity, mGroupItems, object : CleanAdapter.OnGroupClickListener {
      override fun onGroupClick(groupPosition: Int) {
        if (mRecyclerView!!.isGroupExpanded(groupPosition)) {
          mRecyclerView!!.collapseGroupWithAnimation(groupPosition)
        } else {
          mRecyclerView!!.expandGroupWithAnimation(groupPosition)
        }
      }

      override fun onSelectItemHeader(position: Int, isCheck: Boolean) {
        changeCleanFileHeader(position, isCheck)
      }

      override fun onSelectItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
        changeCleanFileItem(groupPosition, childPosition, isCheck)
      }
    })
    mRecyclerView!!.setAdapter(mAdapter)
  }

  private fun changeCleanFileHeader(position: Int, isCheck: Boolean) {
    val total = mGroupItems[position].total
    mTotalSizeSystemCache = if (isCheck) {
      mTotalSizeSystemCache + total
    } else {
      mTotalSizeSystemCache - total
    }
    Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType, btnCleanUpCache)
    mGroupItems[position].setIsCheck(isCheck)
    for (childItem in mGroupItems[position].items) {
      childItem.setIsCheck(isCheck)
    }
    mAdapter!!.notifyDataSetChanged()
  }

  private fun changeCleanFileItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
    val total = mGroupItems[groupPosition].items[childPosition].cacheSize
    mTotalSizeSystemCache = if (isCheck) {
      mTotalSizeSystemCache + total
    } else {
      mTotalSizeSystemCache - total
    }
    Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType, mBtnCleanUp)
    mGroupItems[groupPosition].items[childPosition].setIsCheck(isCheck)
    var isCheckItem = false
    for (childItem in mGroupItems[groupPosition].items) {
      isCheckItem = childItem.isCheck
      if (!isCheckItem) {
        break
      }
    }
    if (isCheckItem) {
      mGroupItems[groupPosition].setIsCheck(true)
    } else {
      mGroupItems[groupPosition].setIsCheck(false)
    }
    mAdapter!!.notifyDataSetChanged()
  }

  private val cacheFile: Unit
    get() {
      TaskScan( object : OnActionListener {
        override fun onScanCompleted(totalSize: Long, result: List<ChildItem>?) {
          //mTotalSizeSystemCache = totalSize
          Utils.setTextFromSize(totalSize, mTvTotalCache, mTvType, mBtnCleanUp)
          result?.let {
            if (result.isNotEmpty()) {
              val groupItem = GroupItem()
              groupItem.title = getString(R.string.system_cache)
              groupItem.total = totalSize
              groupItem.setIsCheck(true)
              groupItem.type = GroupItem.TYPE_CACHE
              groupItem.items = result
              mGroupItems.add(groupItem)
            }
          }
          mRotateloadingCache!!.stop()
          updateAdapter()
        }
      }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

  private fun updateAdapter() {
    if (mGroupItems.size != 0) {
      for (i in mGroupItems.indices) {
        if (mRecyclerView!!.isGroupExpanded(i)) {
          mRecyclerView!!.collapseGroupWithAnimation(i)
        } else {
          mRecyclerView!!.expandGroupWithAnimation(i)
        }
      }
      mRecyclerView!!.visibility = View.VISIBLE
      mTvNoJunk!!.visibility = View.GONE
      mBtnCleanUp!!.visibility = View.VISIBLE
      T2!!.cancel()
      T2!!.purge()
      files_cleanCache.visibility = View.GONE
      mAdapter!!.notifyDataSetChanged()
    } else {
      mRecyclerView!!.visibility = View.GONE
      mBtnCleanUp!!.visibility = View.GONE
      mTvNoJunk!!.visibility = View.VISIBLE
    }
    mViewLoading!!.visibility = View.GONE
    mTvTotalFound!!.text = String.format(
      getString(R.string.total_found),
      Utils.formatSize(mTotalSizeSystemCache + mTotalSizeFiles + mTotalSizeApk + mTotalSizeLargeFiles)
    )
  }

  fun getfile(dir: File): ArrayList<File> {
    val listFile = dir.listFiles()
    if (listFile != null && listFile.size > 0) {
      for (aListFile in listFile) {
        if (aListFile.isDirectory && aListFile.name != Environment.DIRECTORY_DOWNLOADS) {
          getfile(aListFile)
        } else {
          val fileSizeInBytes = aListFile.length()
          val fileSizeInKB = fileSizeInBytes / 1024
          val fileSizeInMB = fileSizeInKB / 1024
          if (fileSizeInMB >= 10 && !aListFile.name.endsWith(".apk")) {
            mTotalSizeLargeFiles += aListFile.length()
            mFileListLarge.add(aListFile)
          }
        }
      }
    }
    return mFileListLarge
  }

  override fun onPause() {
    super.onPause()
    mIsFragmentPause = true
  }

  override fun onDestroyView() {
    super.onDestroyView()
    mGroupItems.clear()
    if (mScanApkFiles != null
      && mScanApkFiles!!.status == AsyncTask.Status.RUNNING
    ) {
      mScanApkFiles!!.cancel(true)
    }
    if (mTaskScan != null
      && mTaskScan!!.status == AsyncTask.Status.RUNNING
    ) {
      mTaskScan!!.cancel(true)
    }
    if (mScanDownLoadFiles != null
      && mScanDownLoadFiles!!.status == AsyncTask.Status.RUNNING
    ) {
      mScanDownLoadFiles!!.cancel(true)
    }
    if (mScanLargeFiles != null
      && mScanLargeFiles!!.status == AsyncTask.Status.RUNNING
    ) {
      mScanLargeFiles!!.cancel(true)
    }
  }


  private inner class TaskScan(
    private val mOnActionListener: OnActionListener?
  ) : AsyncTask<Void?, String?, List<ChildItem>>() {
    private var mTotalSize: Long = 0
    override fun onProgressUpdate(vararg values: String?) {
      super.onProgressUpdate(*values)

    }

    override fun doInBackground(vararg params: Void?): List<ChildItem> {
//            List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
      var mGetPackageSizeInfoMethod: Method? = null
      try {
        mGetPackageSizeInfoMethod = requireActivity().packageManager.javaClass.getMethod(
          "getPackageSizeInfo", String::class.java, IPackageStatsObserver::class.java
        )
      } catch (e: NoSuchMethodException) {
        e.printStackTrace()
      }
      val mainIntent = Intent(Intent.ACTION_MAIN, null)
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
      var pkgAppsList: MutableList<ResolveInfo> =
        requireActivity().packageManager.queryIntentActivities(mainIntent, 0)
      pkgAppsList = pkgAppsList.filter {
        it.activityInfo.packageName != requireActivity().packageName
          && !it.activityInfo.packageName.startsWith("com.mi")
          && !it.activityInfo.packageName.startsWith("com.google")
          && !it.activityInfo.packageName.startsWith("com.android")
      }.toMutableList()

      val apps: MutableList<ChildItem> = ArrayList()
      for (pkg in pkgAppsList) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
          val storageStatsManager =
            requireActivity().getSystemService(AppCompatActivity.STORAGE_STATS_SERVICE) as StorageStatsManager
          try {
            val mApplicationInfo =
              requireActivity().packageManager.getApplicationInfo(pkg.activityInfo.packageName, 0)
            val storageStats = storageStatsManager.queryStatsForUid(
              mApplicationInfo.storageUuid,
              mApplicationInfo.uid
            )
            val cacheSize = storageStats.cacheBytes
//                        val cacheSize = getCacheSize(mApplicationInfo.packageName) // todo show real cleanable data size
//                        loge(mApplicationInfo.loadLabel(activity.packageManager).toString() + " - " + cacheSize)
            addPackage(apps, cacheSize, pkg.activityInfo.packageName)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        } else {
          try {
            mGetPackageSizeInfoMethod!!.invoke(requireActivity().packageManager,
              pkg.activityInfo.packageName,
              object : IPackageStatsObserver.Stub() {
                override fun onGetStatsCompleted(
                  pStats: PackageStats,
                  succeeded: Boolean
                ) {
                  val cacheSize =
                    pStats.cacheSize // todo show real cleanable data size
//                                        val cacheSize = getCacheSize(pStats.packageName)
                  addPackage(apps, cacheSize, pkg.activityInfo.packageName)
                }
              }
            )
          } catch (e: IllegalAccessException) {
            e.printStackTrace()
          } catch (e: InvocationTargetException) {
            e.printStackTrace()
          }
        }
        publishProgress(pkg.activityInfo.packageName)
      }
      return apps
    }

    override fun onPostExecute(result: List<ChildItem>) {
      Collections.sort(result) { o1: ChildItem, o2: ChildItem -> o2.cacheSize.compareTo(o1.cacheSize) }
      mOnActionListener?.onScanCompleted(mTotalSize, result)
    }

    private fun getCacheSize(packageName: String): Long {
      val absolutePath = Environment.getExternalStorageDirectory().absolutePath
      val packageData = File(
        String.format(
          Locale.US, "%s%sAndroid%sdata%s%s",
          absolutePath,
          File.separator,
          File.separator,
          File.separator,
          packageName
        )
      )
      val dataSize: Pair<List<String>, Long>? = getFolderSize(packageData)

      var size = 0L
      dataSize?.first?.forEach {
        val file = File(it)
        size += file.length()
      }
      return size
    }

    private fun getFolderSize(file: File): Pair<List<String>, Long>? {
      if (!file.exists()) {
        return null
      }
      val tmpList: MutableList<String> = ArrayList()
      if (!file.isDirectory) {
        tmpList.add(file.absolutePath)
        return Pair(tmpList, file.length())
      }
      val subFilesPatches = file.list() ?: return null
      var length: Long = 0
      var resultData: Pair<List<String>, Long>? = null
      for (subFilePath in subFilesPatches) {
        val subFile = File(file, subFilePath)
        if (!subFile.isDirectory) {
          tmpList.add(subFile.absolutePath)
          length += subFile.length()
          resultData = Pair(tmpList, length)
        } else {
          if (!subFile.isHidden) {
            resultData = getFolderSize(subFile)
            if (resultData != null) {
              length += resultData.second
              tmpList.addAll(resultData.first)
              resultData = Pair(tmpList, length)
            }
          }
        }
      }
      return resultData
    }

    private fun addPackage(apps: MutableList<ChildItem>, cacheSize: Long, pgkName: String) {
      try {
        val packageManager = requireActivity().packageManager
        val info = packageManager.getApplicationInfo(pgkName, PackageManager.GET_META_DATA)
        val showFakeData = PreferenceUtils.isCleanCache(pgkName)
        if (cacheSize > 1024 * 1000 && showFakeData) { // todo remove fake data
          mTotalSize += cacheSize
          apps.add(
            ChildItem(
              pgkName,
              packageManager.getApplicationLabel(info).toString(),
              info.loadIcon(packageManager),
              cacheSize, ChildItem.TYPE_CACHE, null.toString(), true
            )
          )
        }
      } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
      }
    }
  }

  interface OnActionListener {
    fun onScanCompleted(totalSize: Long, result: List<ChildItem>?)
  }

  private inner class ScanLargeFiles(private val mOnScanLargeFilesListener: OnScanLargeFilesListener?) :
    AsyncTask<Void?, Int?, List<File>>() {
    override fun onPreExecute() {}
    override fun doInBackground(vararg p0: Void?): List<File>? {
      val root = File(
        Environment.getExternalStorageDirectory()
          .absolutePath
      )
      return getfile(root)
    }

    override fun onPostExecute(result: List<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnScanLargeFilesListener {
    fun onScanCompleted(result: List<File>)
  }

  private inner class ScanApkFiles(private val mOnScanLargeFilesListener: OnScanApkFilesListener?) :
    AsyncTask<Void?, Int?, List<File>>() {
    override fun onPreExecute() {}
    protected override fun doInBackground(vararg p0: Void?): List<File>? {
      val filesResult: MutableList<File> = ArrayList()
      val downloadDir = File(Environment.getExternalStorageDirectory().absolutePath)
      val files = downloadDir.listFiles()
      if (files != null) {
        for (file in files) {
          if (file.name.endsWith(".apk")) {
            filesResult.add(file)
          }
        }
      }
      return filesResult
    }

    override fun onPostExecute(result: List<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnScanApkFilesListener {
    fun onScanCompleted(result: List<File>?)
  }

  private inner class ScanDownLoadFiles(private val mOnScanLargeFilesListener: OnScanDownloadFilesListener?) :
    AsyncTask<Void?, Int?, Array<File>>() {
    override fun onPreExecute() {}
    protected override fun doInBackground(vararg p0: Void?): Array<File>? {
      val downloadDir = File(
        Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_DOWNLOADS
        ).absolutePath
      )
      return downloadDir.listFiles()
    }

    override fun onPostExecute(result: Array<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnScanDownloadFilesListener {
    fun onScanCompleted(result: Array<File>?)
  }

  private inner class ScanSmallFiles(private val mOnScanLargeFilesListener: OnScanSmallFilesListener?) :
    AsyncTask<Void?, Int?, Array<File>>() {
    override fun onPreExecute() {}
    override fun doInBackground(vararg p0: Void?): Array<File>? {

      val downloadDir = File(
        Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_DCIM
        ).absolutePath
      )
      return downloadDir.listFiles()
    }

    override fun onPostExecute(result: Array<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnScanSmallFilesListener {
    fun onScanCompleted(result: Array<File>)
  }

  companion object {
    const val EXTDIR_REQUEST_CODE = 1110
    const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 300
  }
  }