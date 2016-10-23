package com.wingjay.jianshi.sync;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.util.GsonUtil;

import java.util.List;

public class SyncManager {

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
  }
}
