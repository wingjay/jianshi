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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wingjay.jianshi.R
import com.wingjay.jianshi.db.model.Diary
import com.wingjay.jianshi.db.service.DiaryService
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.util.DateUtil
import com.wingjay.jianshi.util.StringByTime
import kotlinx.android.synthetic.main.activity_edit.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class EditActivity : BaseActivity() {

  private var diaryUUID: String? = null

  private var unchanged = true
  private var diary: Diary? = null
  private var originContent: String? = null
  private var originTitle: String? = null
  @Inject
  lateinit var diaryService: DiaryService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit)
    JianShiApplication.getAppComponent().inject(this)
    val intent = intent
    diaryUUID = intent.getStringExtra(DIARY_UUID)

    if (diaryUUID != null) {
      loadDiary()
    }
    //manually save
    edit_save.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_SAVE_DIARY)
      saveDiary()
    }

    val textWatcher = object : SimpleTextWatcher() {
      override fun afterTextChanged(s: Editable) {
        super.afterTextChanged(s)
        unchanged = false
      }
    }

    edit_title.addTextChangedListener(textWatcher)
    edit_content.addTextChangedListener(textWatcher)

    edit_scroll_view.setOnClickListener {
      if (edit_content.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(edit_content, InputMethodManager.SHOW_IMPLICIT)
      }
    }

    edit_title.hint = StringByTime.getEditTitleHintByNow()
    edit_content.hint = StringByTime.getEditContentHintByNow()
    Blaster.log(LoggingData.PAGE_IMP_WRITE)
  }

  private fun saveDiary() {
    if (!checkNotNull()) {
      Toast.makeText(this@EditActivity, R.string.edit_content_not_null,
          Toast.LENGTH_SHORT).show()
      return
    }

    val titleString = if (TextUtils.isEmpty(edit_title.text.toString()))
      edit_title.hint.toString()
    else
      edit_title.text.toString()
    val contentString = if (TextUtils.isEmpty(edit_content.text.toString()))
      edit_content.hint.toString()
    else
      edit_content.text.toString()

    if (diary == null) {
      diary = Diary()
      diary!!.title = titleString
      diary!!.content = contentString
      diary!!.uuid = UUID.randomUUID().toString().toUpperCase()
      diary!!.time_created = DateUtil.getCurrentTimeStamp()
    } else {
      diary!!.title = titleString
      diary!!.content = contentString
      diary!!.time_modified = DateUtil.getCurrentTimeStamp()
    }
    diaryService.saveDiary(diary)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
          val i = ViewActivity.createIntent(this@EditActivity, diary!!.uuid)
          startActivity(i)
          finish()
        }
  }

  private fun checkNotNull(): Boolean {
    return !TextUtils.isEmpty(edit_title.text) || !TextUtils.isEmpty(edit_content.text)
  }

  private fun loadDiary() {
    diaryService.getDiaryByUuid(diaryUUID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { diary ->
          if (diary != null) {
            this@EditActivity.diary = diary
            originContent = diary.content
            originTitle = diary.title
            edit_title.setText(diary.title)
            edit_content.setText(diary.content)
          }
        }
  }

  override fun onBackPressed() {
    val unchangedFlag = TextUtils.equals(originContent, edit_content.text.toString().trim { it <= ' ' }) && TextUtils.equals(originTitle, edit_title.text.toString().trim { it <= ' ' }) || unchanged
    if (unchangedFlag) {
      super.onBackPressed()
    } else {
      val builder = AlertDialog.Builder(this@EditActivity)
      builder.setTitle(R.string.want_to_save)
          .setPositiveButton(R.string.yes) { _, _ -> saveDiary() }
          .setNegativeButton(R.string.no) { _, _ -> finish() }
          .create()
          .show()

    }

  }

  open inner class SimpleTextWatcher : TextWatcher {

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {

    }
  }

  companion object {

    const val DIARY_UUID = "diary_uuid"

    fun createIntentWithId(context: Context, diaryId: String): Intent {
      val i = Intent(context, EditActivity::class.java)
      i.putExtra(DIARY_UUID, diaryId)
      return i
    }
  }
}
