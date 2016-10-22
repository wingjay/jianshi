import time
import random
import uuid

import pymysql

import server.db as base_db
import server.db.diary as db_diary


def reinit_table():
    conn = base_db.get_conn()
    try:
        with conn.cursor() as cursor:
            sql = "drop table if exists Diary;"
            cursor.execute(sql)
    finally:
        conn.close()
    base_db.init_diary_table()


def test_create():
    reinit_table()
    for user_id in [1, 3, 5]:
        title = _get_title()
        content = _get_content()
        print 'create diary for user_id %s' % user_id
        db_diary.create_diary(user_id, _get_uuid(), title, content)

    for user_id in [1, 3, 5]:
        print 'user_id = %s' % user_id
        result = db_diary.get_diary_list_since_last_sync(user_id, 0)
        assert len(result) == 1


def test_update():
    reinit_table()
    user_id = 1
    _uuid = _get_uuid()
    print 'uuid generated: ', _uuid
    db_diary.create_diary(user_id, _uuid, _get_title(), _get_content())

    result = db_diary.get_diary_by_uuid(_uuid, user_id)
    print result['uuid']
    print _uuid
    assert result['uuid'] == _uuid

    db_diary.update_diary(user_id, _uuid, 'updated title', 'updated content')
    result = db_diary.get_diary_by_uuid(_uuid, user_id)
    print result
    assert result['user_id'] == user_id
    assert result['uuid'] == _uuid
    assert result['title'] == 'updated title'
    assert result['content'] == 'updated content'


def _get_uuid():
    return str(uuid.uuid1())


def _get_title():
    return 'title %s' % int(time.time())


def _get_content():
    return 'content %s' % int(time.time())
