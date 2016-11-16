/*
 * Created by wingjay on 11/16/16 3:32 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 8/10/16.
 */
public class JsonResponse extends UnStripable{

  @SerializedName("rc")
  private int rc;

  @SerializedName("msg")
  private String msg;

  public int getRc() {
    return rc;
  }

  public String getMsg() {
    return msg;
  }

}
