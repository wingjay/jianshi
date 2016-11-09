package com.wingjay.jianshi.di;


import com.wingjay.jianshi.sync.SyncService;
import com.wingjay.jianshi.ui.adapter.DiaryListAdapter;

public interface MiscComponent {
  void inject(DiaryListAdapter adapter);
  void inject(SyncService service);
}
