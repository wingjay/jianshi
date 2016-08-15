package com.wingjay.jianshi.data;

import com.google.gson.annotations.SerializedName;
import com.wingjay.jianshi.util.FullDateManager;

/**
 * Created by wingjay on 9/30/15.
 */
public class Diary extends Table {

  protected static String TABLE_NAME = "diary";
  public final static String DEVICE_ID = "device_id";
  public final static String TITLE = "title";
  public final static String CONTENT = "content";
  public final static String DELETED = "deleted";

  @SerializedName("device_id")
  private String deviceId;

  @SerializedName("title")
  private String title;

  @SerializedName("content")
  private String content;

  @SerializedName("deleted")
  private int deleted;

  public static String getTableName() {
      return TABLE_NAME;
  }

  public String getDeviceId() {
      return deviceId;
  }

  public void setTitle(String title) {
      this.title = title;
  }

  public String getTitle() {
      return title;
  }

  public void setContent(String content) {
      this.content = content;
  }

  public String getContent() {
      return content;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public int getDeleted() {
    return deleted;
  }

  public void setDeleted(int deleted) {
    this.deleted = deleted;
  }

  public String getChineseCreatedTime() {
      FullDateManager fullDateManager = new FullDateManager(createdTime);
      return fullDateManager.getFullDate();
  }

}
