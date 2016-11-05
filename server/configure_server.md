```
ssh root@106.14.26.35   jianshiserver
mkdir /home/test/wingjay-flask
```

```
sudo apt-get update
sudo apt-get install python-pip python-dev nginx
sudo pip install --upgrade pip 
```

## Enter virtualEnv to install python dependency

```
sudo pip install --upgrade virtualenv 
cd /home/test/wingjay-flask
virtualenv venv
source venv/bin/activate # must enter venv to install python dependecy
```

```
vim requirements.txt : Flask=0.10.1  gunicorn==19.6.0
pip install -r requirements.txt
```

## Create sample app

```
/home/test/wingjay-flask
	runserver.py
	server/__init__.py
	requirements.txt
	venv/
```

```
[runserver.py]
# -*- coding:utf-8 -*-
import os

from server import app

if __name__ == "__main__":
   app.run(debug=False, host='0.0.0.0')
```

```
[server/__init.py]
from flask import Flask
app = Flask(__name__)

@app.route("/")
def index():
    return "Congrats! you find Jianshi Server! Woho~"
```

## Create the WSGI Entry Point

```
[root@wingjay-flask] vim wsgi.py
# it must be application, not app.
from server import app as application

if __name__ == "__main__":
   application.run()
```

## Testing Gunicorn's Ability to Serve the Project

```
[root@wingjay-flask]
gunicorn --bind 0.0.0.0:8000 wsgi

visit http://ip:8000
```

## Create an Upstart Script

This Upstart script will allow Ubuntu's init system to automatically start Gunicorn and serve our Flask application whenever the server boots.

```
sudo vim /etc/init/wingjay-flask.conf
```

```
description "Gunicorn application server running wingjay-flask"

start on runlevel [2345]
stop on runlevel [!2345]

respawn
setuid  root
setgid  www-data

env PATH=/home/test/wingjay-flask/venv/bin
chdir /home/test/wingjay-flask
exec gunicorn --workers 3 --bind unix:myproject.sock -m 007 wsgi
```

```
sudo start wingjay-flask
```

## Configuring Nginx to Proxy Requests

```
sudo vim /etc/nginx/sites-available/myproject
```

```
server {
    listen 80;
    server_name 106.14.26.35;

    location / {
       include proxy_params;
       proxy_pass http://unix:/home/test/wingjay-flask/myproject.sock;
    }
}
```

This `location` will pass the requests to the socket we defined using the `proxy_pass` directive

```
sudo ln -s /etc/nginx/sites-available/myproject /etc/nginx/sites-enabled   # link the file to the sites-enabled directory
```

```
sudo nginx -t   # test for syntax errors
```

```
sudo service nginx restart  # start nginx
```

Done! http://106.14.26.35/

[How To Serve Flask Applications with Gunicorn and Nginx on Ubuntu 14.04](https://www.digitalocean.com/community/tutorials/how-to-serve-flask-applications-with-gunicorn-and-nginx-on-ubuntu-14-04)