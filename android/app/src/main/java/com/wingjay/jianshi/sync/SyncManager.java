package com.wingjay.jianshi.sync;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.db.model.PushData_Table;
import com.wingjay.jianshi.network.JsonDataResponse;
import com.wingjay.jianshi.network.RCCode;
import com.wingjay.jianshi.network.UserService;
import com.wingjay.jianshi.network.model.SyncModel;
import com.wingjay.jianshi.prefs.UserPrefs;
import com.wingjay.jianshi.util.GsonUtil;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class SyncManager {

  @Inject
  UserService userService;

  @Inject
  UserPrefs userPrefs;

  @Inject
  SyncManager() {
  }

  public synchronized void sync() {
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
    syncData.add("sync_token", gson.toJsonTree(userPrefs.getSyncToken()));
    Timber.d("Sync Data : %s", syncData.toString());

    JsonDataResponse<SyncModel> response = userService.sync(syncData).toBlocking().first();
    if (response.getRc() == RCCode.SUCCESS) {
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


}
