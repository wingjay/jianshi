from flask import request, jsonify

from server import app
from server.logic import user as logic_user


@app.route("/www/index")
def hello222():
    return "This is www layer for user"


@app.route("/user/signup", methods=['POST'])
def signup():
	data = request.form.to_dict()
	if 'name' not in data or 'password' not in data:
		return jsonify(rc=1, msg='Both name & password should not be null')
	result = logic_user.signup(data['name'], data['password'])
	return jsonify(result)


@app.route("/user/login", methods=['POST'])
def login():
	data = request.form.to_dict()
	return db_user.login(data['email'], data['password'])	