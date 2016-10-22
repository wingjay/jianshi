from server import app
from server.www.base import mobile_request, must_login
from server.logic import diary as logic_diary
 

@app.route("/diary", methods=['POST'])
@mobile_request
@must_login
def create_diary(**kwargs):
    user_id = kwargs['user_id']
    title = kwargs['title']
    content = kwargs['content']
    return logic_diary.create_diary(user_id, title, content)


@app.route("/diary/<int:diary_id>", methods=['PUT'])
@mobile_request
@must_login
def update_diary(diary_id, **kwargs):
    return logic_diary.update_diary(kwargs['user_id'],
                                    diary_id,
                                    kwargs['title'],
                                    kwargs['content'])


@app.route("/diary/<int:diary_id>", methods=['GET'])
@mobile_request
@must_login
def get_diary(diary_id, **kwargs):
    return logic_diary.get_diary(kwargs['user_id'], diary_id)


