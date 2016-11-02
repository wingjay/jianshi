#!/usr/bin/python
import sys

sys.stdout = sys.stderr
sys.path.insert(0, "/Users/Jay/projects/jianshi/server")

import os
import monitor
monitor.start(interval=1.0)
monitor.track(os.path.join(os.path.dirname(__file__), 'site.cf'))

from server import app as application

# I'm in DAEMON MODE
# def application(environ, start_response):
#     status = '200 OK'

#     if not environ['mod_wsgi.process_group']:
#       output = 'EMBEDDED MODE'
#     else:
#       output = 'DAEMON MODE'

#     response_headers = [('Content-Type', 'text/plain'),
#                         ('Content-Length', str(len(output)))]

#     start_response(status, response_headers)

#     return [output]
