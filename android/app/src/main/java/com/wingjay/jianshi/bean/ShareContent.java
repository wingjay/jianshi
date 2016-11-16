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
 * share link + text
 */
public class ShareContent {

  @SerializedName("link")
  String link;

  @SerializedName("share_text")
  String shareText = "回归文字的本质，回归美好";

  public String getLink() {
    return link;
  }

  public String getShareText() {
    return shareText;
  }
}
