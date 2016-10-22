package com.wingjay.jianshi.di;

import com.wingjay.jianshi.ui.DiaryListActivity;
import com.wingjay.jianshi.ui.EditActivity;
import com.wingjay.jianshi.ui.MainActivity;
import com.wingjay.jianshi.ui.SignupActivity;
import com.wingjay.jianshi.ui.ViewActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Jay on 8/10/16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

  void inject(MainActivity obj);

  void inject(SignupActivity obj);

  void inject(DiaryListActivity obj);

  void inject(EditActivity obj);

  void inject(ViewActivity obj);
}
