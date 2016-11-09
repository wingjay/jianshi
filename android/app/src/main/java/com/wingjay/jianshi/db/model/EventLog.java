package com.wingjay.jianshi.db.model;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.wingjay.jianshi.db.JianshiDatabase;


@Table(database = JianshiDatabase.class)
public class EventLog extends BaseModel {
  @PrimaryKey(autoincrement = true)
  long id;

  @Column(name = "event_name")
  @NotNull
  @SerializedName("event_name")
  String eventName;

  @SerializedName("page_source")
  @Column(name = "page_source")
  String pageSource;

  @SerializedName("time_created")
  @Column(name = "time_created", defaultValue = "0")
  @NotNull
  long timeCreated;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getPageSource() {
    return pageSource;
  }

  public void setPageSource(String pageSource) {
    this.pageSource = pageSource;
  }

  public long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(long timeCreated) {
    this.timeCreated = timeCreated;
  }
}
