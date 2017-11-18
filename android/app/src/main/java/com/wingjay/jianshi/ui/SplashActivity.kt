/*
 * Created by wingjay on 11/16/16 3:31 PM
 * Copyright (c) 2016.  All rights reserved.
 *
 * Last modified 11/10/16 11:05 AM
 *
 * Reach me: https://github.com/wingjay
 * Email: yinjiesh@126.com
 */

package com.wingjay.jianshi.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils

import com.wingjay.jianshi.R
import com.wingjay.jianshi.prefs.UserPrefs
import com.wingjay.jianshi.ui.base.BaseActivity

import java.lang.ref.WeakReference

class SplashActivity : BaseActivity() {

  private val handler = MyHandler(this@SplashActivity)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    handler.sendEmptyMessageDelayed(JUMP_TO_NEXT, 500)
  }

  private class MyHandler internal constructor(activity: BaseActivity) : Handler() {
    private val weakReference: WeakReference<BaseActivity> = WeakReference(activity)

    override fun handleMessage(msg: Message) {
      when (msg.what) {
        JUMP_TO_NEXT -> {
          val activity = weakReference.get() ?: return
          val userPrefs = UserPrefs(activity)
          if (userPrefs.isGuestUser || (!TextUtils.isEmpty(userPrefs.authToken) && userPrefs.user != null)) {
            activity.startActivity(MainActivity.createIntent(activity))
          } else {
            activity.startActivity(Intent(activity, SignUpActivity::class.java))
          }
          activity.finish()
        }
      }
      super.handleMessage(msg)
    }
  }

  companion object {

    private val JUMP_TO_NEXT = 1
  }
}
