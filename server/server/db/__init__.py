from flask import Flask, g

import pymysql
import pymysql.cursors

from server import app


def init_db():
    """Initializes the database."""
    try:
        with get_conn().cursor() as cursor:
            # execute schema sql file
            with app.open_resource('db/schema/0001/user.sql', mode='r') as f:
                sql = f.read()
                print sql
                result = cursor.execute(sql)
                print result
    finally:
        print get_conn().close()


def _conn(cursorclass=pymysql.cursors.Cursor):
    return pymysql.connect(host='192.168.33.10',
                           user='emma', password='emma',
                           db='jianshi', charset='utf8mb4',
                           cursorclass=cursorclass)


def get_conn(cursorclass=pymysql.cursors.Cursor):
    with app.app_context():
        if not hasattr(g, 'db_conn'):
            g.db_conn = _conn(cursorclass)
        return g.db_conn