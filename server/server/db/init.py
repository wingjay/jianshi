import os
import os.path

from flask import g
import pymysql
import pymysql.cursors

from server import app
conf = app.config


def _execute_sql_file(sql_file):
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


def init_all_schema():
    current_dir = os.path.dirname(os.path.abspath(__file__))
    path = current_dir + '/schema/'
    for dirpaths, dirs, files in os.walk(path):
        if len(files) > 0:
            for f in files:
                sql_file = dirpaths + '/' + f
                print('sql_file: %s', sql_file)
                _execute_sql_file(sql_file)


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
