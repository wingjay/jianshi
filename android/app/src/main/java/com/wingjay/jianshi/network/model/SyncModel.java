package com.wingjay.jianshi.network.model;

import com.google.gson.annotations.SerializedName;
import com.wingjay.jianshi.db.model.Diary;

import java.util.List;


/**
 * Created by panl on 2016/10/24.
 * contact panlei106@gmail.com
 */

public class SyncModel {
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
