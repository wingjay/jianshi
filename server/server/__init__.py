import os
import logging
from logging.handlers import RotatingFileHandler

from flask import Flask

app = Flask(__name__, instance_relative_config=True)

# load config
app.config.from_object("conf.all")
app.config.from_pyfile('config.py')
if app.config['DEBUG']:
    app.config.from_object("conf.dev")
else:
    app.config.from_object("conf.prod")

# MUST under app = Flask(__name__)
# If you want to use 'from server import app' in your py,
# you must add one line as following
import server.www.user
import server.www.diary
import server.www.sync
import server.db

current_dir = os.path.dirname(os.path.abspath(__file__))
path = current_dir + '/logs/jianshi.log'
if not os.path.exists(path):
    os.popen('cd ' + current_dir +' ; mkdir logs' + '; cd ' + current_dir + '/logs/' + ' ; touch jianshi.log')
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
