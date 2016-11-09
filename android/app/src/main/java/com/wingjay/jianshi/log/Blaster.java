package com.wingjay.jianshi.log;


import com.wingjay.jianshi.db.model.EventLog;
import com.wingjay.jianshi.util.DateUtil;

public class Blaster {

  public static void log(String eventName) {
    log(eventName, null);
  }

  public static void log(String eventName, String pageSource) {
    EventLog eventLog = new EventLog();
    eventLog.setEventName(eventName);
    eventLog.setPageSource(pageSource);
    eventLog.setTimeCreated(DateUtil.getCurrentTimeStamp());
    eventLog.save();
  }
}
