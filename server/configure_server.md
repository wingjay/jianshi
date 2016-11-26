```
ssh root@106.14.26.35   jianshiserver
mkdir /home/test/wingjay-flask
```

```
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install python-pip python-dev nginx git supervisor python-virtualenv ipython
sudo pip install --upgrade pip 
```

## Enter virtualEnv to install python dependency

```
cd /home/test/wingjay-flask
virtualenv venv
source venv/bin/activate # must enter venv to install python dependecy
```

```
vim requirements.txt : Flask==0.10.1  gunicorn==19.6.0
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
[root@jianshi/server] vim wsgi.py
# it must be application, not app.
from server import app as application

if __name__ == "__main__":
   application.run(debug=False, host='0.0.0.0')
```

## Testing Gunicorn's Ability to Serve the Project

```
[root@jianshi/server]
gunicorn --bind 0.0.0.0:8000 wsgi

visit http://ip:8000
```

## Create an Upstart Script

deactivate from virtualenv

This Upstart script will allow Ubuntu's init system to automatically start Gunicorn and serve our Flask application whenever the server boots.

```
sudo vim /etc/init/jianshi.conf
```

```
description "Gunicorn application server running jianshi"

start on runlevel [2345]
stop on runlevel [!2345]

respawn
setuid  root
setgid  www-data

env PATH=/var/lib/jenkins/workspace/jianshi/server/venv/bin
chdir /var/lib/jenkins/workspace/jianshi/server/
exec gunicorn --workers 3 --bind unix:jianshi.sock -m 007 wsgi
```

```
sudo start jianshi
```

## Configuring Nginx to Proxy Requests

```
sudo vim /etc/nginx/sites-available/jianshi
```

```
server {
    listen 80;
    server_name 106.14.26.35 jianshi.wingjay.com;

	access_log /var/lib/jenkins/workspace/jianshi/server/logs/nginx/access.log;
    error_log /var/lib/jenkins/workspace/jianshi/server/logs/nginx/error.log;
    
    location / {
       include proxy_params;
       proxy_pass http://unix:/var/lib/jenkins/workspace/jianshi/server/jianshi.sock;
    }
}
```

This `location` will pass the requests to the socket we defined using the `proxy_pass` directive

```
sudo ln -s /etc/nginx/sites-available/jianshi /etc/nginx/sites-enabled   # link the file to the sites-enabled directory
```

```
sudo nginx -t   # test for syntax errors
```

```
sudo service nginx restart  # start nginx
```

Done! http://106.14.26.35/

[How To Serve Flask Applications with Gunicorn and Nginx on Ubuntu 14.04](https://www.digitalocean.com/community/tutorials/how-to-serve-flask-applications-with-gunicorn-and-nginx-on-ubuntu-14-04)

[Deploying a Flask Site Using NGINX Gunicorn, Supervisor and Virtualenv on Ubuntu](http://alexandersimoes.com/hints/2015/10/28/deploying-flask-with-nginx-gunicorn-supervisor-virtualenv-on-ubuntu.html)

## Supervisor

可以同时启动多个应用，最重要的是，当某个应用Crash的时候，他可以自动重启该应用，保证可用性。

Supervisor is a software that allows users to manage multiple processes, so we could actually have multiple gunicorn sites running with different configurations.

```
sudo vim /etc/supervisor/conf.d/jianshi.conf
[program:jianshi]
command = /var/lib/jenkins/workspace/jianshi/server/venv/bin/gunicorn wsgi -w 4
directory = /var/lib/jenkins/workspace/jianshi/server
user = root
stdout_logfile = /var/lib/jenkins/workspace/jianshi/server/logs/gunicorn/gunicorn_stdout.log
stderr_logfile = /var/lib/jenkins/workspace/jianshi/server/logs/gunicorn/gunicorn_stderr.log
redirect_stderr = True
environment = PRODUCTION=1
```

```
sudo supervisorctl reread
sudo supervisorctl update
sudo supervisorctl start jianshi
```

## Install Jekins in ubuntu

[Doc](https://wiki.jenkins-ci.org/display/JENKINS/Installing+Jenkins+on+Ubuntu)

```
wget -q -O - https://pkg.jenkins.io/debian/jenkins-ci.org.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt-get update
sudo apt-get install jenkins
# Edit /etc/default/jenkins to change port
```

Admin + yin

- Create freeStyle jobs
- General: Github project -> https://github.com/wingjay/jianshi/
- Source Code Management: Git Repository URL -> https://github.com/wingjay/jianshi ; Branch -> */v2.0
- Build Triggers: Build when a change is pushed to GitHub; Poll SCM: H/30 * * * * (every 30 mins auto build)



Code in ubuntu is on `/var/lib/jenkins/workspace/jianshi`

After building, jenkins will auto pull code from github into server.

## Log Rotation

```
sudo vim /etc/logrotate.d/nginx
/var/log/nginx/*.log /var/lib/jenkins/workspace/jianshi/server/logs/nginx/*.log {
     ...
}
```



# Import Command

```
sudo service nginx stop -> This site can't be reachable
sudo stop jianshi -> 502. Totally restart jianshi service
```

? question

Seems the supervisor doesn't



## Install MySQL

```
sudo apt-get update
sudo apt-get install mysql-server  #jianshiserver
sudo mysql_secure_installation
sudo mysql_install_db

service mysql status
sudo service mysql start
```

```
Make mysql allow connections remotely
1. enter mysql
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'thepassword' WITH GRANT OPTION;
FLUSH PRIVILEGES;
2. sudo vim /etc/mysql/my.cnf
#bind-address = 127.0.0.1
3. sudo service mysql restart
```

```
In my own computer, i have two ways to connect mysql
1. mysql -u root -p -h 106.14.26.35
2. Sequel pro.
```

## Config
1. Load config during init
```python
# load config
app.config.from_object("conf.all")
app.config.from_pyfile('config.py')
if app.config['DEBUG']:
    app.config.from_object("conf.dev")
else:
    app.config.from_object("conf.prod")
```
2. set `DEBUG = True` in conf.all
3. set `DEBUG = False` in instance/config.py in Prod env!! Don't forget.


instance/config.py is not included in open source and stored sensentive data into itself.
```
# NOTICE!!! DEBUG Must be turn-on in prod env
#DEBUG = False

# safety constant. The real key is stored in other config, which isn't included in this open-source repo
DEFAULT_KEY = 'xxx'
AUTH_TOKEN_ENCRYPT_KEY = 'xxx'
SYNC_TOKEN_ENCRYPT_KEY = 'xxx'

# mysql
MYSQL_LOCAL_HOST = 'localhost'
MYSQL_USER       = 'user'
MYSQL_PASSWORD   = 'password'
MYSQL_DB_NAME    = 'dbname'


#Email
EMAIL_ADDRESS = 'email'
EMAIL_PASSWORD = 'password' # client authorization code from mail.126.com
```
