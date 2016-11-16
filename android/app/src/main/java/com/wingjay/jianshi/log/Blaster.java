/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

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
