#!/usr/bin/python
# coding: utf-8
import time

import pymysql
import pymysql.cursors

import server.db as base_db
import server.db.user as db_user
from server.data import errors


def create_diary(user_id, title, content):
    if user_id is None or title is None or content is None:
        raise errors.ArgumentShouldNotBeNull()

    _user = db_user.get_user(user_id)
    if _user is None:
        raise errors.InvalidUserIdDuringCreatingDiary()

    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    time_created = int(time.time())
    new_diary_id = -1
    try:
        with conn.cursor() as cursor:
            sql = "insert into `Diary` (`user_id`, `title`, `content`, `time_created`, `time_modified`) " \
                  "values (%s, %s, %s, %s, %s)"
            cursor.execute(sql, (str(user_id), str(title), str(content), str(time_created), '0'))
            new_diary_id = cursor.lastrowid
        conn.commit()
    finally:
        conn.close()
        return new_diary_id


def get_diary(diary_id):
    diary = None
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from Diary where id = %s"
            cursor.execute(sql, str(diary_id))
            diary = cursor.fetchall()[0]

    finally:
        conn.close()
        return diary


def update_diary(user_id, diary_id, title, content):
    if _check_access(user_id, diary_id) is False:
        return
    time_modified = int(time.time())
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    result = 0
    try:
        with conn.cursor() as cursor:
            sql = "update Diary set title = %s, content = %s, time_modified = %s where id = %s"
            result = cursor.execute(sql, (str(title), str(content), str(time_modified), str(diary_id)))
        conn.commit()

    finally:
        conn.close()
        if result is not 1:
            raise errors.UpdateDiaryFailure


def delete_diary(user_id, diary_id):
    if _check_access(user_id, diary_id) is False:
        return
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "delete from `Diary` where `id` = %s"
            cursor.execute(sql, (str(diary_id)))

        conn.commit()
    finally:
        conn.close()


def _check_access(user_id, diary_id):
    diary = get_diary(diary_id)
    if diary is None:
        raise errors.DiaryEmpty()
    if diary['user_id'] is not user_id:
        raise errors.NotAccessForThisDiary()
    return True
