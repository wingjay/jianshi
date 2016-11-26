import os
import logging
from logging.handlers import RotatingFileHandler

from flask import Flask

app = Flask(__name__, instance_relative_config=True)

# create empty instance directory
current_dir = os.path.dirname(os.path.abspath(__file__))
print current_dir
path = current_dir + '/../instance/config.py'
if not os.path.exists(path):
    print 'not exists'
    os.popen('cd ' + current_dir + '/..; mkdir instance' + '; cd ' + current_dir
             + '/../instance/' + ' ; touch config.py; touch __init__.py')

# load config
app.config.from_object("conf.all")
app.config.from_pyfile('config.py')
if app.config['DEBUG']:
    app.config.from_object("conf.dev")
else:
    app.config.from_object("conf.prod")

# send error email
if not app.config['DEBUG']:
    import logging
    from logging.handlers import SMTPHandler
    mail_handler = SMTPHandler("smtp.126.com", app.config['EMAIL_ADDRESS'], app.config['ADMIN_EMAILS'],
                               'JianShi server error!', (app.config['EMAIL_ADDRESS'], app.config['EMAIL_PASSWORD']))
    mail_handler.setLevel(logging.ERROR)
    app.logger.addHandler(mail_handler)


# MUST under app = Flask(__name__)
# If you want to use 'from server import app' in your py,
# you must add one line as following
import server.www
import server.www.user
import server.www.diary
import server.www.sync
import server.db
import server.test

current_dir = os.path.dirname(os.path.abspath(__file__))
path = current_dir + '/../logs/jianshi.log'
if not os.path.exists(path):
    os.popen('cd ' + current_dir +'/.. ; mkdir logs' + '; cd ' + current_dir + '/../logs/' + ' ; touch jianshi.log')
formatter = logging.Formatter("[%(asctime)s] {%(pathname)s:%(lineno)d} %(levelname)s - %(message)s")
handler = RotatingFileHandler(current_dir + '/../logs/jianshi.log', maxBytes=10000, backupCount=0)
handler.setLevel(logging.INFO)
handler.setFormatter(formatter)
app.logger.addHandler(handler)
app.logger.setLevel(logging.INFO)


logger = app.logger


@app.route("/")
def hello():
    return "Hello, Here is Jianshi Server! Welcome!"


@app.route("/logging_test")
def get():
    logger.info('isDebug? %s', app.config['DEBUG'])
    logger.warning('Test logging  (%d apples)', 42)
    logger.error('[Ignored, it\'s testing email]An error occurred')
    logger.info('Info')
    return "get function works Jianshiasasdf"
