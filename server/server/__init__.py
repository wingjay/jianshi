from flask import Flask, request, jsonify

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
    return "get function works Jianshiasasdf"


@app.route("/user/signup", methods=['POST'])
def signup():
	data = request.form.to_dict()
	return jsonify(data)
	# return db_user.create_user(data['email'], data['password'])


@app.route("/user/login", methods=['POST'])
def login():
	data = request.form.to_dict()
	return db_user.login(data['email'], data['password'])	

def get_user():
	return db_user.get_user()	

def get_user_by_id(id):
    return "get user from db by id" + id





