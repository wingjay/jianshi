#!/usr/bin/python
# coding: utf-8
import server.logic.sync as logic_sync
import time


def test_sync_log():
    log_items = [
        {
            'event_name': 'home_page_impression',
            'page_source': None,
            'time_created': int(time.time())
        },
        {
            'event_name': 'home_create_btn_clk',
            'page_source': None,
            'time_created': int(time.time())
        },
        {
            'event_name': 'home_delete_btn_clk',
            'page_source': None,
            'time_created': int(time.time())
        },
        {
            'event_name': 'view_update_btn_clk',
            'page_source': None,
            'time_created': int(time.time())
        }
    ]
    logic_sync.sync_event_log(1, log_items)
