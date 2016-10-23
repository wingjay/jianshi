package com.wingjay.jianshi.db.service;


import com.google.gson.JsonObject;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.model.Diary_Table;
import com.wingjay.jianshi.sync.Change;
import com.wingjay.jianshi.sync.Operation;
import com.wingjay.jianshi.util.DateUtil;
import com.wingjay.jianshi.util.GsonUtil;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func0;

public class DiaryService {

  @Inject
  DiaryService() {
  }

  public Observable<Void> saveDiary(final Diary diary) {
    return Observable.defer(new Func0<Observable<Void>>() {
      @Override
      public Observable<Void> call() {
        JsonObject jsonObject = new JsonObject();
        diary.setTime(DateUtil.getCurrentTimeStamp());
        if (diary.getTime_removed() > 0) {
          jsonObject.add(Operation.DELETE.getAction(),
              GsonUtil.getGsonWithExclusionStrategy().toJsonTree(diary));
        } else if (diary.getTime_modified() >= diary.getTime_created()) {
          jsonObject.add(Operation.UPDATE.getAction(),
              GsonUtil.getGsonWithExclusionStrategy().toJsonTree(diary));
        } else {
          jsonObject.add(Operation.CREATE.getAction(),
              GsonUtil.getGsonWithExclusionStrategy().toJsonTree(diary));
        }
        Change.handleChangeByDBKey(Change.DBKey.DIARY, jsonObject);
        diary.save();
        return Observable.just(null);
      }
    });
  }

  public Observable<List<Diary>> getDiaryList() {
    return Observable.defer(new Func0<Observable<List<Diary>>>() {
      @Override
      public Observable<List<Diary>> call() {
        return Observable.just(fetchDiaryListFromDB());
      }
    });
  }

  private List<Diary> fetchDiaryListFromDB() {
    return SQLite.select()
        .from(Diary.class)
        .where(Diary_Table.time_removed.eq(0))
        .queryList();
  }

  public Observable<Diary> getDiaryByUuid(final String uuid) {
    return Observable.defer(new Func0<Observable<Diary>>() {
      @Override
      public Observable<Diary> call() {
        Diary diary = SQLite
            .select()
            .from(Diary.class)
            .where(Diary_Table.uuid.is(uuid))
            .querySingle();
        return Observable.just(diary);
      }
    });
  }
}
