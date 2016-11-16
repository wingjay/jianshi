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

public class User {

  @SerializedName("id")
  private long id;

  @SerializedName("name")
  private String name;

  @SerializedName("encrypted_token")
  private String encryptedToken;

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEncryptedToken() {
    return encryptedToken;
  }
}
