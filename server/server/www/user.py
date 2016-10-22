from server import app
from server.www.base import mobile_request
from server.logic import user as logic_user
from server.data import errors


@app.route("/index")
def index(**kwargs):
    return "index"


@app.route("/www/index")
@mobile_request
def wwwhello(**kwargs):
    return "This is www layer for user"


@app.route("/test/token", methods=['GET', 'POST'])
@mobile_request
def test_token(user_id, **kwargs):
    return user_id


@app.route("/user/signup", methods=['POST'])
@mobile_request
def signup(**kwargs):
    if 'name' not in kwargs or 'password' not in kwargs:
        return {
            'rc': 1,
            'msg': 'Both name & password should not be null'
        }
    result = logic_user.signup(kwargs['name'], kwargs['password'])
    return result


@app.route("/user/login", methods=['POST'])
@mobile_request
def login(**kwargs):
    result, user = logic_user.login(kwargs['name'], kwargs['password'])
    if result:
        return user
    else:
        raise errors.UserLoginFailure()


