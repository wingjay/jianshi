import random, time

from server import app
from server.www.base import mobile_request, must_login
from server.logic import user as logic_user
from server.data import errors, images, poems, android_version, share
from server.util import mathutil


@app.route("/index")
def index(**kwargs):
    return "index"


@app.route("/www/index")
@mobile_request
def wwwhello(**kwargs):
    return "This is www layer for user"


@app.route("/app/download_link", methods=['GET'])
@mobile_request
def app_download_link(**kwargs):
    return android_version.newest_version['link']


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
    image_index = random.randint(0, len(images.images) - 1)
    poem_index = random.randint(0, len(poems.poems) - 1)

    unsplash_image_url = images.get_unsplash_url(image_index, width, height)

    next_fetch_time = int(time.time()) + app.config['HOME_IMAGE_POEM_FETCH_TIME_GAP']
    return {
        'image': unsplash_image_url,
        'poem': poems.poems[poem_index],
        'next_fetch_time': next_fetch_time
    }


@app.route("/user/upgrade", methods=['GET'])
@mobile_request
def check_upgrade(version_name, **kwargs):
    newest_version = android_version.newest_version
    if mathutil.version_gt(newest_version['version_name'], version_name):
        return newest_version
    return None


@app.route("/app/share", methods=['GET'])
@mobile_request
def get_share_text(**kwargs):
    link = android_version.newest_version['link']
    share_text = share.share_text + link
    return {
        'link': link,
        'share_text': share_text
    }
