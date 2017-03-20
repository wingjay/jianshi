#!/usr/bin/env python
# -*- coding: utf-8 -*-

# web api
import redis
from flask import render_template
from flask import request, jsonify, url_for

from server import app
from server.logic import user as logic_user

redis_client = redis.StrictRedis(host='localhost', port=6379, db=0)


@app.route('/web/index')
def web_index():
    return render_template("index.html", user={'name': 'jay', 'age': 20}, number=10)


@app.route('/test/forget_password/<string:email>')
def test_redis(email, *args, **kwargs):
    link = url_for('app_download_link', email='myemail', key='urlforvalue', _external=True)
    print link
    print 'jay' + app.config['SERVER_NAME']
    redis_client.set('key', 'value2')
    # get_params = redis_client.get('key')
    return jsonify({})


@app.route('/web_page/update_password/<string:email>')
def update_password_page(email, verification_code):
    return render_template("index.html", email=email, verification_code=verification_code)


@app.route('/web_action/update_password/<string:email>', methods=['POST'])
def update_password_with_verification_code(email, verification_code, new_password):
    real_code = redis_client.get(email)
    if real_code and real_code is verification_code:
        logic_user.update_password(email, new_password)
        # redirect to success page
    else:
        # redirect to resend email page
        pass
