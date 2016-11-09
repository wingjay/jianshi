package com.wingjay.jianshi.bean;

import com.google.gson.annotations.SerializedName;

/**
 * share link + text
 */
public class ShareContent {

  @SerializedName("link")
  String link;

  @SerializedName("share_text")
  String shareText = "回归文字的本质,回归初心.";

  public String getLink() {
    return link;
  }

  public String getShareText() {
    return shareText;
  }
}
