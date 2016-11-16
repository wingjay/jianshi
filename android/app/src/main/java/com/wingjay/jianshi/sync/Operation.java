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


public enum Operation {
  CREATE("create"),
  UPDATE("update"),
  DELETE("delete");

  private final String action;

  Operation(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }
}
