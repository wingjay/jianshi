#!/usr/bin/python
# coding: utf-8

import pymysql
import pymysql.cursors
from flask import Flask, g

def _conn():
	return pymysql.connect(host='192.168.33.10',
                             user='emma',
                             password='emma',
                             db='jianshi',
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)

def _get_conn():
	if not hasattr(g, 'db_conn'):
		g.db_conn=_conn()
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

		cursor.commit()		
	finally:
		print 'close connection'
		print _get_conn().close()
	

def get_user_by_id(id):
    return "get user from db by id" + id
