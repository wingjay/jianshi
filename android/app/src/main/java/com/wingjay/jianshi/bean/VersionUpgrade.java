/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 11/7/16.
 */
public class VersionUpgrade {

  @SerializedName("version_name")
  String versionName;

  @SerializedName("desc")
  String description;

  @SerializedName("link")
  String downloadLink;

  public String getDescription() {
    return description;
  }

  public String getVersionName() {
    return versionName;
  }

  public String getDownloadLink() {
    return downloadLink;
  }
}
