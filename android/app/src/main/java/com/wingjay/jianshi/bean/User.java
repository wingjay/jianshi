package com.wingjay.jianshi.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 8/27/16.
 */
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
