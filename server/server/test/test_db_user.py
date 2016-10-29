import server.db as base_db
import server.test as base_test
from server.logic import user as logic_user

USER_DB_NAME = 'User'


def test_signup_login():
    base_test.reinit_table(USER_DB_NAME)
    email = 'email_1@qq.com'
    password = 'password_1'
    user = logic_user.signup(email, password)
    assert user is not None
    user_id = user['id']
    user_from_db = logic_user.get_user_by_id(user_id)
    assert user_from_db is not None
    assert user_from_db['email'] == user['email']
    login_result, login_user = logic_user.login(email, password)
    assert login_result
    assert login_user is not None
    assert login_user['email'] == user['email']


def test_token():
    base_test.reinit_table(USER_DB_NAME)
    email = 'email_1@qq.com'
    password = 'password_1'
    user = logic_user.signup(email, password)
    assert user is not None
    login_result, login_user = logic_user.login(email, password)
    assert login_result
    assert login_user is not None
    auth_token = login_user['encrypted_token']
    is_valid, user_id_from_token = logic_user.is_token_valid(auth_token)
    assert is_valid
    assert user_id_from_token == login_user['id']
