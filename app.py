# -*- coding:utf-8 -*-
import os
import socket

# fetch local ip address
localhost = socket.gethostbyname(socket.gethostname())
print localhost

# create empty instance directory
current_dir = os.path.dirname(os.path.abspath(__file__))
print current_dir
path = current_dir + '/instance/config.py'
if not os.path.exists(path):
    print 'not exists'
    os.popen('cd ' + current_dir +' ; mkdir instance' + '; cd ' + current_dir
             + '/instance/' + ' ; touch config.py; touch __init__.py')

from server import app
app.run(host='0.0.0.0', debug=True, port=port)
