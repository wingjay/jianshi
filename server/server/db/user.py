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

def clear_user_table():
	try:
		with _get_conn().cursor() as cursor:
			sql = "drop table `User`"
			cursor.execute(sql)
		
	finally:
		_get_conn().close()	

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
		print _get_conn().close()

def create_user(name, password):
	if name is None or password is None:
		return False
	name_hash = abs(hash(name))	
	password = safetyutils.get_hash_password(password)
	conn = _get_conn(pymysql.cursors.DictCursor)
	time_created = int(time.time())
	new_user_id = -1
	try:
		with conn.cursor() as cursor:
			sql = "insert into `User` (`name`, `name_hash`, `password`, `time_created`, `time_modified`) values (%s, %s, %s, %s, %s)"
			result = cursor.execute(sql, (str(name), str(name_hash), str(password), str(time_created), '0'))
			new_user_id = cursor.lastrowid

		conn.commit()
	finally:
		conn.close()
		return new_user_id


def login(name, password):
	if name is None or password is None:
		return False
	conn = _get_conn(pymysql.cursors.DictCursor)
	try:
		with conn.cursor() as cursor:
			sql = "select * from `User` where `name` = %s "
			cursor.execute(sql, (str(name)))
			_user = cursor.fetchone()
			if not _user:
				return False
			else:
				return safetyutils.verify_hash_password(_user['password'], password)	
	finally:
		conn.close()


def delete_user(user_id):
	"""Delete user."""
	conn = _get_conn(pymysql.cursors.DictCursor)
	try:
		with conn.cursor() as cursor:
			sql = "delete from `User` where `id` = %s"
			cursor.execute(sql, (str(user_id)))

		conn.commit()	
	finally:
		conn.close()


def get_user(user_id):
	user = None
	conn = _get_conn(pymysql.cursors.DictCursor)
	try:
		with conn.cursor() as cursor:
			sql = "select * from User where id = %s"
			cursor.execute(sql, str(user_id))
			user = cursor.fetchall()[0]
		
	finally:
		conn.close()
		return user
