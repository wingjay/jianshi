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
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import com.wingjay.jianshi.Constants
import com.wingjay.jianshi.R
import com.wingjay.jianshi.bean.ShareContent
import com.wingjay.jianshi.db.service.DiaryService
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.manager.ScreenshotManager
import com.wingjay.jianshi.network.UserService
import com.wingjay.jianshi.prefs.UserPrefs
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.util.DisplayUtil
import com.wingjay.jianshi.util.IntentUtil
import com.wingjay.jianshi.util.LanguageUtil
import kotlinx.android.synthetic.main.activity_view.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ViewActivity : BaseActivity() {

  private var diaryUuid: String? = null
  private var verticalStyle = false

  @Inject
  lateinit var diaryService: DiaryService

  @Inject
  override lateinit var userPrefs: UserPrefs

  @Inject
  lateinit var userService: UserService

  @Inject
  lateinit var screenshotManager: ScreenshotManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_view)
    JianShiApplication.getAppComponent().inject(this)
    val userPrefs = UserPrefs(this@ViewActivity)
    verticalStyle = userPrefs.verticalWrite
    setVisibilityByVerticalStyle()

    diaryUuid = intent.getStringExtra(EditActivity.DIARY_UUID)
    if (diaryUuid == null) {
      finish()
    }

    loadDiary()

    view_edit.setOnClickListener {
      setResult(Activity.RESULT_OK)
      Blaster.log(LoggingData.BTN_CLK_UPDATE_DIARY)
      val i = EditActivity.createIntentWithId(this@ViewActivity, diaryUuid!!)
      startActivity(i)
      finish()
    }
    Timber.i("contentWidth : %s", container!!.width)

    view_share.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_SHARE_DIARY_IMAGE)

      val target = if (verticalStyle) container else normal_container
      val dialog = ProgressDialog.show(this, "", "加载中...")
      screenshotManager.shotScreen(target, "temp.jpg")
          .observeOn(Schedulers.io())
          .filter { s -> !TextUtils.isEmpty(s) }
          .flatMap(Func1<String, Observable<Pair<String, ShareContent>>> { path ->
            Timber.i("ViewActivity ScreenshotManager 1 %s", Thread.currentThread().name)
            var shareContent = ShareContent()
            try {
              val response = userService.shareContent.toBlocking().first()
              if (response.rc == Constants.ServerResultCode.RESULT_OK && response.data != null) {
                shareContent = response.data
              }
            } catch (e: Exception) {
              Timber.e(e, "getShareContent() error")
              return@Func1 Observable.just(Pair.create(path, shareContent))
            }

            Observable.just(Pair.create(path, shareContent))
          })
          .observeOn(AndroidSchedulers.mainThread())
          .doOnTerminate { dialog.dismiss() }
          .subscribe(Action1 { stringShareContentPair ->
            Timber.i("ViewActivity ScreenshotManager 2 %s", Thread.currentThread().name)
            if (!isUISafe) {
              return@Action1
            }
            IntentUtil.shareLinkWithImage(this@ViewActivity,
                stringShareContentPair.second,
                Uri.fromFile(File(stringShareContentPair.first)))
          }, Action1 { throwable ->
            makeToast(getString(R.string.share_failure))
            Timber.e(throwable, "screenshot share failure")
          })
    }
    Blaster.log(LoggingData.PAGE_IMP_VIEW)
  }

  private fun loadDiary() {
    diaryService.getDiaryByUuid(diaryUuid)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { diary ->
          if (diary != null) {
            showDiary(
                diary.title,
                diary.content,
                LanguageUtil.getDiaryDateEnder(
                    applicationContext,
                    diary.time_created))
          }
        }
  }

  private fun showDiary(titleString: String, contentString: String, contentDate: String) {
    setVisibilityByVerticalStyle()

    if (verticalStyle) {
      vertical_view_content.setText(titleString)
      vertical_view_content.setText(contentString)
      vertical_view_date.setText(contentDate)
      container.setBackgroundResource(userPrefs.backgroundColor)
      container.post {
        val scrollOffsetX = container!!.width - DisplayUtil.getDisplayWidth()
        Timber.i("contentWidthAfter : %s", container!!.measuredHeight)
        if (scrollOffsetX > 0) {
          vertical_container.scrollBy(scrollOffsetX, 0)
        }
      }
    } else {
      normal_container.setBackgroundResource(userPrefs.backgroundColor)
      view_title.text = titleString
      view_content.text = "$contentString${getString(R.string.space_of_date_record_end)}$contentDate"
    }
  }

  private fun setVisibilityByVerticalStyle() {
    hori_container.visibility = if (verticalStyle) View.GONE else View.VISIBLE
    vertical_container.visibility = if (verticalStyle) View.VISIBLE else View.GONE
  }

  companion object {

    fun createIntent(context: Context, diaryId: String): Intent {
      val i = Intent(context, ViewActivity::class.java)
      i.putExtra(EditActivity.DIARY_UUID, diaryId)
      return i
    }
  }

}
