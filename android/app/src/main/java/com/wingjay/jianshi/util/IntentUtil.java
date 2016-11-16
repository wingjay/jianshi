/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wingjay.jianshi.bean.ShareContent;

public class IntentUtil {

  private IntentUtil() {
  }

  public static void shareLinkWithImage(@NonNull Context context,
                                        @NonNull ShareContent shareContent,
                                        @Nullable Uri imageUri) {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent.getShareText());
    shareIntent.setType("text/plain");
    if (imageUri != null) {
      shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
      shareIntent.setType("image/*");
    }
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    Intent chooserIntent = Intent.createChooser(shareIntent, "分享");
    context.startActivity(chooserIntent);
  }
}
