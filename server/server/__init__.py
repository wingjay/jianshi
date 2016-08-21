import time

from flask import Flask

app = Flask(__name__)

## MUST under app = Flask(__name__)
## If you want to add 'from server import app' in your py, 
## you must add one line as following
import server.db.user as db_user

@app.route("/")
def hello():
    return "Hello, I love Digital Ocean! Jianshi"


@app.route("/get")
def get():
    return "get function works Jianshi"

@app.route("/user", methods=['GET'])
def create_user(email, password):
	return db_user.create_user(email, password)


def get_user():
	return db_user.get_user()	

def get_user_by_id(id):
    return "get user from db by id" + id



