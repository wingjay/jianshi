# -*- coding:utf-8 -*-
import socket

# fetch local ip address
localhost = socket.gethostbyname(socket.gethostname())
print localhost

from server import app

if __name__ == "__main__":
    app.run(host=localhost, debug=True)
