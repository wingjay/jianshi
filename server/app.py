# coding: utf-8

from datetime import datetime

from flask import Flask, jsonify, request
from flask import render_template
from flask_sockets import Sockets

app = Flask(__name__)
sockets = Sockets(app)

@app.route('/')
def index():
    return render_template('index.html')


@app.route('/get_json')
def get_json():
    json_data = jsonify(v=2, data='jianshi server')
    return json_data


@app.route('/diary', methods=['POST'])
def create_diary():
	diary = 'test'
	args='args'
	form='form'
	values='values'
	client_data='client_data'
	if request.args:
		args = request.args.to_dict() # parameters in the URL
	if request.form:
		form=request.form.to_dict() # information in the body (as sent by a html POST form)
	if request.data:
		client_data=json.loads(request.data)
	if request.values:
		values=request.values.to_dict()
	json_data = jsonify(diary=diary, args=args, form=form, data=client_data, values=values)
	return json_data