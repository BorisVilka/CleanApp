package com.smartbooster.junkcleaner.utils;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.smartbooster.junkcleaner.R;

import java.io.File;
import java.text.DecimalFormat;

public class Utils {

  public static String getFileName(String  path) {
    String filename=path.substring(path.lastIndexOf("/")+1);
    return filename;
  }
  public static File[] getFileList(String str) {
    File file = new File(str);
    if (!file.isDirectory()) {
      return new File[0];
    }

    return file.listFiles();
  }
  public static boolean checkSelfPermission(Activity activity, String s) {
    if (isAndroid23()) {
      return ContextCompat.checkSelfPermission(activity, s) == 0;
    } else {
      return true;
    }
  }

  public static boolean isAndroid23() {
    return android.os.Build.VERSION.SDK_INT >=23;
  }

  public static String formatSize(long size) {
    if (size <= 0)
      return "";
    final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
  }

  public static void setTextFromSize(long size, TextView tvNumber, TextView tvType, Button button) {

    if (size <= 0) {
      tvNumber.setText(String.valueOf(0.00));
      tvType.setText("");
      button.setText(button.getContext().getString(R.string.op_clean, ""));
      return;
    }
    final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    tvType.setText(units[digitGroups]);
    tvNumber.setText(String.valueOf(new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))));
    String value = tvNumber.getText().toString() + " " + tvType.getText();

    button.setText(button.getContext().getString(R.string.op_clean, value));


  }
}
