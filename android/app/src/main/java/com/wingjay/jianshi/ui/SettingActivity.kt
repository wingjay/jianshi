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
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.Toast
import com.wingjay.jianshi.BuildConfig
import com.wingjay.jianshi.R
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.manager.PayDeveloperManager
import com.wingjay.jianshi.manager.UpgradeManager
import com.wingjay.jianshi.manager.UserManager
import com.wingjay.jianshi.prefs.UserPrefs
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.ui.widget.BgColorPickDialogFragment
import kotlinx.android.synthetic.main.activity_setting.*
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import timber.log.Timber
import javax.inject.Inject

class SettingActivity : BaseActivity() {

  @Inject
  override  lateinit var userPrefs: UserPrefs

  @Inject
  lateinit var userManager: UserManager

  @Inject
  lateinit var upgradeManager: UpgradeManager

  @Inject
  lateinit var payDeveloperManager: PayDeveloperManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    JianShiApplication.getAppComponent().inject(this)
    setContentView(R.layout.activity_setting)
    vertical_write.isChecked = userPrefs.verticalWrite
    home_image_poem_switch.isChecked = userPrefs.homeImagePoemSetting
    Blaster.log(LoggingData.PAGE_IMP_SETTING)

    val builder = SpannableStringBuilder()
    builder.append(getString(R.string.version_upgrade))
    builder.append(". ")
    val length = builder.length
    builder.append(getString(R.string.current_version_code))
    builder.append(BuildConfig.VERSION_NAME)
    builder.setSpan(RelativeSizeSpan(10f), length, builder.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    version_upgrade_title.text = builder.toString()
    initViews()
    setupUpgradeWarning()
  }

  private fun initViews() {
    vertical_write.setOnCheckedChangeListener { _, isChecked ->
      userPrefs.verticalWrite = isChecked
      if (isChecked) {
        Blaster.log(LoggingData.BTN_CLK_TURN_ON_VERTICAL_TEXT)
      } else {
        Blaster.log(LoggingData.BTN_CLK_TURN_OFF_VERTICAL_TEXT)
      }
    }
    home_image_poem_switch.setOnCheckedChangeListener {  _, isChecked ->
      userPrefs.setHomeImagePoem(isChecked)
      if (isChecked) {
        Blaster.log(LoggingData.BTN_CLK_SHOW_HOME_IMAGE)
      } else {
        Blaster.log(LoggingData.BTN_CLK_TURN_OFF_HOME_IMAGE)
      }
    }

    version_upgrade.setOnClickListener {
      val progressDialog = ProgressDialog.show(this, getString(R.string.checking_upgrade), "")
      upgradeManager.checkUpgradeObservable()
          .doOnTerminate { progressDialog.dismiss() }
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(Action1 { versionUpgrade ->
            if (!isUISafe) {
              return@Action1
            }

            if (versionUpgrade == null) {
              makeToast(getString(R.string.current_newest_verion))
              return@Action1
            }

            val upgradeInfo = getString(R.string.upgrade_info,
                versionUpgrade.versionName,
                versionUpgrade.description)
            val builder = AlertDialog.Builder(this@SettingActivity)

            builder.setTitle(R.string.upgrade_title)
                .setMessage(upgradeInfo)
                .setPositiveButton(R.string.upgrade) { _, _ ->
                  val browserIntent = Intent(Intent.ACTION_VIEW,
                      Uri.parse(versionUpgrade.downloadLink))
                  startActivity(browserIntent)
                }
                .setNegativeButton(R.string.next_time) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.create().show()
          }, Action1 { throwable -> Timber.e(throwable, "upgrade failure") })
    }

    send_feedback.setOnClickListener {
      val i = Intent(Intent.ACTION_SENDTO)
      i.type = "*/*"
      i.data = Uri.parse("mailto:")
      i.putExtra(Intent.EXTRA_EMAIL, arrayOf("yinjiesh@126.com"))
      i.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.feedback_email_subject))
      i.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.feedback_email_prefix))
      try {
        if (i.resolveActivity(packageManager) != null) {
          startActivity(Intent.createChooser(i, resources.getString(R.string.send_email)))
        }
      } catch (ex: android.content.ActivityNotFoundException) {
        Toast.makeText(this, resources.getString(R.string.email_client_no_found),
            Toast.LENGTH_SHORT).show()
      }

    }

    customize_bg_color.setOnClickListener {
      val bgColorPickDialogFragment = BgColorPickDialogFragment()
      bgColorPickDialogFragment.onBackgroundColorChangedListener = { newColorRes ->
        userPrefs.backgroundColor = newColorRes
        this@SettingActivity.setContainerBgColor(newColorRes)
        setResult(Activity.RESULT_OK)
      }
      bgColorPickDialogFragment.show(supportFragmentManager, null)
    }

    pay_developer_dialog.setOnClickListener {
      if (userPrefs.localPayDeveloperDialogData != null) {
        payDeveloperManager.displayPayDeveloperDialog(this@SettingActivity, userPrefs.localPayDeveloperDialogData!!)
      } else {
        payDeveloperManager.updateLocalPayDeveloperDialogInfo()
      }
    }

    logout.setOnClickListener {
      Blaster.log(LoggingData.BTN_CLK_LOGOUT)
      userManager.logout(this)
    }
  }

  private fun setupUpgradeWarning() {
    val versionUpgrade = userPrefs.versionUpgrade
    if (versionUpgrade == null) {
      version_upgrade_warning.visibility = View.GONE
    } else {
      version_upgrade_warning.visibility = View.VISIBLE
    }
  }
}
