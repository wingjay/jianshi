# web api
from flask import render_template

from server import app


@app.route('/web/index')
def web_index():
    return render_template("index.html", user={'name': 'jay', 'age': 20}, number=10)
