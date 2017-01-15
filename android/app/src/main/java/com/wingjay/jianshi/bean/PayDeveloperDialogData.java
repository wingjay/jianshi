/*
 * Created by wingjay on 1/15/17 10:40 PM
 * Copyright (c) 2017.  All rights reserved.
 *
 * Last modified 1/15/17 10:40 PM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 1/15/17.
 */

public class PayDeveloperDialogData {

  @SerializedName("title")
  private String title;

  @SerializedName("message")
  private String message;

  @SerializedName("time_gap_seconds")
  private long timeGapSeconds;

  @SerializedName("ali_pay_account")
  private String aliPayAccount;

  @SerializedName("wechat_pay_account")
  private String wechatPayAccount;

  public String getAliPayAccount() {
    return aliPayAccount;
  }

  public String getWechatPayAccount() {
    return wechatPayAccount;
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public long getTimeGapSeconds() {
    return timeGapSeconds;
  }
}
