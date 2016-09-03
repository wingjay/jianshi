# User releated error
class UnknownError(Exception):
    rc = 1
    msg = 'An error has occurred when processing the request.'


class DBError(Exception):
    rc = 2
    msg = 'An error has occurred at DB layer'


class AccessForbiddenError(Exception):
    rc = 403
    msg = 'Forbidden'

# User releated


class AuthTokenInvalid(Exception):
    rc = 1000
    msg = "User auth token is invalid"


class UserCreateFailure(Exception):
    rc = 1001
    msg = "User create failure"


class UserLoginFailure(Exception):
    rc = 1002
    msg = "User login failure"


class UserNotFound(Exception):
    rc = 1003
    msg = "User not found"


class WrongPassword(Exception):
    rc = 1004
    msg = "Your original password is not correct"


class UserNameAlreadyUsed(Exception):
    rc = 1005
    msg = "This user name is already used. Please choose a new one if that's not yourself"
