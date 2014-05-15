#!/bin/bash

cd site
gunicorn jadebusem.wsgi
