from flask import g
import pymysql
import pymysql.cursors

from server import app
conf = app.config


def _init_db(sql_file):
    """Initializes the database."""
    try:
        with get_conn().cursor() as cursor:
            # execute schema sql file
            with app.open_resource(sql_file, mode='r') as f:
                sql = f.read()
                print sql
                result = cursor.execute(sql)
                print result
    finally:
        print get_conn().close()


def init_user_table():
    _init_db('db/schema/0001/user.sql')


def init_diary_table():
    _init_db('db/schema/0001/diary.sql')


def init_event_log_table():
    _init_db('db/schema/0001/event_log.sql')


def _conn(cursorclass=pymysql.cursors.Cursor):
    return pymysql.connect(host=conf['MYSQL_LOCAL_HOST'],
                           user=conf['MYSQL_USER'],
                           password=conf['MYSQL_PASSWORD'],
                           db=conf['MYSQL_DB_NAME'],
                           charset='utf8mb4',
                           cursorclass=cursorclass)


def get_conn(cursorclass=pymysql.cursors.Cursor):
    with app.app_context():
        if not hasattr(g, 'db_conn'):
            g.db_conn = _conn(cursorclass)
        return g.db_conn
