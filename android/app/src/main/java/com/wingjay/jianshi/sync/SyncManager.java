/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.sync;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.Constants;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.model.EventLog;
import com.wingjay.jianshi.db.model.EventLog_Table;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.db.model.PushData_Table;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.bean.SyncModel;
import com.wingjay.jianshi.prefs.UserPrefs;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Singleton
public class SyncManager {

  @Inject
  UserService userService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  Gson gson;

  @Inject
  SyncManager() {
  }

  public synchronized void sync() {
    sync(null);
  }

  public synchronized void sync(final @Nullable SyncResultListener syncResultListener) {
    final List<PushData> pushDataList = SQLite.select().from(PushData.class).queryList();
    JsonParser jsonParser = new JsonParser();
    JsonObject syncData = new JsonObject();
    JsonArray array = new JsonArray();
    for (PushData pushData : pushDataList) {
      array.add(jsonParser.parse(pushData.getData()));
    }
    syncData.add("sync_items", array);
    syncData.add("need_pull", gson.toJsonTree(1));
    syncData.add("sync_token", gson.toJsonTree(userPrefs.getSyncToken()));
    Timber.d("Sync Data : %s", syncData.toString());

    userService.sync(syncData).subscribe(new Action1<JsonDataResponse<SyncModel>>() {
      @Override
      public void call(JsonDataResponse<SyncModel> response) {
        if (response.getRc() == Constants.ServerResultCode.RESULT_OK) {
          SyncModel syncModel = response.getData();
          userPrefs.setSyncToken(syncModel.getSyncToken());
          if (syncModel.getUpsert() != null) {
            for (Diary diary : syncModel.getUpsert()) {
              diary.save();
            }
          }

          if (syncModel.getDelete() != null) {
            for (Diary diary : syncModel.getDelete()) {
              diary.save();
            }
          }

          int syncCount = syncModel.getSyncedCount();

          for (PushData pushData : pushDataList) {
            SQLite.delete()
                .from(PushData.class)
                .where(PushData_Table.id.eq(pushData.getId()))
                .execute();
            syncCount--;
            if (syncCount <= 0) {
              break;
            }
          }
        }
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        Timber.e(throwable, "Sync Failed");
      }
    });

    Observable.just(null).subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<Object>() {
          @Override
          public void call(Object o) {
            if (syncResultListener != null) {
              if (SQLite.select().from(PushData.class).queryList().size() > 0) {
                syncResultListener.onFailure();
              } else {
                syncResultListener.onSuccess();
              }
            }
          }
        });
  }

  public synchronized void syncLog() {
    final List<EventLog> logItems = SQLite.select().from(EventLog.class).queryList();
    JsonObject syncLog = new JsonObject();
    JsonArray array = new JsonArray();
    for (EventLog eventLog : logItems) {
      array.add(new Gson().toJsonTree(eventLog));
    }
    syncLog.add("log_items", array);
    userService.syncLog(syncLog).subscribe(new Action1<JsonDataResponse<JsonObject>>() {
      @Override
      public void call(JsonDataResponse<JsonObject> jsonObjectJsonDataResponse) {
        if (jsonObjectJsonDataResponse.getRc() == Constants.ServerResultCode.RESULT_OK) {
          JsonObject object = jsonObjectJsonDataResponse.getData();
          int syncedCount = object.get("synced_count").getAsInt();
          Timber.i("synced log count %d", syncedCount);
          for (EventLog eventLog : logItems) {
            SQLite.delete(EventLog.class)
                .where(EventLog_Table.id.eq(eventLog.getId()))
                .execute();
            syncedCount--;
            if (syncedCount <= 0) {
              break;
            }
          }
        }
      }
    }, new Action1<Throwable>() {
      @Override
      public void call(Throwable throwable) {
        Timber.e(throwable, "SyncLog failed");
      }
    });
  }

  public interface SyncResultListener {
    void onSuccess();

    void onFailure();
  }

}
