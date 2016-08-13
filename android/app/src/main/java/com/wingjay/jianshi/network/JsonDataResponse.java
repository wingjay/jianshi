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
