# web api
from flask import render_template, request, jsonify

from server import app

@app.route('/web/index')
def web_index():
    return render_template("before.html")


# @app.rout('/get_diary')
# def get_diary():
#     get_parameters = request.args.to_dict()
#     user_id = get_parameters['userid']
    # diary = get_diary_from_db(user_id)
    # return diary


@app.route('/web/index2')
def web_index2():
    get_parameters = request.args.to_dict()
    email = get_parameters['email']
    new_password = get_parameters['new_password']
    return render_template("after.html", user={'name': 'yinxuan'},email=email ,new_password=new_password)


@app.route('/web/change/url2')
def web_change_url2():
    get_parameters = request.args.to_dict()
    num1 = get_parameters['num1']
    num2 = get_parameters['num2']
    return render_template("before.html",num1=num1,num2=num2)