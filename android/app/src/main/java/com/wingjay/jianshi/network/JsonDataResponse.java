/*
 * Created by wingjay on 11/16/16 3:32 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 8/13/16 11:49 PM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 8/10/16.
 */
public class JsonDataResponse<T> extends JsonResponse {
  @SerializedName("data")
  private T data;

  public T getData() {
    return data;
  }

}
