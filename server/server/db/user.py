#!/usr/bin/python
# coding: utf-8

import time

import pymysql
import pymysql.cursors
from flask import Flask, g

from server import app
from server.util import safetyutils

# @app.route("/user")
# def user():
# 	return "root from User, app.route works"

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
			with app.open_resource('schema/0001/user.sql', mode='r') as f:
				sql = f.read()
				print sql
				result = cursor.execute(sql)
				print result
	
	finally:
		print _get_conn().close()

def create_user(email, password):
	if email is None or password is None:
		return False
	password = safetyutils.get_hash_password(password)
	conn = _get_conn(pymysql.cursors.DictCursor)
	time_created = int(time.time())
	try:
		with conn.cursor() as cursor:
			sql = "insert into `User` (`email`, `password`, `time_created`, `time_modified`) values (%s, %s, %s, %s)"
			cursor.execute(sql, (str(email), str(password), str(time_created), '0'))
		
		conn.commit()
	finally:
		conn.close()	


def login(email, password):
	if email is None or password is None:
		return False
	conn = _get_conn(pymysql.cursors.DictCursor)
	try:
		with conn.cursor() as cursor:
			sql = "select * from `User` where `email` = %s "
			cursor.execute(sql, (str(email)))
			_user = cursor.fetchone()
			if not _user:
				return False
			else:
				return safetyutils.verify_hash_password(_user['password'], password)	
	finally:
		conn.close()


def get_user(user_id):
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