#!/usr/bin/env python
# -*- coding: utf-8 -*-
from werkzeug import exceptions

# User releated error
class UnknownError(Exception):
    rc = 1
    msg = '好像出了故障,请稍后重试'


class DBError(Exception):
    rc = 2
    msg = 'An error has occurred at DB layer'


class AccessForbiddenError(exceptions.Forbidden):
    rc = 403
    msg = 'Forbidden'
    no_warning_email = True


class ArgumentShouldNotBeNull(Exception):
    rc = 700
    msg = '虽然不想说,你的输入有点问题吧'
    no_warning_email = True

# User related


class AuthTokenInvalid(exceptions.NotFound):
    rc = 1000
    msg = "当前用户失效,快去重新登录"
    no_warning_email = True


class UserCreateFailure(Exception):
    rc = 1001
    msg = "用户创建失败,呃呃"


class UserLoginFailure(Exception):
    rc = 1002
    msg = "用户登录失败,请检查输入信息或稍后重试"
    no_warning_email = True


class UserNotFound(exceptions.NotFound):
    rc = 1003
    msg = "未找到用户,你一定是个假用户"
    no_warning_email = True


class WrongPassword(Exception):
    rc = 1004
    msg = "你竟然输入了错误的密码,哼哼"
    no_warning_email = True


class UserEmailAlreadyUsed(Exception):
    rc = 1005
    msg = "邮箱已经被注册,Sorry~"
    no_warning_email = True


class CanNotFindUserId(exceptions.NotFound):
    rc = 1006
    msg = "你确定你已经注册了?"
    no_warning_email = True


class UserDeleteFailure(Exception):
    rc = 1007
    msg = "User delete error"
    no_warning_email = True


class EmailFormatWrong(Exception):
    rc = 1008
    msg = "喂喂请检查邮箱格式啊"
    no_warning_email = True


class PasswordLengthMustBiggerThanSix(Exception):
    rc = 1009
    msg = "密码长度至少为六,呵呵"
    no_warning_email = True


class PasswordChangeError(Exception):
    rc = 1010
    msg = "抱歉密码修改失败,请重试"
    no_warning_email = False


# Diary related
class InvalidUserIdDuringCreatingDiary(exceptions.Forbidden):
    rc = 2000
    msg = "User Id is invalid during creating diary"
    no_warning_email = True


class DiaryEmpty(exceptions.NotFound):
    rc = 2001
    msg = "你是不是把这篇日记删掉了"
    no_warning_email = True


class NotAccessForThisDiary(exceptions.Forbidden):
    rc = 2002
    msg = "没权限,看不到,别怪我"
    no_warning_email = True


class UpdateDiaryFailure(Exception):
    rc = 2003
    msg = "擦日记更新失败,请重试"


class NoAccessForOthersDiary(exceptions.Forbidden):
    rc = 2004
    msg = "喂喂你没权限看别人的日记"


# DB
class DbCreateError(Exception):
    rc = 3000
    msg = "DB Creation error"


class DbUpdateError(Exception):
    rc = 3001
    msg = "DB Update error"


class DbDeleteError(Exception):
    rc = 3002
    msg = "Db Deletion error"


class DbReadError(Exception):
    rc = 3003
    msg = "Db Read Error"


# Email
class EmailSendError(Exception):
    rc = 4000
    msg = "邮件发送失败啦,请过会再试下!"


class EmailReceiverError(Exception):
    rc = 4001
    msg = "收件人信息悲剧了,是不是输错啦!"