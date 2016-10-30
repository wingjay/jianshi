from server.test import test_db_diary, test_db_user


def execute():
    test_db_diary.execute_all_test()
    test_db_user.execute_all_test()
