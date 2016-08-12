package com.wingjay.jianshi.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 8/10/16.
 */
public class JsonResponse {

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
