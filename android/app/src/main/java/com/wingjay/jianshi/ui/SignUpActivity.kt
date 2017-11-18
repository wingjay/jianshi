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
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.util.PatternsCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.wingjay.jianshi.BuildConfig
import com.wingjay.jianshi.R
import com.wingjay.jianshi.global.JianShiApplication
import com.wingjay.jianshi.log.Blaster
import com.wingjay.jianshi.log.LoggingData
import com.wingjay.jianshi.manager.UserManager
import com.wingjay.jianshi.network.UserService
import com.wingjay.jianshi.prefs.UserPrefs
import com.wingjay.jianshi.ui.base.BaseActivity
import com.wingjay.jianshi.util.RxUtil
import kotlinx.android.synthetic.main.activity_signup.*
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Signup Activity.
 */
class SignUpActivity : BaseActivity() {

  @Inject
  lateinit var userService: UserService

  @Inject
  lateinit var userManager: UserManager

  @Inject
  lateinit var userPrefs: UserPrefs

  private val emailText: String
    get() = email.text.toString().trim { it <= ' ' }

  private val password: String
    get() = passwordEdit.text.toString()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_signup)
    JianShiApplication.getAppComponent().inject(this)

    if (userPrefs.authToken != null) {
      startActivity(MainActivity.createIntent(this))
      finish()
      return
    }
    skip.visibility = if (BuildConfig.DEBUG) View.VISIBLE else View.GONE
    Blaster.log(LoggingData.PAGE_IMP_SIGN_UP)
    setListeners()
  }


  private fun setListeners() {
    signup.setOnClickListener { signUp() }
    login.setOnClickListener { login() }
    forget_password.setOnClickListener { forgetPassword() }
    skip.setOnClickListener { skip() }
    guestUser.setOnClickListener {  guestLogin() }
  }

  private fun guestLogin() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("真的不想注册吗？")
    builder.setMessage("不注册使用，数据仅能存在本机上，我们将无法为您同步数据，在您卸载重装或者更换设备的时候，您的记录将不会保留！！！")
    builder.setPositiveButton("不注册", { _, _ ->
      userPrefs.setGuestUser()
      startActivity(MainActivity.createIntent(this))
    })
    builder.setNegativeButton("还是注册吧", null)
    builder.create().show()
  }

  private fun signUp() {
    Blaster.log(LoggingData.BTN_CLK_SIGN_UP)
    if (!checkEmailPwdNonNull()) {
      return
    }

    if (password.length < 6) {
      passwordEdit.error = getString(R.string.password_length_must_bigger_than_6)
      return
    }

    userManager.signup(this@SignUpActivity,
        emailText,
        password)
  }

  private fun login() {
    Blaster.log(LoggingData.BTN_CLK_LOGIN)
    if (!checkEmailPwdNonNull()) {
      return
    }
    userManager.login(this@SignUpActivity,
        emailText,
        password)
  }

  private fun forgetPassword() {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(R.string.please_enter_your_email)
    val inputView = LayoutInflater.from(this).inflate(R.layout.layout_reset_password, null)
    val input = inputView.findViewById<EditText>(R.id.emailEdit)
    builder.setView(inputView)
    val weakReference = WeakReference<Context>(this@SignUpActivity)

    builder.setPositiveButton(R.string.send_email_for_updating_password, DialogInterface.OnClickListener { _, _ ->
      val email = input.text.toString().trim { it <= ' ' }
      if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
        makeToast(R.string.wrong_email_format)
        return@OnClickListener
      }
      userService.forgetPassword(email)
          .compose(RxUtil.normalSchedulers())
          .subscribe({ jsonResponse ->
            if (weakReference.get() != null && jsonResponse != null) {
              if (jsonResponse.rc == 0) {
                makeToast(weakReference.get(), R.string.success_send_password_changing_email)
              } else {
                makeToast(weakReference.get(), jsonResponse.msg)
              }
            }
          }) {
            if (weakReference.get() != null) {
              makeToast(weakReference.get(), R.string.server_request_error)
            }
          }
    })
    builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }

    builder.show()
  }

  private fun checkEmailPwdNonNull(): Boolean {
    if (TextUtils.isEmpty(emailText)) {
      email.error = getString(R.string.email_should_not_be_null)
      return false
    }
    if (!PatternsCompat.EMAIL_ADDRESS.matcher(emailText).matches()) {
      email.error = getString(R.string.wrong_email_format)
      return false
    }
    if (TextUtils.isEmpty(password)) {
      passwordEdit.error = getString(R.string.password_should_not_be_null)
      return false
    }

    return true
  }

  private fun skip() {
    startActivity(MainActivity.createIntent(this@SignUpActivity))
  }

  companion object {

    fun createIntent(context: Context): Intent {
      val intent = Intent(context, SignUpActivity::class.java)
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or
          Intent.FLAG_ACTIVITY_NO_ANIMATION)
      return intent
    }
  }
}
