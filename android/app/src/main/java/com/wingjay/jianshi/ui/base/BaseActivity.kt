/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.wingjay.jianshi.R
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.prefs.UserPrefs
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

  //todo(wingjay) Add RxLifecycle function
  var isUISafe = false
  private var containerView: View? = null
  private var TAG = javaClass.simpleName + ": %s"

  private var isNeedRegister = false

  @Inject
  open lateinit var userPrefs: UserPrefs

  protected fun setNeedRegister() {
    this.isNeedRegister = true
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    JianShiApplication.getAppComponent().inject(this)
    Timber.d(TAG, "onCreate")
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
  }

  override fun setContentView(@LayoutRes layoutResID: Int) {
    super.setContentView(layoutResID)
    containerView = findViewById(R.id.layout_container)
  }

  protected fun setContainerBgColorFromPrefs() {
    if (containerView != null) {
      containerView!!.setBackgroundResource(userPrefs.backgroundColor)
    }
  }

  protected fun setContainerBgColor(colorRes: Int) {
    if (containerView != null) {
      containerView!!.setBackgroundResource(colorRes)
    }
  }

  override fun onRestart() {
    super.onRestart()
    Timber.d(TAG, "onRestart")
  }

  override fun onStart() {
    super.onStart()
    Timber.d(TAG, "onStart")
    setContainerBgColorFromPrefs()

    if (isNeedRegister) {
      EventBus.getDefault().register(this)
    }
  }

  override fun onResume() {
    super.onResume()
    isUISafe = true
    Timber.d(TAG, "onResume")
  }

  override fun onPause() {
    super.onPause()
    isUISafe = false
    Timber.d(TAG, "onPause")
  }

  override fun onStop() {
    super.onStop()
    Timber.d(TAG, "onStop")
    if (EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.d(TAG, "onDestroy")
  }

  protected fun makeToast(context: Context, content: String) {
    Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
  }

  protected fun makeToast(context: Context, @StringRes stringRes: Int) {
    Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
  }

  protected fun makeToast(content: String) {
    makeToast(this, content)
  }

  protected fun makeToast(@StringRes stringRes: Int) {
    makeToast(this, stringRes)
  }
}
