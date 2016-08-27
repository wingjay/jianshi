## 环境搭建

1. Ubuntu Server + Apache + MySQL
2. Enable WSGI
3. Install `virtualenv` & `flask`
4. Create `Flask Application` 

[How To Serve Flask Applications with uWSGI and Nginx on Ubuntu 16.04](https://www.digitalocean.com/community/tutorials/how-to-serve-flask-applications-with-uwsgi-and-nginx-on-ubuntu-16-04)

[How To Deploy a Flask Application on an Ubuntu VPS](https://www.digitalocean.com/community/tutorials/how-to-deploy-a-flask-application-on-an-ubuntu-vps)

## Install Apache

1. For Mac OS, it already has Apache and doesn't need to install.
2. Start/Stop/Restart: `sudo apachectl start/stop/restart`
3. System level web root: `/Library/WebServer/Documents/`
4. User level root: `~/Sites`, the configuration location is `/etc/apache2`
5. Enable `vitural host` to run sites individually. `http://jay.local/` ——`/Users/Jay/Sites`
6. Edit `/etc/apache2/extra/httpd-vhosts.conf`, Add `ServerAlias 192.168.*` So Computer & Genymotion will be able to use IP address to connect apache server.

[Get Apache, MySQL, PHP and phpMyAdmin working on OSX 10.11 El Capitan](https://coolestguidesontheplanet.com/get-apache-mysql-php-and-phpmyadmin-working-on-osx-10-11-el-capitan/)

[How to set up Virtual Hosts in Apache on Mac OSX 10.11 El Capitan](https://coolestguidesontheplanet.com/how-to-set-up-virtual-hosts-in-apache-on-mac-osx-10-11-el-capitan/)

[403 permission](http://stackoverflow.com/questions/10873295/error-message-forbidden-you-dont-have-permission-to-access-on-this-server?rq=1)



## Install MySQL

Because I already has one mysql in my vituralBox, It's hard and non-sense to install another one in my computer. So I can connect to vituralBox MySQL directly.

```mysql
> mysql -u emma -h 192.168.33.10 -p
> pwd: emma
```

```py
import pymysql.cursors

# Connect to the database
connection = pymysql.connect(host='192.168.33.10',
                             user='emma',
                             password='emma',
                             # db='db',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)
```

**If you don't have mysql, you can download from their [official site](https://www.mysql.com/). And there are lots of tutorial outside on the Internet, So I won't talk much about its installation**



## Install Flask app

[Flask app example](http://flask.pocoo.org/)

1. install flask `pip install flask`

2. create `app.python`

   ```py
   from flask import Flask
   app = Flask(__name__)

   @app.route("/")
   def hello():
       return "Hello World!"

   @app.route("/haha")
   def haha():
   	return "haha"

   if __name__ == "__main__":
       app.run()
   ```

3. Run `python app.python`

   ```
   Running on http://localhost:5000/
   ```



## Setup Apache WSGI module

> We should make the `Flask application running under Apache WSGI module`.
>
> [Official mod_wsgi](https://github.com/GrahamDumpleton/mod_wsgi) should be installed in python & apache

#### 1. Install mod_wsgi into python.

 `pip install mod_wsgi` After that, you'll be able to use this command `mod_wsgi-express` And `mod_wsgi-express start-server` in current directory `which owns a *.wsgi file`

#### 2. Install mod_wsgi into Apache

[Install Manual](http://localhost:8000/__wsgi__/docs/user-guides/quick-installation-guide.html)

1. download from https://github.com/GrahamDumpleton/mod_wsgi/releases
2. Unpack `tar xvfz mod_wsgi-4.5.5.tar.gz`
3. (If you're using MacOS, read [this](http://modwsgi.readthedocs.io/en/develop/user-guides/installation-on-macosx.html))     `cd mod_wsgi-4.5.5`  &  `./configure`  &  `make` & `sudo make install LIBEXECDIR=/usr/local/apache/modules` & `LoadModule wsgi /usr/local/apache/modules/mod_wsgi.so`  in`/etc/apache/httpd.conf`

#### 3. Create testapp.wsgi

```py
#!/usr/bin/python
import sys
sys.path.insert(0, "/Users/Jay/Sites/testapp/")

from app import app as application
```

structure in `/Users/Jay/Sites` is

```
.
├── index.html
└── testapp
    ├── app
    │   ├── __init__.py
    │   ├── static
    │   └── templates
    ├── index.html
    └── testapp.wsgi
```

#### 4. Configure Apache

(Read [here](http://modwsgi.readthedocs.io/en/develop/user-guides/quick-configuration-guide.html))

Edit `/etc/apache2/extra/httpd-vhosts.conf`

Add as below:

```py
<VirtualHost *:80>
    ServerAdmin jay
    DocumentRoot "/Users/Jay/Sites/testapp"
    ServerName jay.local
    ServerAlias www.jay.local
    
    WSGIDaemonProcess jay.local processes=2 threads=15 display-name=%{GROUP}
    WSGIProcessGroup jay.local
    WSGIScriptAlias / /Users/Jay/Sites/testapp/testapp.wsgi
    <Directory "/Users/Jay/Sites/testapp">
        Require all granted
    </Directory>
    
    ErrorLog "/private/var/log/apache2/jay.local-error_log"
    CustomLog "/private/var/log/apache2/jay.local-access_log" common
</VirtualHost>
```

#### 5. Restart Apache and Test

`sudo apachectl restart`  & `curl http://jay.local/get`



## Change website from testapp to Jianshi-Server

1. create a empty folder called `server`

2. create a `__init__.py` in `server`

   ```py
     1 from flask import Flask
     2 app = Flask(__name__)
     3
     4 @app.route("/")
     5 def hello():
     6     return "Hello, I love Digital Ocean! Jianshi"
     7
     8
     9 @app.route("/get")
    10 def get():
    11     return "get function works Jianshi"
    12
    13 if __name__ == "__main__":
    14     app.run()
   ```

3. create a `jianshi.wsgi`

   ```python
   #!/usr/bin/python
   import sys
   sys.stdout = sys.stderr
   sys.path.insert(0, "/Users/Jay/projects/jianshi/")

   from server import app as application
   ```

4. configure apache vhost `/etc/apache2/extra/httpd-vhosts.conf`

   ```py
   <VirtualHost *:80>
       ServerAdmin jay
       DocumentRoot "/Users/Jay/projects/jianshi"
       ServerName jay.local
       ServerAlias www.jay.local
       
       WSGIDaemonProcess jay.local processes=2 threads=15 display-name=%{GROUP}
       WSGIProcessGroup jay.local

       WSGIScriptAlias / /Users/Jay/projects/jianshi/jianshi.wsgi
       <Directory "/Users/Jay/projects/jianshi">
           Require all granted
       </Directory>
       ErrorLog "/private/var/log/apache2/jay.local-error_log"
       CustomLog "/private/var/log/apache2/jay.local-access_log" common
   </VirtualHost>
   ```


5. Edit `/etc/apache2/users/Jay.conf`

   ```py
   <Directory "/Users/Jay/projects/jianshi">
        AllowOverride All
        Options Indexes MultiViews FollowSymLinks
        Require all granted
   </Directory>
   ```

6. Restart apache  `sudo apachectl restart`



#### Material

[Flask mod_wsgi (Apache)](http://flask.pocoo.org/docs/0.11/deploying/mod_wsgi/)

[Installer for Apache/mod_wsgi](https://pypi.python.org/pypi/mod_wsgi)

[How To Deploy a Flask Application on an Ubuntu VPS](https://www.digitalocean.com/community/tutorials/how-to-deploy-a-flask-application-on-an-ubuntu-vps)



## Knowledge base

1. virtualenv 用来建立一个虚拟的python环境，一个专属于项目的python环境。用virtualenv 来保持一个干净的环境非常有用。
2. pip 用来解决项目依赖问题。将项目所有依赖的第三方库写在一个requirements.txt 中用pip 批量安装。一般和virtualenv 配合使用，将所有包安装在virtualenv 的环境中
3.  fabric 用来自动化远程部署项目，非常的方便。可以根据需要在本地、远程依次执行一系列shell 命令、程序等。比如从代码库更新代码，执行数据迁移脚本，重启服务进程，完成自动化的部署

1. `PyMySQL` 是在Python3.x 版本中用于连接MySQL 服务器的一个库，Python2中则使用`mysqldb`。 PyMySQL 遵循Python 数据库API v2.0 规范，并包含了`pure-Python` MySQL 客户端库。

   pymysql is written in python, mysqldb is a c-extension 

   `API is same as mySQLdb.`

   gevent

   [Python3 MySQL 数据库连接](http://www.runoob.com/python3/python3-mysql.html)

2. [MySQLdb](http://mysql-python.sourceforge.net/MySQLdb.html) 是用于Python链接Mysql数据库的接口，它实现了 Python 数据库 API 规范 V2.0，基于 MySQL C API 上建立的。

   [python操作mysql数据库](http://www.runoob.com/python/python-mysql.html)

   ```python
   #!/usr/bin/python
   # -*- coding: UTF-8 -*-

   import MySQLdb

   # 打开数据库连接
   db = MySQLdb.connect("localhost","testuser","test123","TESTDB" )

   # 使用cursor()方法获取操作游标 
   cursor = db.cursor()

   # 如果数据表已经存在使用 execute() 方法删除表。
   cursor.execute("DROP TABLE IF EXISTS EMPLOYEE")

   # 创建数据表SQL语句
   sql = """CREATE TABLE EMPLOYEE (
            FIRST_NAME  CHAR(20) NOT NULL,
            LAST_NAME  CHAR(20),
            AGE INT,  
            SEX CHAR(1),
            INCOME FLOAT )"""

   cursor.execute(sql)

   # SQL 插入语句
   sql = """INSERT INTO EMPLOYEE(FIRST_NAME,
            LAST_NAME, AGE, SEX, INCOME)
            VALUES ('Mac', 'Mohan', 20, 'M', 2000)"""
   try:
      # 执行sql语句
      cursor.execute(sql)
      # 提交到数据库执行
      db.commit()
   except:
      # Rollback in case there is any error
      db.rollback()

   # 使用execute方法执行SQL语句
   cursor.execute("SELECT VERSION()")

   # 使用 fetchone() 方法获取一条数据库。
   data = cursor.fetchone()

   print "Database version : %s " % data

   # 关闭数据库连接
   db.close()
   ```

   ​



