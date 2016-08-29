import time
import base64
import json
import struct

from Crypto.Cipher import AES
from werkzeug.security import generate_password_hash, check_password_hash

SECURE_HASH_METHOD = 'pbkdf2:sha1:1111'
default_key = 'XjYpwIiYLbaOsU69HXUjlGRMCut88zQG'
AUTH_TOKEN_ENCRYPT_KEY = '8G7Zg3kjhsdv23bjdalj82nh'

def get_hash_password(real_password):
	hashed = generate_password_hash(real_password, SECURE_HASH_METHOD)
	return hashed.split('$', 1)[1]


def verify_hash_password(hashed_pwd, to_be_verify_pwd):
	return check_password_hash('{}${}'.format(SECURE_HASH_METHOD, hashed_pwd), to_be_verify_pwd)	


def encrypt_auth_token(user_id):
	return encrypt_obj((user_id, time.time()), AUTH_TOKEN_ENCRYPT_KEY)


def decrypt_auth_token_for_user_id(encrypted_token):
	return decrypt_auth_token(encrypted_token)[0]


def decrypt_auth_token(encrypted_token):
    """Decrypt authToken and return (user_id, create_time)
    """
    return decrypt_obj(encrypted_token, AUTH_TOKEN_ENCRYPT_KEY)


def encrypt_obj(obj, key=default_key):
    enc = AES.new(key, AES.MODE_ECB)
    text = json.dumps(obj)
    packed = struct.pack('I', len(text)) + text
    padded = padding_16(packed)
    return base64.urlsafe_b64encode(enc.encrypt(padded))


def decrypt_obj(text, key=default_key):
    dec = AES.new(key, AES.MODE_ECB)
    text = dec.decrypt(base64.urlsafe_b64decode(str(text)))
    size, data = text[:4], text[4:]
    size = struct.unpack('I', size)[0]
    data = data[:size]
    return json.loads(data)	


def padding_16(text):
    padding = len(text) % 16
    if padding:
        return text + ' ' * (16 - padding)
    else:
        return text