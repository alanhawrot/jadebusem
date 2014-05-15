import os
from django.core.wsgi import get_wsgi_application
from dj_static import Cling
import sys

import settings

os.environ['DJANGO_SETTINGS_MODULE'] = os.path.abspath(settings.__file__)
sys.path.insert(0, os.path.abspath(settings.__file__))
sys.path.insert(0, os.path.dirname(os.path.abspath(settings.__file__)))
application = Cling(get_wsgi_application())