import time

import server.db.user as db_user
from server.util import safetyutils
from conf import all as conf
from server.data import errors


def signup(email, password):
    """Signup with email and password. After creating this new user,
    we'll create a authToken by user_id and current timestamp,
    and return this authToken back to user.
    In the future, each request from user must contains authToken for us to fetch his user_id.

    Return:
    User Object + authToken.
    """
    new_user_id = -1
    final_create_success = False
    try:
        new_user_id = db_user.create_user(email, password)
        if new_user_id == -1:
            raise errors.DBError()
        user = db_user.get_user(new_user_id)
        user['encrypted_token'] = safetyutils.encrypt_auth_token(user['id'])
        final_create_success = True
        return user
    finally:
        if new_user_id != -1 and final_create_success is False:
            db_user.delete_user(new_user_id)
            raise errors.UserCreateFailure()


def login(email, password):
    """1. check real password with encrypted password in db
       2. if pass, get user info for him and create a auth_token and return back.

    Return:
        {
            'result': True/False,
            'data': user base info + auth token
        }
    """
    user = db_user.login(email, password)
    if user is not None and 'id' in user:
        auth_token = create_auth_token(user['id'])
        user['encrypted_token'] = auth_token
        return True, user
    return False, None


def create_auth_token(user_id):
    if user_id is None:
        return None
    return safetyutils.encrypt_auth_token(user_id)


def get_user_by_id(user_id):
    return db_user.get_user(user_id)


def is_token_valid(encrypted_token):
    user_id = 0
    try:
        obj = safetyutils.decrypt_auth_token(encrypted_token)
        user_id = obj[0]
    except Exception as e:
        print e
        return False, user_id
    # if user is None or expire
    if db_user.get_user(user_id) is None or (time.time() - obj[1]) > conf.AUTH_TOKEN_EXPIRE_TIME:
        return False, user_id
    return True, user_id


def delete_user(user_id):
    db_user.delete_user(user_id)
