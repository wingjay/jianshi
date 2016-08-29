import functools

from flask import request, jsonify

from server import app
from server.logic import user as logic_user
from server.data import errors

def mobile_request(func):
    @functools.wraps(func)
    def wrapped(*args, **kwargs):
    	# put all parameters into kwargs
    	kwargs = kwargs if kwargs else {'testkey': 'testvalue'}
    	if request.args: # get param
    		kwargs.update(request.args.to_dict())
    	if request.form: # post param
    		kwargs.update(request.form.to_dict())
    	#todo: request.files & request.data
    	if request.headers.get('Authorization'):
    		encrypted_token = request.headers.get('Authorization')
    		isValid, user_id = logic_user.is_token_valid(encrypted_token)
    		if not isValid:
    			raise errors.AuthTokenInvalid()
    		user = logic_user.get_user_by_id(user_id)
    		if not user:
    			# token is valid, but maybe user is deleted
    			raise errors.UserNotFound()

        print 'start'
        kwargs['user_id'] = 20
        print kwargs
    	return func(**kwargs)
    return wrapped	


@app.route("/www/index")
@mobile_request
def hello222(user_id, **kwargs):
    return "This is www layer for user"


@app.route("/user/signup", methods=['POST'])
def signup(user_id, **kwargs):
	data = request.form.to_dict()
	if 'name' not in data or 'password' not in data:
		return jsonify(rc=1, msg='Both name & password should not be null')
	result = logic_user.signup(data['name'], data['password'])
	return jsonify(result)


@app.route("/user/login", methods=['POST'])
def login(user_id, **kwargs):
	data = request.form.to_dict()
	return db_user.login(data['email'], data['password'])	


