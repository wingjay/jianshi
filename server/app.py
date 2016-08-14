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

