from server import app
from server.www.base import mobile_request, must_login
from server.logic import sync as logic_sync

logger = app.logger


@app.route("/sync", methods=['POST'])
@mobile_request
@must_login
def sync_data(user_id, sync_token=None, sync_items=[], need_pull=True, **kwargs):
    logger.info("start sync data %s, %s, %s, %s", user_id, sync_token, sync_items, need_pull)
    return logic_sync.sync_data(user_id, sync_token, sync_items, need_pull)
