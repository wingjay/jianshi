#!/usr/bin/python
# coding: utf-8
import time

import pymysql
import pymysql.cursors

import server.db.init as base_db
from server import app
import server.db.user as db_user
from server.data import errors


logger = app.logger
DB_NAME = "Diary"


def create_diary(user_id, uuid, title, content, time_created=None):
    """
    Raise: errors.DbCreateError
    """
    if user_id is None or uuid is None or title is None or content is None:
        raise errors.ArgumentShouldNotBeNull()

    _user = db_user.get_user(user_id)
    if _user is None:
        raise errors.InvalidUserIdDuringCreatingDiary()

    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    if time_created is None:
        time_created = int(time.time())
    new_diary_id = -1
    try:
        with conn.cursor() as cursor:
            sql = "insert into `Diary` (`user_id`, `uuid`, `title`, `content`, `time_created`, `time_modified`) " \
                  "values (%s, %s, %s, %s, %s, %s)"
            cursor.execute(sql, (str(user_id), str(uuid), str(title), str(content), str(time_created), str(time_created)))
            new_diary_id = cursor.lastrowid
        conn.commit()
    except Exception as e:
        logger.error(e)
        raise errors.DbCreateError()
    finally:
        conn.close()
        return new_diary_id


def get_diary_by_id(diary_id):
    """
    Raise: errors.DbReadError
    """
    diary = None
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from Diary where id = %s and time_removed = %s"
            cursor.execute(sql, (str(diary_id), str(0)))
            result = cursor.fetchall()
            diary = result[0] if len(result) > 0 else None
    except Exception as e:
        logger.error(e)
        raise errors.DbReadError()
    finally:
        conn.close()
        return diary


def get_diary_by_uuid(uuid, user_id):
    """
    Raise: errors.DbReadError
    """
    print 'get_diary_by_uuid uuid ', uuid, ', user_id: ', user_id
    diary = None
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from Diary where uuid = %s and user_id = %s and time_removed = %s"
            cursor.execute(sql, (str(uuid), str(user_id), str(0)))
            result = cursor.fetchall()
            diary = result[0] if len(result) > 0 else None
            print 'diary: ', diary
    except Exception as e:
        logger.error(e)
        raise errors.DbReadError()
    finally:
        conn.close()
        return diary


def get_diary_list_since_last_sync(user_id, last_sync_time):
    """
    Raise: errors.DbReadError
    """
    print 'get_diary_list_since_last_sync for user_id: ', user_id, ', last_sync_time: ', last_sync_time
    diary_list = []
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    try:
        with conn.cursor() as cursor:
            sql = "select * from Diary where user_id = %s and time_modified > %s"
            cursor.execute(sql, (str(user_id), str(last_sync_time)))
            diary_list = cursor.fetchall()
            print diary_list
    except Exception as e:
        logger.error(e)
        raise errors.DbReadError()
    finally:
        conn.close()
        return diary_list


def update_diary(user_id, uuid, title, content, time_modified=None):
    """
    Raise: errors.DbUpdateError
    """
    if time_modified is None:
        time_modified = int(time.time())
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    print 'prepare update_diary user_id: ', user_id, ', uuid: ', uuid, 'title: ', title, ', content: ', content
    try:
        with conn.cursor() as cursor:
            sql = "update Diary set title = %s, content = %s, time_modified = %s where uuid = %s and user_id = %s"
            print 'update!'
            result = cursor.execute(sql, (str(title), str(content), str(time_modified), str(uuid), str(user_id)))
        print 'update_diary result:', result
        conn.commit()
    except Exception as e:
        logger.error(e)
        raise errors.DbUpdateError()
    finally:
        conn.close()
        return


def delete_diary(user_id, uuid, time_removed=None):
    """
    Raise: errors.DbDeleteError
    """
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    if time_removed is None:
        time_removed = int(time.time())
    try:
        with conn.cursor() as cursor:
            sql = "update `Diary` set time_removed = %s, time_modified = %s where `uuid` = %s and `user_id` = %s"
            result = cursor.execute(sql, (str(time_removed), str(time_removed), str(uuid), str(user_id)))
            print result
        conn.commit()
    except Exception as e:
        logger.error(e)
        raise errors.DbDeleteError()
    finally:
        conn.close()


def upsert_diary(user_id, uuid, data):
    """If user_id+uuid already exists, update with data. disallow field in data:
            'id', 'user_id', 'uuid', 'time_created', 'time_modified', 'time_removed';
       If not exists, we can create a new diary.
    """
    if not user_id or not uuid:
        return None
    disallow_fields = ['id', 'user_id', 'uuid', 'time_created', 'time_modified', 'time_removed']
    update = data.copy()
    for field in disallow_fields:
        update.pop(field, None)

    diary = get_diary_by_uuid(uuid, user_id)
    if diary:
        update_diary(user_id, uuid, data.get('title').encode('utf8'), data.get('content').encode('utf8'),
                     data.get('time'))
    else:
        create_diary(user_id, uuid, data.get('title').encode('utf8'), data.get('content').encode('utf8'),
                     data.get('time'))


def _check_access(user_id, uuid):
    diary = get_diary_by_uuid(uuid, user_id)
    if diary is None:
        raise errors.DiaryEmpty()
    if diary['user_id'] is not user_id:
        raise errors.NotAccessForThisDiary()
    return True
