package com.wingjay.jianshi.network;

import com.google.gson.JsonObject;
import com.wingjay.jianshi.bean.Diary;
import com.wingjay.jianshi.bean.User;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Jay on 8/10/16.
 */
public interface UserService {
  @GET("get")
  Observable<JsonDataResponse> getJsonTest();

  @FormUrlEncoded
  @POST("diary")
  Observable<JsonDataResponse<Diary>> createDiary(@Field(Diary.TITLE) String title,
                                                  @Field(Diary.CONTENT) String content,
                                                  @Field(Diary.CREATED_TIME) long createdTime,
                                                  @Field(Diary.DEVICE_ID) String deviceId);

  @FormUrlEncoded
  @POST("user/signup")
  Observable<JsonDataResponse<User>> signup(@Field("name") String name,
                                            @Field("password") String password);

  @FormUrlEncoded
  @POST("user/login")
  Observable<JsonDataResponse<User>> login(@Field("name") String name,
                                           @Field("password") String password);

  @FormUrlEncoded
  @POST("sync")
  Observable<JsonDataResponse<Object>> sync(@Body JsonObject jsonObject);
}
