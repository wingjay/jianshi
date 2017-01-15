from server.db import diary as db_diary
from server.logic import user as logic_user
from server.data import errors


def create_diary(user_id, uuid, title, content):
    """Create new diary with title & content for user_id.

    Return:
        new_diary_id
    """
    user = logic_user.get_user_by_id(user_id)
    if user is None:
        raise errors.UserNotFound()
    return db_diary.create_diary(user_id, uuid, title, content)


def get_diary_by_id(user_id, diary_id):
    diary = db_diary.get_diary_by_id(diary_id)
    if diary['user_id'] is user_id:
        return diary
    raise errors.NoAccessForOthersDiary()


def get_diary_by_uuid(user_id, uuid):
    diary = db_diary.get_diary_by_uuid(uuid)
    if diary['user_id'] is user_id:
        return diary
    raise errors.NoAccessForOthersDiary()


def update_diary(user_id, uuid, title, content):
    """Update a existed diary.
    """
    db_diary.update_diary(user_id, uuid, title, content)


def delete_diary(user_id, uuid):
    db_diary.delete_diary(user_id, uuid)


def upsert_diary(user_id, uuid, data):
    db_diary.upsert_diary(user_id, uuid, data)