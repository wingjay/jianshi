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
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import com.wingjay.jianshi.Constants
import com.wingjay.jianshi.R
import com.wingjay.jianshi.db.model.Diary
import com.wingjay.jianshi.db.service.DiaryService
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.ui.adapter.DiaryListAdapter
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.util.DateUtil
import kotlinx.android.synthetic.main.activity_diary_list.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class DiaryListActivity : BaseActivity(), DiaryListAdapter.RecyclerClickListener {

  private val diaryList = ArrayList<Diary>()
  private var adapter: DiaryListAdapter? = null

  @Inject
  lateinit var diaryService: DiaryService


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_diary_list)
    JianShiApplication.getAppComponent().inject(this)
    diary_list.setHasFixedSize(true)
    diary_list.layoutManager = LinearLayoutManager(
        this@DiaryListActivity,
        LinearLayoutManager.HORIZONTAL,
        true)

    // get all local diaries
    adapter = DiaryListAdapter(this@DiaryListActivity, diaryList)
    adapter!!.setRecyclerClickListener(this)
    diary_list.adapter = adapter

    view_write.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_DIARY_LIST_WRITE)
      startActivity(Intent(this, EditActivity::class.java))
    }

    Blaster.log(LoggingData.PAGE_IMP_DIARY_LIST)
  }

  override fun onResume() {
    super.onResume()
    diaryService.diaryList
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { diaries ->
          diaryList.clear()
          diaryList.addAll(diaries)
          adapter!!.notifyDataSetChanged()
        }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == Constants.RequestCode.REQUEST_CODE_VIEW_DIARY_FROM_LIST && resultCode == Activity.RESULT_OK) {
      adapter!!.notifyItemRangeChanged(0, diaryList.size)
      adapter!!.notifyDataSetChanged()
    }
  }

  override fun onItemClick(diary: Diary) {
    Blaster.log(LoggingData.BTN_CLK_DIARY_LIST_VIEW)
    startActivity(ViewActivity.createIntent(this, diary.uuid))
  }

  override fun onItemLongClick(diary: Diary) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(R.string.do_you_want_to_delete_diary)
        .setPositiveButton(R.string.yes) { _, _ ->
          diary.time_removed = DateUtil.getCurrentTimeStamp()
          diaryService.saveDiary(diary)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe {
                diaryList.remove(diary)
                adapter!!.notifyDataSetChanged()
              }
        }
        .setNegativeButton(R.string.no, null)
        .create()
        .show()
  }
}
