from server import app
from server.www.base import mobile_request, must_login
from server.logic import sync as logic_sync

logger = app.logger


@app.route("/sync", methods=['POST'])
@mobile_request
def sync_data(user_id, sync_token=None, sync_items=[], need_pull=True, **kwargs):
    return logic_sync.sync_data(user_id, sync_token, sync_items, need_pull)
