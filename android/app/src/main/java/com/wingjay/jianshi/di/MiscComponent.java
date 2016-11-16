/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.di;


import com.wingjay.jianshi.sync.SyncService;
import com.wingjay.jianshi.ui.adapter.DiaryListAdapter;

public interface MiscComponent {
  void inject(DiaryListAdapter adapter);
  void inject(SyncService service);
}
