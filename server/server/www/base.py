import functools

from flask import request, jsonify

from server.data import errors
from server.logic import user as logic_user


def mobile_request(func):
    @functools.wraps(func)
    def wrapped(*args, **kwargs):
        # put all parameters into kwargs
        kwargs = kwargs if kwargs else {'testkey': 'testvalue'}
        if request.args:  # get param
            kwargs.update(request.args.to_dict())
        if request.form:  # post param
            kwargs.update(request.form.to_dict())
        # todo: request.files & request.data
        if request.headers.get('Authorization'):
            encrypted_token = request.headers.get('Authorization')
            is_valid, user_id = logic_user.is_token_valid(encrypted_token)
            if not is_valid:
                raise errors.AuthTokenInvalid()
            user = logic_user.get_user_by_id(user_id)
            if not user:
                # token is valid, but maybe user is deleted
                raise errors.UserNotFound()
        try:
            result = func(**kwargs)
            response = {
                'rc': 0,
                'data': result
            }

        except Exception as e:
            rc = getattr(e, 'rc', errors.UnknownError.rc)
            err_msg = getattr(e, 'msg', errors.UnknownError.msg)
            response = {
                'rc': rc,
                'msg': err_msg,
            }

        return jsonify(response)

    return wrapped


def must_login(func):
    @functools.wraps(func)
    def wrapped(*args, **kwargs):
        if 'user_id' not in kwargs:
            raise errors.CanNotFindUserId()
        return func(**kwargs)

    return wrapped
