import os
import logging
from logging.handlers import RotatingFileHandler

from flask import Flask

app = Flask(__name__)

# MUST under app = Flask(__name__)
# If you want to use 'from server import app' in your py,
# you must add one line as following
import server.www.user
import server.www.diary
import server.www.sync
import server.db.diary

current_dir = os.path.dirname(os.path.abspath(__file__))
formatter = logging.Formatter("[%(asctime)s] {%(pathname)s:%(lineno)d} %(levelname)s - %(message)s")
handler = RotatingFileHandler(current_dir + '/logs/jianshi.log', maxBytes=10000, backupCount=1)
handler.setLevel(logging.DEBUG)
handler.setFormatter(formatter)
app.logger.addHandler(handler)
app.logger.setLevel(logging.DEBUG)


logger = app.logger


@app.route("/")
def hello():
    return "Hello, Here is Jianshi Server! Welcome!"


@app.route("/logging_test")
def get():
    logger.warning('Test logging  (%d apples)', 42)
    logger.error('An error occurred')
    logger.info('Info')
    return "get function works Jianshiasasdf"






