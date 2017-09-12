#!/usr/bin/python
# coding: utf-8

import time

import pymysql
import pymysql.cursors

import server.db.init as base_db
from server.util import safetyutils, regxutils
from server.data import errors
from server import app

logger = app.logger
DB_NAME = 'User'


def clear_user_table():
    try:
        with base_db.get_conn().cursor() as cursor:
            sql = "drop table `User`"
            cursor.execute(sql)
    finally:
        base_db.get_conn().close()


def check_email_existance(email):
    email_hash = abs(hash(email))
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from User where email_hash = %s"
            cursor.execute(sql, str(email_hash))
            user = cursor.fetchall()
            return len(user) > 0

    finally:
        conn.close()


def create_user(email, password):
    """If success, return real user_id, otherwise return -1.
    """
    new_user_id = -1
    if email is None or password is None:
        return new_user_id
    if not regxutils.validate_email(email):
        raise errors.EmailFormatWrong()
    if check_email_existance(email):
        raise errors.UserEmailAlreadyUsed()
    email_hash = abs(hash(email))
    password = safetyutils.get_hash_password(password)
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    time_created = int(time.time())

    try:
        with conn.cursor() as cursor:
            sql = "insert into `User` (`email`, `email_hash`, `password`, `time_created`, `time_modified`) " \
                  "values (%s, %s, %s, %s, %s)"
            cursor.execute(sql, (str(email), str(email_hash), str(password), str(time_created), '0'))
            new_user_id = cursor.lastrowid

        conn.commit()
    except Exception as e:
        logger.exception(e)
        raise errors.UserCreateFailure()
    finally:
        conn.close()
        if new_user_id == -1:
            raise errors.UserCreateFailure()
        return new_user_id


def login(email, password):
    if email is None or password is None:
        return False
    email_hash = abs(hash(email))
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    _user = None
    try:
        with conn.cursor() as cursor:
            sql = "select * from `User` where `email_hash` = %s "
            cursor.execute(sql, (str(email_hash)))
            _user = cursor.fetchone()
    except Exception as e:
        logger.exception(e)
    finally:
        conn.close()
        if not _user:
            raise errors.UserNotFound()
        elif not safetyutils.verify_hash_password(_user['password'], password):
            raise errors.WrongPassword()
        else:
            _user.pop('password')
            return _user


def delete_user(user_id):
    """Delete user."""
    current_time = int(time.time())
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "update `User` set time_removed = %s, time_modified = %s where `id` = %s"
            cursor.execute(sql, (str(current_time), str(current_time), str(user_id)))

        conn.commit()
    except Exception as e:
        logger.exception(e)
        raise errors.UserDeleteFailure(e)
    finally:
        conn.close()


def update_password(email, new_password):
    """update password for existed email which must be verified first
    Note: length of password must be at least 6.
    """
    if not new_password or len(new_password) < 6:
        raise errors.PasswordLengthMustBiggerThanSix()
    email_hash = abs(hash(email))
    new_password = safetyutils.get_hash_password(new_password)
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "update `User` set password = %s where `email_hash` = %s"
            cursor.execute(sql, (str(new_password), str(email_hash)))
        conn.commit()
    except Exception as e:
        logger.exception(e)
        raise errors.PasswordChangeError(e)
    finally:
        conn.close()


def get_user(user_id, with_password=False):
    user = None
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from User where id = %s"
            cursor.execute(sql, str(user_id))
            user = cursor.fetchall()[0]
            if not with_password:
                user.pop('password')
    except Exception as e:
        logger.exception(e)
    finally:
        conn.close()
        return user


def get_user_with_password(user_id):
    return get_user(user_id, True)
