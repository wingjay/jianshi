import server.db as base_db


def reinit_table(table):
    conn = base_db.get_conn()
    try:
        with conn.cursor() as cursor:
            sql = "drop table if exists %s;" % table
            cursor.execute(sql)
    finally:
        conn.close()
    if 'Diary' == table:
        base_db.init_diary_table()
    elif 'User' == table:
        base_db.init_user_table()
