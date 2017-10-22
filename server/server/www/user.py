#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random, time

from flask import render_template

from server import app
from server.www.base import mobile_request, must_login
from server.logic import user as logic_user
import server.data.images
import server.data.poems
import server.data.android_version
import server.data.share
from server.data import errors
from server.util import mathutil


@app.route("/index")
def index():
    return render_template("./web/templates/index.html")


@app.route("/ping")
def ping():
    return "pong"


@app.route("/www/index")
@mobile_request
def wwwhello(**kwargs):
    return "This is www layer for user"


@app.route("/app/download_link", methods=['GET'])
@mobile_request
def app_download_link(**kwargs):
    return server.data.android_version.newest_version['link']


@app.route("/test/token", methods=['GET', 'POST'])
@mobile_request
def test_token(user_id, **kwargs):
    return user_id


@app.route("/user/signup", methods=['POST'])
@mobile_request
def signup(email, password, **kwargs):
    return logic_user.signup(email, password)


@app.route("/user/login", methods=['POST'])
@mobile_request
def login(email, password, **kwargs):
    result, user = logic_user.login(email, password)
    if result:
        return user
    else:
        raise errors.UserLoginFailure()


@app.route("/home/image_poem", methods=['GET'])
@mobile_request
@must_login
def get_home_poem(width=0, height=0, **kwargs):
    if width == 0 or height == 0:
        width = 900
        height = 1600
    poem_index = random.randint(0, len(server.data.poems.poems) - 1)
    unsplash_image_url = server.data.images.get_unsplash_url(width, height)

    next_fetch_time = int(time.time()) + app.config['HOME_IMAGE_POEM_FETCH_TIME_GAP']
    return {
        'image': unsplash_image_url,
        'poem': server.data.poems.poems[poem_index],
        'next_fetch_time': next_fetch_time
    }


@app.route("/user/upgrade", methods=['GET'])
@mobile_request
def check_upgrade(version_name, **kwargs):
    newest_version = server.data.android_version.newest_version
    if mathutil.version_gt(newest_version['version_name'], version_name):
        return newest_version
    return None


@app.route("/app/share", methods=['GET'])
@mobile_request
def get_share_text(**kwargs):
    link = server.data.android_version.newest_version['link']
    share_text = server.data.share.share_text + link
    return {
        'link': link,
        'share_text': share_text
    }


@app.route("/pay/developer", methods=['GET'])
@mobile_request
def pay_developer(**kwargs):
    return {
        'title': '打赏开发者',
        'message': '为了把简诗的美好带给大家,程序猿小哥花费了很多个人时间和精力进行开发,'
                   '甚至自己掏钱购买服务器来为用户云备份日记,看程序猿小哥这么可怜,快来打赏杯咖啡吧,这样小哥更有动力去为你带来更多美好哦.(打赏时附上账号以后有惊喜哦)',
        'ali_pay_account': '18916376110',
        'wechat_pay_account': 'iam_wingjay',
        'time_gap_seconds': 3 * 86400
    }
