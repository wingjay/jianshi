package com.wingjay.jianshi.sync;


import com.google.gson.JsonObject;
import com.wingjay.jianshi.db.model.PushData;
import com.wingjay.jianshi.util.DateUtil;

public class Change {
  public enum DBKey {
    DIARY("Diary");

    String key;

    DBKey(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }

  public static void handleChangeByDBKey(DBKey dbKey, JsonObject object) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add(dbKey.getKey(), object);
    PushData pushData = new PushData();
    pushData.setData(jsonObject.toString());
    pushData.setTimeCreated(DateUtil.getCurrentTimeStamp());
    pushData.save();
  }
}
