/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.bean;

import com.google.gson.annotations.SerializedName;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.network.UnStripable;

import java.util.List;


/**
 * Created by panl on 2016/10/24.
 * contact panlei106@gmail.com
 */

public class SyncModel extends UnStripable {
  @SerializedName("sync_token")
  String syncToken;

  @SerializedName("synced_count")
  int syncedCount;

  List<Diary> upsert;

  List<Diary> delete;


  public String getSyncToken() {
    return syncToken;
  }

  public void setSyncToken(String syncToken) {
    this.syncToken = syncToken;
  }

  public int getSyncedCount() {
    return syncedCount;
  }

  public void setSyncedCount(int syncedCount) {
    this.syncedCount = syncedCount;
  }

  public List<Diary> getUpsert() {
    return upsert;
  }

  public void setUpsert(List<Diary> upsert) {
    this.upsert = upsert;
  }

  public List<Diary> getDelete() {
    return delete;
  }

  public void setDelete(List<Diary> delete) {
    this.delete = delete;
  }
}
