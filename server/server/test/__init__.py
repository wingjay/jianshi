from server.db import init as base_db

tables_name = {'Diary', 'User', 'EventLog'}


def reinit_table():
    conn = base_db.get_conn()
    print 'reinit start droping tables'
    try:
        with conn.cursor() as cursor:
            sql = ""
            for table_name in tables_name:
                sql += 'drop table if exists %s;' % table_name
            cursor.execute(sql)
    finally:
        conn.close()

    print 'reinit start init all schema files'
    base_db.init_all_schema()

