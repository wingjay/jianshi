# User releated error
class AuthTokenInvalid(Exception):
	rc = 1000
	msg = "User auth token is invalid"

class UserNotFound(Exception):
    rc = 1001
    msg = "User not found"