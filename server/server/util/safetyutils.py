from werkzeug.security import generate_password_hash, check_password_hash

SECURE_HASH_METHOD = 'pbkdf2:sha1:1111'

def get_hash_password(real_password):
	hashed = generate_password_hash(real_password, SECURE_HASH_METHOD)
	return hashed.split('$', 1)[1]


def verify_hash_password(hashed_pwd, to_be_verify_pwd):
	return check_password_hash('{}${}'.format(SECURE_HASH_METHOD, hashed_pwd), to_be_verify_pwd)	