from flask import Flask
app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello, I love Digital Ocean! Jianshi"


@app.route("/get")
def get():
    return "get function works Jianshi"

if __name__ == "__main__":
    app.run()
