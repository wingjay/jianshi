package com.wingjay.jianshi.data;

/**
 * Created by wingjay on 9/30/15.
 */
public class Table {

    public static final String _ID = "id";
    public static final String CREATED_TIME = "created_time";
    public static final String MODIFIED_TIME = "modified_time";

    protected long id;
    protected long createdTime;
    protected long modifiedTime;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
