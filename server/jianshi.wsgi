#!/usr/bin/python
import sys
sys.stdout = sys.stderr
sys.path.insert(0, "/Users/Jay/projects/jianshi/server")

from server import app as application
