package com.wingjay.jianshi.sync;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.util.GsonUtil;
import com.wingjay.jianshi.util.RxUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.functions.Action1;

@Singleton
public class SyncManager {

  @Inject
  UserService userService;

  @Inject
  public SyncManager() {
  }

  public void sync() {
    List<PushData> pushDataList = SQLite.select().from(PushData.class).queryList();
    Gson gson = GsonUtil.getGsonWithExclusionStrategy();
    JsonParser jsonParser = new JsonParser();
    JsonObject syncData = new JsonObject();
    JsonArray array = new JsonArray();
    for (PushData pushData : pushDataList) {
      array.add(jsonParser.parse(pushData.getData()));
    }
    syncData.add("sync_items", array);
    syncData.add("need_pull", gson.toJsonTree(1));
    syncData.add("sync_token", gson.toJsonTree(""));
    Log.i("Sync Data", syncData.toString());
    userService.sync(syncData)
        .compose(RxUtil.<JsonDataResponse<Object>>normalSchedulers())
        .subscribe(new Action1<JsonDataResponse<Object>>() {
          @Override
          public void call(JsonDataResponse<Object> objectJsonDataResponse) {

          }
        }, new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            Log.e("sync", throwable.getMessage());
          }
        });
  }


}
