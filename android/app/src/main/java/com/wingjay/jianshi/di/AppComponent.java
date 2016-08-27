package com.wingjay.jianshi.di;

import com.wingjay.jianshi.ui.MainActivity;
import com.wingjay.jianshi.ui.SignupActivity;

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

}
