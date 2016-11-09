import time
import base64
import json
import struct

from Crypto.Cipher import AES
from werkzeug.security import generate_password_hash, check_password_hash

from server import app
conf = app.config

SECURE_HASH_METHOD = 'pbkdf2:sha1:1111'
DEFAULT_KEY = conf['DEFAULT_KEY']
AUTH_TOKEN_ENCRYPT_KEY = conf['AUTH_TOKEN_ENCRYPT_KEY']
SYNC_TOKEN_ENCRYPT_KEY = conf['SYNC_TOKEN_ENCRYPT_KEY']


def get_hash_password(real_password):
    hashed = generate_password_hash(real_password, SECURE_HASH_METHOD)
    return hashed.split('$', 1)[1]


def verify_hash_password(hashed_pwd, to_be_verify_pwd):
    return check_password_hash('{}${}'.format(SECURE_HASH_METHOD, hashed_pwd), to_be_verify_pwd)


def encrypt_auth_token(user_id):
    return encrypt_obj((user_id, time.time()), AUTH_TOKEN_ENCRYPT_KEY)


def decrypt_auth_token_for_user_id(encrypted_token):
    try:
        return decrypt_auth_token(encrypted_token)[0]
    except Exception:
        return None


def decrypt_auth_token(encrypted_token):
    """Decrypt authToken and return (user_id, create_time)
    """
    try:
        return decrypt_obj(encrypted_token, AUTH_TOKEN_ENCRYPT_KEY)
    except Exception:
        return None


def encrypt_sync_token(last_sync_time):
    sync_token_info = {
        'last_sync_time': last_sync_time
    }
    return encrypt_obj(sync_token_info, SYNC_TOKEN_ENCRYPT_KEY)


def decrypt_sync_token(sync_token):
    try:
        return decrypt_obj(sync_token, SYNC_TOKEN_ENCRYPT_KEY)
    except Exception:
        return {}


def encrypt_obj(obj, key=DEFAULT_KEY):
    enc = AES.new(key, AES.MODE_ECB)
    text = json.dumps(obj)
    packed = struct.pack('I', len(text)) + text
    padded = padding_16(packed)
    return base64.urlsafe_b64encode(enc.encrypt(padded))


def decrypt_obj(text, key=DEFAULT_KEY):
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
