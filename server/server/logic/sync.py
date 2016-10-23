import time

import server.db.diary as db_diary
from server.logic import user as logic_user
from server.util import safetyutils


def sync_data(user_id, sync_token, sync_items, need_pull):
    """Upsert client data, sync_items, into server db; fetch changed data from server and return to client.
     1. Decrypt sync_token and fetch last_sync_time;
     2. Push: Reverse sync_items, execute upsert/delete action for specified table. Record sync_count for client usage;
     3. If need_pull: Get changed data since last_sync_time, comparing with time_modified(upsert) & time_removed(delete);
     4. Pull: return changed data to user.

     sync_items = [
        {
            'Diary': {
                'create': {
                    'uuid': "2F69DEB5-B631-40DD-A65E-AFE9A0882275",
                    'time': 1477139399,
                    'title': 'this is a new diary',
                    'content': 'today is a good day',
                }
            },
            'Diary': {
                'update': {
                    'uuid': "04B977C7-6F7F-4D36-BFDC-FE98C5241DB0",
                    'time': 1477139399,
                    'title': 'I update this title',
                    'content': 'I'm updated content',
                }
            },
            'Diary': {
                'delete': {
                    'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241DB0"
                    'time': 1477139399,
                }
            }

        }
     ]

     Return:
    {
        'synced_count': 2,
        'sync_token': 'jym0JTE-svI8iDOPp-6e_UMe6dYOVVNSVes8pzZCXDd_I4xn3CYT-oyGVjaCgKgtHO' (based on new last_sync_time),
        'Diary': {
            'delete': [
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241DB0",
                    'time': 1477139340,
                }
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241ABC",
                    'time': 1477139340,
                }
            ],
            'upsert': [
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDC-FE98C5241DB0",
                    'time': 1477139399,
                    'title': 'I'm created by other client',
                    'content': 'I'm created by other client',
                },
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDC-FE98C5241DB0",
                    'time': 1477139399,
                    'title': 'I'm created by other client',
                    'content': 'I'm created by other client',
                }
            ]
        }
     }
    """
    if user_id is None or logic_user.get_user_by_id(user_id) is None:
        return {}

    last_sync_info = safetyutils.decrypt_sync_token(sync_token)
    last_sync_time = last_sync_info.get("last_sync_time", 0)

    synced_count = _push(user_id, sync_items)
    result = {
        'synced_count': synced_count
    }
    if need_pull:
        pull_data = _pull(user_id, last_sync_time)
        result.update(pull_data)

    return result


def _push(user_id, sync_items):
    """Check each sync_items, execute create/update/delete action in database.
    """
    synced_count = 0
    try:
        for item in sync_items:
            for table, action_data in item.iteritems():
                print "table", table, "; action & data", action_data
                if table == db_diary.DB_NAME:
                    _push_diary_data_by_action(user_id, action_data.keys()[0], action_data.values()[0])
            synced_count += 1
    except Exception as e:
        print e

    return synced_count


def _pull(user_id, last_sync_time):
    """1. Extract All Diary new changes since last_sync_time, comparing with time_modified
       2. Generate new sync_token and return to user.

    Return:
    {
        'sync_token': 'jym0JTE-svI8iDOPp-6e_UMe6dYOVVNSVes8pzZCXDd_I4xn3CYT-oyGVjaCgKgtHO' (based on new last_sync_time),
        'Diary': {
            'delete': [
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241DB0",
                    'time': 1477139340,
                }
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241ABC",
                    'time': 1477139340,
                }
            ],
            'upsert': [
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDC-FE98C5241DB0",
                    'time': 1477139399,
                    'title': 'I'm created by other client',
                    'content': 'I'm created by other client',
                },
                {
                    'uuid': "04B977C7-6F7F-4D36-BFDC-FE98C5241DB0",
                    'time': 1477139399,
                    'title': 'I'm created by other client',
                    'content': 'I'm created by other client',
                }
            ]
        }
     }
    """
    result = {}
    changed_diary_list = db_diary.get_diary_list_since_last_sync(user_id, last_sync_time)
    delete = []
    upsert = []
    for diary in changed_diary_list:
        if diary.get('time_removed') == 0:
            upsert.append(diary)
        else:
            delete.append(diary)
    if len(delete):
        result['delete'] = delete
    if len(upsert):
        result['upsert'] = upsert
    new_sync_token = safetyutils.encrypt_sync_token(int(time.time()))
    result['sync_token'] = new_sync_token
    return result


def _push_diary_data_by_action(user_id, action, data):
    """
    Update server diary database based on client changes.

    action: 'create'
    data: {
        'uuid': "2F69DEB5-B631-40DD-A65E-AFE9A0882275",
        'time': 1477139399,
        'title': 'this is a new diary',
        'content': 'today is a good day',
    }
    action:'delete'
    data: {
        'uuid': "04B977C7-6F7F-4D36-BFDA-FE98C5241DB0"
        'time': 1477139399,
    }
    """
    if action == 'create' or action == 'update':
        db_diary.upsert_diary(user_id, data.get('uuid'), data)
    elif action == 'delete':
        db_diary.delete_diary(user_id, data.get('uuid'), data.get('time'))
    else:
        return
