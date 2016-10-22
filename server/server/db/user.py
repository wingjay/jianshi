#!/usr/bin/python
# coding: utf-8

import time

import pymysql
import pymysql.cursors

import server.db as base_db
from server.util import safetyutils
from server.data import errors

DB_NAME = 'User'

def clear_user_table():
    try:
        with base_db.get_conn().cursor() as cursor:
            sql = "drop table `User`"
            cursor.execute(sql)
    finally:
        base_db.get_conn().close()


def check_name_existance(name):
    name_hash = abs(hash(name))
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from User where name_hash = %s"
            cursor.execute(sql, str(name_hash))
            user = cursor.fetchall()
            return len(user) > 0

    finally:
        conn.close()


def create_user(name, password):
    if name is None or password is None:
        return False
    name_hash = abs(hash(name))
    password = safetyutils.get_hash_password(password)
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    time_created = int(time.time())
    new_user_id = -1
    try:
        with conn.cursor() as cursor:
            sql = "insert into `User` (`name`, `name_hash`, `password`, `time_created`, `time_modified`) " \
                  "values (%s, %s, %s, %s, %s)"
            result = cursor.execute(sql, (str(name), str(name_hash), str(password), str(time_created), '0'))
            new_user_id = cursor.lastrowid

        conn.commit()
    finally:
        conn.close()
        return new_user_id


def login(name, password):
    if name is None or password is None:
        return False
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from `User` where `name` = %s "
            cursor.execute(sql, (str(name)))
            _user = cursor.fetchone()
            if not _user:
                raise errors.UserNotFound()
            elif not safetyutils.verify_hash_password(_user['password'], password):
                raise errors.WrongPassword()
            else:
                _user.pop('password')
                return _user
    finally:
        conn.close()


def delete_user(user_id):
    """Delete user."""
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "delete from `User` where `id` = %s"
            cursor.execute(sql, (str(user_id)))

        conn.commit()
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

    finally:
        conn.close()
        return user


def get_user_with_password(user_id):
    return get_user(user_id, True)
