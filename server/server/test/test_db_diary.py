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


def test_delete():
    reinit_table()
    uuid1 = _get_uuid()
    uuid2 = _get_uuid()
    uuid3 = _get_uuid()
    diary_id_1 = db_diary.create_diary(1, uuid1, _get_title(), _get_content())
    diary_id_2 = db_diary.create_diary(3, uuid2, _get_title(), _get_content())
    diary_id_3 = db_diary.create_diary(5, uuid3, _get_title(), _get_content())

    assert db_diary.get_diary_by_id(diary_id_1) is not None
    assert db_diary.get_diary_by_uuid(uuid1, 1) is not None
    assert db_diary.get_diary_by_id(diary_id_2) is not None
    assert db_diary.get_diary_by_uuid(uuid2, 3) is not None
    assert db_diary.get_diary_by_id(diary_id_3) is not None
    assert db_diary.get_diary_by_uuid(uuid3, 5) is not None

    db_diary.delete_diary(1, uuid1)
    assert db_diary.get_diary_by_id(diary_id_1) is None
    assert db_diary.get_diary_by_uuid(uuid1, 1) is None
    db_diary.delete_diary(3, uuid2)
    assert db_diary.get_diary_by_id(diary_id_1) is None
    assert db_diary.get_diary_by_uuid(uuid2, 3) is None
    db_diary.delete_diary(5, uuid3)
    assert db_diary.get_diary_by_id(diary_id_3) is None
    assert db_diary.get_diary_by_uuid(uuid3, 5) is None


def test_push():
    reinit_table()
    # create a diary
    data = {
        'title': 'first diary',
        'content': 'first diary',
        'time': 11111111,
    }
    _uuid = _get_uuid()
    user_id = 1

    assert db_diary.get_diary_by_uuid(_uuid, user_id) is None
    db_diary.upsert_diary(user_id, _uuid, data)
    diary = db_diary.get_diary_by_uuid(_uuid, user_id)
    assert diary is not None
    assert diary.get('time_created') == 11111111
    assert diary.get('time_modified') == 11111111

    # change this diary
    data = {
        'title': 'changed diary',
        'content': 'changed diary',
        'time': 2222222,
    }
    db_diary.upsert_diary(user_id, _uuid, data)
    diary = db_diary.get_diary_by_uuid(_uuid, user_id)
    assert diary is not None
    assert diary.get('title') == 'changed diary'
    assert diary.get('content') == 'changed diary'
    assert diary.get('time_created') == 11111111
    assert diary.get('time_modified') == 2222222

    # delete this diary
    time_delete = 333333333
    db_diary.delete_diary(user_id, _uuid, time_delete)
    diary = db_diary.get_diary_by_uuid(_uuid, user_id)
    assert diary is None


def test_pull():
    reinit_table()
    user_id = 1
    # set last_sync_time
    current_time = int(time.time())
    last_sync_time = current_time - 1000
    # generate new data[create, update, delete] since last_sync_time
    uuid1 = _get_uuid()
    db_diary.create_diary(user_id, uuid1, _get_title(), _get_content(), current_time)

    uuid2 = _get_uuid()
    db_diary.create_diary(user_id, uuid2, _get_title(), _get_content(), current_time)
    db_diary.update_diary(user_id, uuid2, 'updated', 'updated', current_time + 100)

    uuid3 = _get_uuid()
    db_diary.create_diary(user_id, uuid3, _get_title(), _get_content(), current_time)
    db_diary.delete_diary(user_id, uuid3, current_time + 150)
    # assert uuid1 created, uuid2 created, uuid3 unknown
    changed_diary_list = db_diary.get_diary_list_since_last_sync(user_id, last_sync_time)
    assert len(changed_diary_list) == 3



def _get_uuid():
    return str(uuid.uuid1())


def _get_title():
    return 'title %s' % int(time.time())


def _get_content():
    return 'content %s' % int(time.time())
