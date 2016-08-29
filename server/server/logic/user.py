import time

import server.db.user as db_user
from server.util import safetyutils
from server.conf import all as conf

def signup(name, password):
	"""Signup with name and password. After creating this new user, 
	we'll create a authToken by user_id and current timestamp, and 
	return this authToken back to user. In the future, each request 
	from user must contains authToken for us to fetch his user_id.

	Return:
		User Object + authToken.
	"""
	new_user_id = -1
	final_create_success = False
	try:
		new_user_id = db_user.create_user(name, password)
		if new_user_id == -1:
			return {'rc': 1, 'msg': 'db error'}
		user = db_user.get_user(new_user_id)
		current_time = int(time.time())
		user['encrypted_token'] = safetyutils.encrypt_auth_token(user['id'])
		final_create_success = True
		return {'rc':0, 'data': user}	
	finally:
		if new_user_id != -1 and final_create_success == False:
			db_user.delete_user(new_user_id)
			return {'rc':1, 'msg': 'create failure'}


def get_user_by_id(user_id):
	return db_user.get_user(user_id)


def is_token_valid(encrypted_token):
    user_id = 0
    try:
        obj = safetyutils.decrypt_auth_token(encrypted_token)
        user_id = obj[0]
    except Exception, e:
        return False, user_id
    # if user is None or expire
    if db_user.get_user(user_id) is None or (time.time() - obj[1]) > conf.AUTH_TOKEN_EXPIRE_TIME:
    	return False, user_id
    return True, user_id


