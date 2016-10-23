package com.wingjay.jianshi.db.service;


import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.wingjay.jianshi.db.model.Diary;
import com.wingjay.jianshi.db.model.Diary_Table;

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
