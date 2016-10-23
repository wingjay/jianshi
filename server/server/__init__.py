from flask import Flask

app = Flask(__name__)

# MUST under app = Flask(__name__)
# If you want to use 'from server import app' in your py,
# you must add one line as following
import server.www.user
import server.www.diary
import server.www.sync
import server.db.diary


@app.route("/")
def hello():
    return "Hello, Here is Jianshi Server! Welcome!"


@app.route("/get")
def get():
    return "get function works Jianshiasasdf"






