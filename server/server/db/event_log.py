#!/usr/bin/python
# coding: utf-8

import pymysql
import pymysql.cursors

import server.db.init as base_db
from server.data import errors
from server import app

logger = app.logger


def add_event_log(user_id, log_item):
    """
    Record the event log from client
    """
    conn = base_db.get_conn(pymysql.cursors.DictCursor)
    new_event_id = -1
    try:
        with conn.cursor() as cursor:
            sql = 'insert into `EventLog` (`user_id`,`event_name`,`page_source`,`time_created`) values (%s,%s,%s,%s)'
            cursor.execute(sql, (str(user_id), str(log_item['event_name']), str(log_item['page_source']), str(log_item['time_created'])))
            new_event_id = cursor.lastrowid
            conn.commit()
    except Exception as e:
        logger.error(e)
        raise errors.DbCreateError()
    finally:
        conn.close()
        return new_event_id
