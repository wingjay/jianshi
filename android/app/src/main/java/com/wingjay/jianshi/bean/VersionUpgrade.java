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
