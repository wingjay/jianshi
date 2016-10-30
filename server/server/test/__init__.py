import server.db as base_db

tables_name = {'Diary', 'User'}


def reinit_table():
    conn = base_db.get_conn()
    try:
        with conn.cursor() as cursor:
            sql = ""
            for table_name in tables_name:
                sql += 'drop table if exists %s;' % table_name
            cursor.execute(sql)
    finally:
        conn.close()
    if 'Diary' in tables_name:
        base_db.init_diary_table()
    if 'User' in tables_name:
        base_db.init_user_table()
