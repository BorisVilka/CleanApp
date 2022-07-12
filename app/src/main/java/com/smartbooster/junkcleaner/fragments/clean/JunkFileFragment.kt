package com.smartbooster.junkcleaner.fragments.clean

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.IPackageStatsObserver
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.adapter.CleanAdapter
import com.smartbooster.junkcleaner.model.ChildItem
import com.smartbooster.junkcleaner.model.GroupItem
import com.smartbooster.junkcleaner.task.TaskClean
import com.smartbooster.junkcleaner.utils.AnimatedExpandableListView
import com.smartbooster.junkcleaner.utils.Utils
import com.smartbooster.junkcleaner.view.RotateLoading
import kotlinx.android.synthetic.main.fragment_junk_file.*
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.CountDownLatch


class JunkFileFragment : Fragment() {
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
    // requestPerMission();
    return inflater.inflate(R.layout.fragment_junk_file, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)


    about_back_clean.setOnClickListener {
      startActivity(Intent(requireActivity(), MainActivity::class.java))
    }


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
    mViewLoading = requireActivity().findViewById<View>(R.id.viewLoading) as LinearLayout
    mRotateloadingApks =
      requireActivity().findViewById<View>(R.id.rotateloadingApks) as RotateLoading
    mRotateloadingCache =
      requireActivity().findViewById<View>(R.id.rotateloadingCache) as RotateLoading
    mRotateloadingDownloadFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingDownload) as RotateLoading
    mRotateloadingLargeFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingLargeFiles) as RotateLoading
    mRotateloadingSmallFiles =
      requireActivity().findViewById<View>(R.id.rotateloadingSmallFiles) as RotateLoading
    mRecyclerView =
      requireActivity().findViewById<View>(R.id.recyclerView) as AnimatedExpandableListView
    mTvTotalCache = requireActivity().findViewById<View>(R.id.tvTotalCache) as TextView
    mTvType = requireActivity().findViewById<View>(R.id.tvType) as TextView
    mTvTotalFound = requireActivity().findViewById<View>(R.id.tvTotalFound) as TextView
    mTvNoJunk = requireActivity().findViewById<View>(R.id.tvNoJunk) as TextView
    mBtnCleanUp = requireActivity().findViewById<View>(R.id.btnCleanUp) as Button
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
      filesFromDirApkOld

      pm = requireActivity().getPackageManager()

      packages = pm!!.getInstalledApplications(0)

      T2 = Timer()
      T2!!.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
          requireActivity().runOnUiThread(Runnable {
            if (prog < packages!!.size) {
              files_clean.setText("" + packages!![prog].sourceDir)
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

        findNavController().navigate(R.id.action_junkFileFragment_to_cleanResultFragment)

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
    Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType, btnCleanUp)
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

  val filesFromDirApkOld: Unit
    get() {
      if (mScanApkFiles != null
        && mScanApkFiles!!.status == AsyncTask.Status.RUNNING
      ) {
        return
      }
      mScanApkFiles = ScanApkFiles(object : OnScanApkFilesListener {
        override fun onScanCompleted(result: List<File>?) {
          if (mIsFragmentPause) {
            return
          }
          if (result != null && result.size > 0) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.obsolete_apk)
            groupItem.setIsCheck(false)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            for (currentFile in result) {
              if (currentFile.name.endsWith(".apk")) {
                val childItem = ChildItem(
                  currentFile.name,
                  currentFile.name, ContextCompat.getDrawable(
                    activity!!,
                    R.drawable.ic_android_white_24dp
                  ),
                  currentFile.length(), ChildItem.TYPE_APKS,
                  currentFile.path, false
                )
                childItems.add(childItem)
                mTotalSizeApk += currentFile.length()
                groupItem.total = mTotalSizeApk
                groupItem.items = childItems
              }
            }
            mGroupItems.add(groupItem)
            mTvTotalFound!!.text = String.format(
              getString(R.string.total_found),
              Utils.formatSize(mTotalSizeApk)
            )
          }
          mRotateloadingApks!!.stop()
          cacheFile
        }
      })
      mScanApkFiles!!.execute()
    }
  private val cacheFile: Unit
    private get() {
      if (mTaskScan != null
        && mTaskScan!!.status == AsyncTask.Status.RUNNING
      ) {
        return
      }
      mTaskScan = TaskScan(object : OnActionListener {
        override fun onScanCompleted(totalSize: Long, result: List<ChildItem?>) {
          if (mIsFragmentPause) {
            return
          }
          mTotalSizeSystemCache = totalSize
          Utils.setTextFromSize(totalSize, mTvTotalCache, mTvType, mBtnCleanUp)
          if (result.size != 0) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.system_cache)
            groupItem.total = mTotalSizeSystemCache
            groupItem.setIsCheck(true)
            groupItem.type = GroupItem.TYPE_CACHE
            groupItem.items = result
            mGroupItems.add(groupItem)
            mTvTotalFound!!.text = String.format(
              getString(R.string.total_found),
              Utils.formatSize(mTotalSizeApk + mTotalSizeSystemCache)
            )
            Utils.setTextFromSize(mTotalSizeSystemCache, mTvTotalCache, mTvType, mBtnCleanUp)
          }
          mRotateloadingCache!!.stop()
          filesFromDirFileDownload
        }
      })
      mTaskScan!!.execute()
    }

  val filesFromDirFileDownload: Unit
    get() {
      if (mScanDownLoadFiles != null
        && mScanDownLoadFiles!!.status == AsyncTask.Status.RUNNING
      ) {
        return
      }
      mScanDownLoadFiles = ScanDownLoadFiles(object : OnScanDownloadFilesListener {
        override fun onScanCompleted(result: Array<File>?) {
          if (mIsFragmentPause) {
            return
          }
          if (result != null && result.size > 0) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.downloader_files)
            groupItem.setIsCheck(false)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            for (currentFile in result) {
              mTotalSizeFiles += currentFile.length()
              val childItem = ChildItem(
                currentFile.name,
                currentFile.name, ContextCompat.getDrawable(
                  activity!!,
                  R.drawable.ic_android_white_24dp
                ),
                currentFile.length(), ChildItem.TYPE_DOWNLOAD_FILE,
                currentFile.path, false
              )
              childItems.add(childItem)
              groupItem.total = mTotalSizeFiles
              groupItem.items = childItems
            }
            mGroupItems.add(groupItem)
          }
          mRotateloadingDownloadFiles!!.stop()
          largeFile
        }
      })
      mScanDownLoadFiles!!.execute()
    }
  private val largeFile: Unit
    private get() {
      if (mScanLargeFiles != null
        && mScanLargeFiles!!.status == AsyncTask.Status.RUNNING
      ) {
        return
      }
      mScanLargeFiles = ScanLargeFiles(object : OnScanLargeFilesListener {
        override fun onScanCompleted(result: List<File>) {
          if (mIsFragmentPause) {
            return
          }
          if (result.size != 0) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.large_files)
            groupItem.total = mTotalSizeLargeFiles
            groupItem.setIsCheck(false)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            for (currentFile in result) {
              val childItem = ChildItem(
                currentFile.name,
                currentFile.name, ContextCompat.getDrawable(
                  activity!!,
                  R.drawable.ic_android_white_24dp
                ),
                currentFile.length(), ChildItem.TYPE_LARGE_FILES,
                currentFile.path, false
              )
              childItems.add(childItem)
              groupItem.items = childItems
            }
            mGroupItems.add(groupItem)
          }
          mRotateloadingLargeFiles!!.stop()
          // smallFiles
          updateAdapter()
        }
      })
      mScanLargeFiles!!.execute()
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
      files_clean.visibility = View.GONE
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

  @SuppressLint("StaticFieldLeak")
  private inner class TaskScan(private val mOnActionListener: OnActionListener?) :
    AsyncTask<Void?, Int?, List<ChildItem?>>() {
    private var mGetPackageSizeInfoMethod: Method? = null
    private var mTotalSize: Long = 0
    override fun onPreExecute() {}
     override fun doInBackground(vararg p0: Void?): List<ChildItem?>? {
      val packages = requireActivity().packageManager.getInstalledApplications(
        PackageManager.GET_META_DATA
      )
      val countDownLatch = CountDownLatch(packages.size)
      val apps: MutableList<ChildItem?> = ArrayList()
      try {
        for (pkg in packages) {
          mGetPackageSizeInfoMethod!!.invoke(requireActivity().packageManager, pkg.packageName,
            object : IPackageStatsObserver.Stub() {
              @Throws(RemoteException::class)
              override fun onGetStatsCompleted(
                pStats: PackageStats,
                succeeded: Boolean
              ) {
                synchronized(apps) { addPackage(apps, pStats) }
                synchronized(countDownLatch) { countDownLatch.countDown() }
              }
            }
          )
        }
        countDownLatch.await()
      } catch (e: InvocationTargetException) {
        e.printStackTrace()
      } catch (e: InterruptedException) {
        e.printStackTrace()
      } catch (e: IllegalAccessException) {
        e.printStackTrace()
      }
      return ArrayList(apps)
    }

    override fun onPostExecute(result: List<ChildItem?>) {
      mOnActionListener?.onScanCompleted(mTotalSize, result)
    }

    private fun addPackage(apps: MutableList<ChildItem?>, pStats: PackageStats) {
      var cacheSize = pStats.cacheSize
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        cacheSize += pStats.externalCacheSize
      }
      try {
        val packageManager = activity!!.packageManager
        val info = packageManager.getApplicationInfo(
          pStats.packageName,
          PackageManager.GET_META_DATA
        )
        if (cacheSize > 1024 * 12) {
          mTotalSize += cacheSize
          apps.add(
            ChildItem(
              pStats.packageName,
              packageManager.getApplicationLabel(info).toString(),
              packageManager.getApplicationIcon(pStats.packageName),
              cacheSize, ChildItem.TYPE_CACHE, null, true
            )
          )
        }
      } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
      }
    }

    init {
      try {
        mGetPackageSizeInfoMethod = requireActivity().packageManager.javaClass.getMethod(
          "getPackageSizeInfo", String::class.java, IPackageStatsObserver::class.java
        )
      } catch (e: NoSuchMethodException) {
        e.printStackTrace()
      }
    }
  }

  interface OnActionListener {
    fun onScanCompleted(totalSize: Long, result: List<ChildItem?>)
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