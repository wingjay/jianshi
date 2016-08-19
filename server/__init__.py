import time

from flask import Flask, g
import pymysql
import pymysql.cursors

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello, I love Digital Ocean! Jianshi"


@app.route("/get")
def get():
    return "get function works Jianshi"



def _conn(cursorclass=pymysql.cursors.Cursor):
	return pymysql.connect(host='192.168.33.10',
                             user='emma',
                             password='emma',
                             db='jianshi',
                             charset='utf8mb4',
                             cursorclass=cursorclass)

def _get_conn(cursorclass=pymysql.cursors.Cursor):
	with app.app_context():
		if not hasattr(g, 'db_conn'):
			g.db_conn=_conn(cursorclass)
		return g.db_conn

def init_db():
	"""Initializes the database."""
	try:
		with _get_conn().cursor() as cursor:
			# execute schema sql file
			with app.open_resource('db/schema/0001/user.sql', mode='r') as f:
				sql = f.read()
				print sql
				result = cursor.execute(sql)
				print result
	
	finally:
		print 'close connection'
		print _get_conn().close()
	

def create_user(email, password):
	conn = _get_conn(pymysql.cursors.DictCursor)
	time_created = int(time.time())
	try:
		with conn.cursor() as cursor:
			sql = "insert into `User` (`email`, `password`, `time_created`, `time_modified`) values (%s, %s, %s, %s)"
			cursor.execute(sql, (str(email), str(password), str(time_created), '0'))
		
		conn.commit()
	finally:
		conn.close()	


def get_user():
	try:
		with _get_conn().cursor() as cursor:
			sql = "select * from User"
			print sql
			print cursor.execute(sql)
			data = cursor.fetchall()
			for d in data:
				print d
		
	finally:
		_get_conn().close()	
		print 'close'	

def get_user_by_id(id):
    return "get user from db by id" + id


if __name__ == "__main__":
    app.run()
