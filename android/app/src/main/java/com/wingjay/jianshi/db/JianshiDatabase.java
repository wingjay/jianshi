/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = JianshiDatabase.NAME, version = JianshiDatabase.VERSION)
public class JianshiDatabase {
  public static final String NAME = "jianshi";
  public static final int VERSION = 1;
}
