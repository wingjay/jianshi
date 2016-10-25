# -*- coding:utf-8 -*-
from server import app
import socket
#获取本机ip
localhost = socket.gethostbyname(socket.gethostname())
print localhost
app.run(host=localhost,debug=True)
