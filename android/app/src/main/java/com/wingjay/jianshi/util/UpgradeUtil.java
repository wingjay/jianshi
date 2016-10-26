package com.wingjay.jianshi.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.base.BaseActivity;

import im.fir.sdk.version.AppVersion;

/**
 * Check app upgrade.
 */
public class UpgradeUtil {

  public static void checkUpgrade(final BaseActivity context) {
//    displayUpgradeDialog(context, appVersion);
  }

  private static void displayUpgradeDialog(final Context context, final AppVersion appVersion) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.upgrade_title)
        .setMessage(appVersion.getChangeLog())
        .setPositiveButton("接受", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(appVersion.getUpdateUrl()));
            context.startActivity(browserIntent);
            Toast.makeText(context, "进入升级", Toast.LENGTH_SHORT).show();
          }
        })
        .setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        })
        .create()
        .show();
  }

  public static String getMetadata(Context context, String name) {
    try {
      ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
          context.getPackageName(), PackageManager.GET_META_DATA);
      if (appInfo.metaData != null) {
        return appInfo.metaData.getString(name);
      }
    } catch (PackageManager.NameNotFoundException e) {
    }
    return null;
  }


}
