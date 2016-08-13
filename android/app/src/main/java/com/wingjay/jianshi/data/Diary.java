package com.wingjay.jianshi.data;

import com.wingjay.jianshi.FullDateManager;

/**
 * Created by wingjay on 9/30/15.
 */
public class Diary extends Table {

    protected static String TABLE_NAME = "diary";

    public final static String DEVICE_ID = "device_id";
    public final static String TITLE = "title";
    public final static String CONTENT = "content";
    public final static String DELETED = "deleted";

    private String deviceId;
    private String title;
    private String content;

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

    public String getChineseCreatedTime() {
        FullDateManager fullDateManager = new FullDateManager(createdTime);
        return fullDateManager.getFullDate();
    }

}
