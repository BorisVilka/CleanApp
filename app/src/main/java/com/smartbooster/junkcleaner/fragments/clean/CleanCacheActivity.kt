package com.smartbooster.junkcleaner.fragments.clean

import android.animation.Animator
import android.app.Activity
import android.app.usage.StorageStatsManager
import android.content.Intent
import android.content.pm.*
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.smartbooster.junkcleaner.DoneActivity
import com.smartbooster.junkcleaner.MainActivity
import com.smartbooster.junkcleaner.R
import com.smartbooster.junkcleaner.adapter.CleanAdapter
import com.smartbooster.junkcleaner.model.ChildItem
import com.smartbooster.junkcleaner.model.GroupItem
import com.smartbooster.junkcleaner.task.TaskClean
import com.smartbooster.junkcleaner.task.TaskJunkClean
import com.smartbooster.junkcleaner.utils.*
import com.smartbooster.junkcleaner.view.AnimationListener
import kotlinx.android.synthetic.main.activity_clean_result.*
import kotlinx.android.synthetic.main.fragment_junk_file.*
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

class CleanCacheActivity : AppCompatActivity() {

  @JvmField
  var mRecyclerView: AnimatedExpandableListView? = null

  @JvmField
  var mTvTotalCache: TextView? = null

  @JvmField
  var mTvType: TextView? = null

  @JvmField
  var mTvNoJunk: TextView? = null

  @JvmField
  var mBtnCleanUp: Button? = null

  @JvmField
  var mViewLoading: View? = null

  @JvmField
  var tvPkgName: TextView? = null

  @JvmField
  var avProgress: LottieAnimationView? = null

  @JvmField
  var tvCalculating: TextView? = null



  private var mTotalJunk: Long = 0
  private val mFileListLarge = ArrayList<File>()
  private var mGroupItems = ArrayList<GroupItem>()
  private var mAdapter: CleanAdapter? = null
  private var flagExit = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_clean_result)

    initData()

  }

  private fun clean() {
    val lstChildItems: MutableList<ChildItem> = ArrayList()
    for (mGroupItem in mGroupItems) {
      for (mChildItem in mGroupItem.items) {
        if (mChildItem.isCheck) lstChildItems.add(mChildItem)
      }
    }
    if (lstChildItems.isNotEmpty()) {
      cleanUp(lstChildItems)
      MyCache.getCache().save(1, System.currentTimeMillis())
    } else Toast.makeText(
      this,
      getString(R.string.selcet_file_to_clean),
      Toast.LENGTH_LONG
    ).show()
  }

  private fun initData() {
    mAdapter = CleanAdapter(this, mGroupItems, object : CleanAdapter.OnGroupClickListener {
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
    if (mGroupItems.size == 0) {
      Utils.setTextFromSize(0, mTvTotalCache, mTvType, btnCleanUpCach)
      mViewLoading!!.visibility = View.VISIBLE
      startImageLoading()
      filesFromDirApkOld
    } else {
      mAdapter!!.notifyDataSetChanged()
    }
  }

  private fun changeCleanFileHeader(position: Int, isCheck: Boolean) {
    val total = mGroupItems[position].total
    if (isCheck) {
      mTotalJunk += total
    } else {
      mTotalJunk -= total
    }
    mGroupItems[position].setIsCheck(isCheck)
    for (childItem in mGroupItems[position].items) {
      childItem.setIsCheck(isCheck)
    }
    mAdapter!!.notifyDataSetChanged()
    updateSizeTotal()
  }

  private fun changeCleanFileItem(groupPosition: Int, childPosition: Int, isCheck: Boolean) {
    val total = mGroupItems[groupPosition].items[childPosition].cacheSize
    if (isCheck) {
      mTotalJunk += total
    } else {
      mTotalJunk -= total
    }
    mGroupItems[groupPosition].items[childPosition].setIsCheck(isCheck)
    var isCheckItem = false
    for (childItem in mGroupItems[groupPosition].items) {
      isCheckItem = childItem.isCheck
      if (!isCheckItem) {
        break
      }
    }
    mGroupItems[groupPosition].setIsCheck(isCheckItem)
    mAdapter!!.notifyDataSetChanged()
    updateSizeTotal()
  }

  private fun updateSizeTotal() {
    mTotalJunk = 0
    for (mGroupItem in mGroupItems) {
      for (mChildItem in mGroupItem.items) {
        if (mChildItem.isCheck) mTotalJunk += mChildItem.cacheSize
      }
    }
    Utils.setTextFromSize(mTotalJunk, mTvTotalCache, mTvType, btnCleanUpCach)
  }

  private fun startImageLoading() {

  }


  private val filesFromDirApkOld: Unit
    get() {
      ScanApkFiles(object : OnScanApkFilesListener {
        override fun onScanCompleted(result: List<File>?) {
          if (result != null && result.isNotEmpty()) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.obsolete_apk)
            groupItem.setIsCheck(true)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            var size: Long = 0
            for (currentFile in result) {
              if (currentFile.name.endsWith(".apk")) {
                val childItem = ContextCompat.getDrawable(
                  this@CleanCacheActivity,
                  R.drawable.ic_android_white_24dp
                )?.let {
                  ChildItem(
                    currentFile.name,
                    currentFile.name, it,
                    currentFile.length(), ChildItem.TYPE_APKS,
                    currentFile.path, true
                  )
                }
                childItem?.let { childItems.add(it) }
                size += currentFile.length()
              }
            }
            groupItem.total = size
            groupItem.items = childItems
            mGroupItems.add(groupItem)
          }

          cacheFile
        }
      }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
  private val cacheFile: Unit
    get() {
      TaskScan(this, object : OnActionListener {
        override fun onScanCompleted(totalSize: Long, result: List<ChildItem>?) {
          Utils.setTextFromSize(totalSize, mTvTotalCache, mTvType, btnCleanUpCach)
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

          filesFromDirFileDownload
        }
      }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
  val filesFromDirFileDownload: Unit
    get() {
      ScanDownLoadFiles(object : OnScanDownloadFilesListener {
        override fun onScanCompleted(result: Array<File>?) {
          if (result != null && result.isNotEmpty()) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.downloader_files)
            groupItem.setIsCheck(false)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            var size: Long = 0
            for (currentFile in result) {
              size += currentFile.length()
              val childItem = ContextCompat.getDrawable(
                this@CleanCacheActivity,
                R.drawable.ic_android_white_24dp
              )?.let {
                ChildItem(
                  currentFile.name,
                  currentFile.name, it,
                  currentFile.length(), ChildItem.TYPE_DOWNLOAD_FILE,
                  currentFile.path, false
                )
              }
              childItem?.let { childItems.add(it) }
            }
            groupItem.total = size
            groupItem.items = childItems
            mGroupItems.add(groupItem)
          }

          largeFile
        }
      }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }
  private val largeFile: Unit
    private get() {
      ScanLargeFiles(object : OnScanLargeFilesListener {
        override fun onScanCompleted(result: List<File>?) {

          if (result != null && result.isNotEmpty()) {
            val groupItem = GroupItem()
            groupItem.title = getString(R.string.large_files)
            groupItem.setIsCheck(false)
            groupItem.type = GroupItem.TYPE_FILE
            val childItems: MutableList<ChildItem> = ArrayList()
            var size: Long = 0
            for (currentFile in result) {
              val childItem = ContextCompat.getDrawable(
                this@CleanCacheActivity,
                R.drawable.ic_android_white_24dp
              )?.let {
                ChildItem(
                  currentFile.name,
                  currentFile.name, it,
                  currentFile.length(), ChildItem.TYPE_LARGE_FILES,
                  currentFile.path, false
                )
              }
              childItem?.let { childItems.add(it) }
              size += currentFile.length()
            }
            groupItem.items = childItems
            groupItem.total = size
            mGroupItems.add(groupItem)
          }

          updateAdapter()
        }
      }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

  private fun updateAdapter() {
    updateSizeTotal()
    if (mGroupItems.size != 0) {
      for (i in mGroupItems.indices) {
        if (mRecyclerView!!.isGroupExpanded(i)) {
          mRecyclerView!!.collapseGroupWithAnimation(i)
        } else {
          mRecyclerView!!.expandGroupWithAnimation(i)
        }
      }
      //YoYo.with(Techniques.FadeInUp).duration(1000).playOn(mRecyclerView)
      mTvNoJunk!!.visibility = View.GONE
      mBtnCleanUp!!.visibility = View.VISIBLE
      mAdapter!!.notifyDataSetChanged()
    } else {
      mRecyclerView!!.visibility = View.GONE
      mBtnCleanUp!!.visibility = View.GONE
      mTvNoJunk!!.visibility = View.VISIBLE
      Toast.makeText(this, getString(R.string.no_junk), Toast.LENGTH_LONG).show()
      //openScreenResult(Config.FUNCTION.JUNK_FILES)
      finish()
    }
    tvCalculating!!.visibility = View.GONE
    mViewLoading!!.visibility = View.GONE
    avProgress!!.pauseAnimation()
    avProgress!!.visibility = View.INVISIBLE
//    YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTvTotalCache)
//    YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTvType)
//    YoYo.with(Techniques.FadeOut).duration(1000).playOn(tvPkgName)
    flagExit = true
  }

  interface OnScanLargeFilesListener {
    fun onScanCompleted(result: List<File>?)
  }

  interface OnScanApkFilesListener {
    fun onScanCompleted(result: List<File>?)
  }

  private open inner class ScanApkFiles(private val mOnScanLargeFilesListener: OnScanApkFilesListener?) :
    AsyncTask<Void?, String?, List<File>>() {
    override fun onProgressUpdate(vararg values: String?) {
      super.onProgressUpdate(*values)
      if (!TextUtils.isEmpty(values[0])) tvPkgName!!.text = values[0]
    }

    override fun doInBackground(vararg params: Void?): List<File> {
      val filesResult: MutableList<File> = ArrayList()
      val downloadDir = File(Environment.getExternalStorageDirectory().absolutePath)
      val files = downloadDir.listFiles()
      if (files != null) {
        for (file in files) {
          publishProgress(file.name)
          if (file.name.endsWith(".apk")) {
            filesResult.add(file)
          }
          try {
            Thread.sleep(100)
          } catch (e: InterruptedException) {
            e.printStackTrace()
          }
        }
      }
      return filesResult
    }

    override fun onPostExecute(result: List<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnScanDownloadFilesListener {
    fun onScanCompleted(result: Array<File>?)
  }

  private open inner class ScanDownLoadFiles(private val mOnScanLargeFilesListener: OnScanDownloadFilesListener?) :
    AsyncTask<Void?, String?, Array<File>?>() {
    override fun onProgressUpdate(vararg values: String?) {
      super.onProgressUpdate(*values)
      if (!TextUtils.isEmpty(values[0])) tvPkgName!!.text = values[0]
    }

    override fun doInBackground(vararg params: Void?): Array<File>? {
      val downloadDir = File(
        Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_DOWNLOADS
        ).absolutePath
      )
      val lst = downloadDir.listFiles()
      if (lst != null && lst.size != 0) {
        for (mFile in lst) {
          publishProgress(mFile.path)
          try {
            Thread.sleep(100)
          } catch (e: InterruptedException) {
            e.printStackTrace()
          }
        }
      }
      return lst
    }

    override fun onPostExecute(result: Array<File>?) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }
  }

  interface OnActionListener {
    fun onScanCompleted(totalSize: Long, result: List<ChildItem>?)
  }

  private inner class TaskScan(
    private val activity: Activity,
    private val mOnActionListener: OnActionListener?
  ) : AsyncTask<Void?, String?, List<ChildItem>>() {
    private var mTotalSize: Long = 0
    override fun onProgressUpdate(vararg values: String?) {
      super.onProgressUpdate(*values)
      if (!TextUtils.isEmpty(values[0])) tvPkgName!!.text = values[0]
    }

    override fun doInBackground(vararg params: Void?): List<ChildItem> {
//            List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
      var mGetPackageSizeInfoMethod: Method? = null
      try {
        mGetPackageSizeInfoMethod = packageManager.javaClass.getMethod(
          "getPackageSizeInfo", String::class.java, IPackageStatsObserver::class.java
        )
      } catch (e: NoSuchMethodException) {
        e.printStackTrace()
      }
      val mainIntent = Intent(Intent.ACTION_MAIN, null)
      mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
      var pkgAppsList: MutableList<ResolveInfo> =
        packageManager.queryIntentActivities(mainIntent, 0)
      pkgAppsList = pkgAppsList.filter {
        it.activityInfo.packageName != activity.packageName
          && !it.activityInfo.packageName.startsWith("com.mi")
          && !it.activityInfo.packageName.startsWith("com.google")
          && !it.activityInfo.packageName.startsWith("com.android")
      }.toMutableList()

      val apps: MutableList<ChildItem> = ArrayList()
      for (pkg in pkgAppsList) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
          val storageStatsManager =
            getSystemService(STORAGE_STATS_SERVICE) as StorageStatsManager
          try {
            val mApplicationInfo =
              packageManager.getApplicationInfo(pkg.activityInfo.packageName, 0)
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
            mGetPackageSizeInfoMethod!!.invoke(packageManager,
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
        val packageManager = packageManager
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

  fun cleanUp(listItem: List<ChildItem>) {
    for (i in 0 until mGroupItems.size + 1) {
      if (i == mGroupItems.size) {

        //findNavController().navigate(R.id.action_junkFileFragment_to_cleanResultFragment)

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
                  this.deleteFile(file.name)
                }
              } catch (e: IOException) {
                Log.e("JunkFile", "error delete file $e")
              }
            }
          }
        }
      } else {
        if (groupItem.isCheck) {
          TaskClean(this) { result ->
            Log.i(
              "TaskClean",
              "===--onCleanCompleted-->$result"
            )
          }.execute()
        }
      }
    }
  }

  private inner class ScanLargeFiles(private val mOnScanLargeFilesListener: OnScanLargeFilesListener?) :
    AsyncTask<Void?, String?, List<File>>() {
    override fun onProgressUpdate(vararg values: String?) {
      super.onProgressUpdate(*values)
      if (!TextUtils.isEmpty(values[0])) tvPkgName!!.text = values[0]
    }

    override fun doInBackground(vararg params: Void?): List<File> {
      val root = File(
        Environment.getExternalStorageDirectory()
          .absolutePath
      )
      return getfile(root, true)
    }

    override fun onPostExecute(result: List<File>) {
      mOnScanLargeFilesListener?.onScanCompleted(result)
    }

    fun getfile(dir: File, isSleep: Boolean): ArrayList<File> {
      val listFile = dir.listFiles()
      if (listFile != null && listFile.isNotEmpty()) {
        for (aListFile in listFile) {
          publishProgress(aListFile.path)
          if (aListFile.isDirectory && aListFile.name != Environment.DIRECTORY_DOWNLOADS) {
            getfile(aListFile, false)
          } else {
            val fileSizeInBytes = aListFile.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB >= 10 && !aListFile.name.endsWith(".apk")) {
              mTotalJunk += aListFile.length()
              mFileListLarge.add(aListFile)
            }
          }
          if (isSleep) try {
            Thread.sleep(100)
          } catch (e: InterruptedException) {
            e.printStackTrace()
          }
        }
      }
      return mFileListLarge
    }
  }


}