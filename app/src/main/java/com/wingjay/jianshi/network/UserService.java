package com.wingjay.jianshi.network;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Jay on 8/10/16.
 */
public interface UserService {
  @GET("get_json")
  Observable<JsonDataResponse> getJsonTest();
}
