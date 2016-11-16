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
