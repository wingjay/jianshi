package com.wingjay.jianshi.network;

import com.google.gson.JsonObject;
import com.wingjay.jianshi.bean.Diary;
import com.wingjay.jianshi.bean.User;
import com.wingjay.jianshi.bean.ImagePoem;
import com.wingjay.jianshi.network.model.SyncModel;

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
  Observable<JsonDataResponse<User>> signup(@Field("email") String email,
                                            @Field("password") String password);

  @FormUrlEncoded
  @POST("user/login")
  Observable<JsonDataResponse<User>> login(@Field("email") String email,
                                           @Field("password") String password);

  @POST("sync")
  Observable<JsonDataResponse<SyncModel>> sync(@Body JsonObject jsonObject);

  @GET("home/image_poem")
  Observable<JsonDataResponse<ImagePoem>> getImagePoem();

}
