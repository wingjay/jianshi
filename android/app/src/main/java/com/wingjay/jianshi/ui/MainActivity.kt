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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewTreeObserver
import com.squareup.picasso.Picasso
import com.wingjay.jianshi.BuildConfig
import com.wingjay.jianshi.Constants
import com.wingjay.jianshi.R
import com.wingjay.jianshi.bean.ImagePoem
import com.wingjay.jianshi.events.InvalidUserTokenEvent
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.manager.FullDateManager
import com.wingjay.jianshi.manager.PayDeveloperManager
import com.wingjay.jianshi.manager.UpgradeManager
import com.wingjay.jianshi.manager.UserManager
import com.wingjay.jianshi.network.UserService
import com.wingjay.jianshi.prefs.UserPrefs
import com.wingjay.jianshi.sync.SyncService
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.util.RxUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

  @Inject
  lateinit var userService: UserService

  @Inject
  lateinit var userManager: UserManager

  @Inject
  lateinit var upgradeManager: UpgradeManager

  @Inject
  override lateinit var userPrefs: UserPrefs

  @Inject
  lateinit var payDeveloperManager: PayDeveloperManager

  @Volatile
  private var year: Int = 0
  @Volatile
  private var month: Int = 0
  @Volatile
  private var day: Int = 0


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    JianShiApplication.getAppComponent().inject(this@MainActivity)

    setContentView(R.layout.activity_main)
    setNeedRegister()
    debug.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE

    if (savedInstanceState != null) {
      year = savedInstanceState.getInt(YEAR)
      month = savedInstanceState.getInt(MONTH)
      day = savedInstanceState.getInt(DAY)
    } else {
      setTodayAsFullDate()
      tryNotifyUpgrade()

      showPayDialog()
    }
    updateFullDate()

    writer.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_HOME_WRITE)
      val i = Intent(this@MainActivity, EditActivity::class.java)
      startActivity(i)
    }

    reader.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_HOME_VIEW)
      startActivity(Intent(this@MainActivity, DiaryListActivity::class.java))
    }

    setting.setOnClickListener {
      val intent = Intent(this@MainActivity, SettingActivity::class.java)
      startActivityForResult(intent, Constants.RequestCode.REQUEST_CODE_BG_COLOR_CHANGE)
    }

    debug.setOnClickListener {
      startActivity(Intent(this, DebugActivity::class.java))
    }

    Blaster.log(LoggingData.PAGE_IMP_HOME)
    SyncService.syncImmediately(this)
  }

  private fun showPayDialog() {
    payDeveloperManager.payDeveloperDialogData
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ payDeveloperDialogData ->
          if (payDeveloperDialogData != null && userPrefs.ableToShowPayDeveloperDialog(payDeveloperDialogData.timeGapSeconds)) {
            payDeveloperManager.displayPayDeveloperDialog(this@MainActivity, payDeveloperDialogData)
            userPrefs.savePayDeveloperDialogData(payDeveloperDialogData)
          }
        }) { }
  }

  private fun tryNotifyUpgrade() {
    upgradeManager.checkUpgradeObservable()
        .compose(RxUtil.normalSchedulers())
        .subscribe(Action1 { versionUpgrade ->
          if (!isUISafe) {
            return@Action1
          }
          if (versionUpgrade != null && !userPrefs.isNewVersionNotified(versionUpgrade)) {
            val upgradeInfo = getString(R.string.upgrade_info,
                versionUpgrade.versionName,
                versionUpgrade.description)
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle(R.string.upgrade_title)
                .setMessage(upgradeInfo)
                .setPositiveButton(R.string.upgrade) { _, _ ->
                  val browserIntent = Intent(Intent.ACTION_VIEW,
                      Uri.parse(versionUpgrade.downloadLink))
                  startActivity(browserIntent)
                  userPrefs.addNotifiedNewVersionName(versionUpgrade)
                }
                .setNegativeButton(R.string.next_time) { dialogInterface, i ->
                  makeToast(getString(R.string.go_to_setting_for_upgrading))
                  dialogInterface.dismiss()
                  userPrefs.addNotifiedNewVersionName(versionUpgrade)
                }
            builder.create().show()
          }
        }, Action1 { throwable -> Timber.e(throwable, "check upgrade failure") })

  }

  override fun onStart() {
    super.onStart()
    setupImagePoemBackground()
  }

  private fun setupImagePoemBackground() {
    if (!userPrefs.homeImagePoemSetting) {
      setContainerBgColorFromPrefs()
      return
    }

    if (!userPrefs.canFetchNextHomeImagePoem() && userPrefs.lastHomeImagePoem != null) {
      // use last imagePoem data
      setImagePoem(userPrefs.lastHomeImagePoem)
      return
    }

    if (background_image.width == 0) {
      background_image.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          background_image.viewTreeObserver.removeGlobalOnLayoutListener(this)
          loadImagePoem()
        }
      })
    } else {
      loadImagePoem()
    }
  }

  private fun loadImagePoem() {
    userService.getImagePoem(background_image.width, background_image.height)
        .compose(RxUtil.normalSchedulers())
        .filter { response -> response.rc == Constants.ServerResultCode.RESULT_OK && response.data != null }
        .subscribe({ response ->
          setImagePoem(response.data)
          userPrefs.setLastHomeImagePoem(response.data)
          userPrefs.setNextFetchHomeImagePoemTime(response.data.nextFetchTimeSec)
          Blaster.log(LoggingData.LOAD_IMAGE_EVENT)
        }) { throwable -> Timber.e(throwable, "getImagePoem() failure") }
  }

  private fun setImagePoem(imagePoem: ImagePoem?) {
    setContainerBgColor(R.color.transparent)
    if (imagePoem == null) {
      Picasso.with(this)
          .load(R.mipmap.default_home_image)
          .fit()
          .centerCrop()
          .into(background_image)
    } else {
      Picasso.with(this@MainActivity)
          .load(imagePoem.imageUrl)
          .placeholder(R.mipmap.default_home_image)
          .fit()
          .centerCrop()
          .into(background_image)
      three_line_poem.setThreeLinePoem(imagePoem.poem)
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Constants.RequestCode.REQUEST_CODE_BG_COLOR_CHANGE) {
      if (resultCode == Activity.RESULT_OK) {
        setContainerBgColorFromPrefs()
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onEvent(event: InvalidUserTokenEvent) {
    makeToast(getString(R.string.invalid_token_force_logout))
    userManager.logoutByInvalidToken(this)
  }

  private fun setDate(date: DateTime) {
    year = date.year
    month = date.monthOfYear
    day = date.dayOfMonth
  }

  private fun setTodayAsFullDate() {
    val currentDateTime = DateTime()
    setDate(currentDateTime)
  }

  private fun updateFullDate() {
    val fullDateManager = FullDateManager()
    yearText.text = fullDateManager.getYear(year)
    monthText.text = fullDateManager.getMonth(month)
    dayText.text = fullDateManager.getDay(day)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(YEAR, year)
    outState.putInt(MONTH, month)
    outState.putInt(DAY, day)
    super.onSaveInstanceState(outState)
  }

  companion object {

    private const val YEAR = "year"
    private const val MONTH = "month"
    private const val DAY = "day"

    fun createIntent(context: Context): Intent {
      val intent = Intent(context, MainActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_NO_ANIMATION)
      return intent
    }
  }
}
