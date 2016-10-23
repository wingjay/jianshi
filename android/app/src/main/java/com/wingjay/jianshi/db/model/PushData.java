package com.wingjay.jianshi.db.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.wingjay.jianshi.db.JianshiDatabase;

@Table(database = JianshiDatabase.class)
public class PushData extends BaseModel{

  @PrimaryKey(autoincrement = true)
  private long id;

  @Column
  @NotNull
  private String data;

  @Column(name = "time_created")
  @NotNull
  private long timeCreated;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public long getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(long timeCreated) {
    this.timeCreated = timeCreated;
  }
}
