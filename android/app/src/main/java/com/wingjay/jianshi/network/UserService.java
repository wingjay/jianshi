package com.wingjay.jianshi.network;

import com.wingjay.jianshi.bean.Diary;
import com.wingjay.jianshi.bean.User;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Jay on 8/10/16.
 */
public interface UserService {
  @GET("get_json")
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

}
