# coding=utf-8
# Django settings for jadebusem project.

import django.conf.global_settings as DEFAULT_SETTINGS
import os.path

DEBUG = True
TEMPLATE_DEBUG = DEBUG
ADMINS = (
    ('alanhawrot', 'alan.hawrot@gmail.com'),
    ('michalsemik', 'michal.semik@gmail.com'),
    ('mateuszbobinski', 'mat.bobinski@gmail.com'),
)

MANAGERS = ADMINS

DATABASES = {
    #######################################################################################
    #
    # !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! UWAGA !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    #
    # Przy konfiguracji lokalnej proszę o pozostawienie tej konfiguracji bez zmian.
    # Nadpisywać jedynie wpis 'local', 'default' nie ruszać.
    # To może być konieczne bo na dole dzieje się konfiguracja związana z heroku.
    #
    #######################################################################################
    'default': { #konfiguracja heroku
        'ENGINE': 'django.db.backends.postgresql_psycopg2', # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
        'NAME': 'd1kj79e4uus55f', # Or path to database file if using sqlite3.
        # The following settings are not used with sqlite3:
        'USER': 'hriaoepqihdupl',
        'PASSWORD': 'o6RZgPY-qQX6GCqJLCzlcHydH6',
        'HOST': 'ec2-184-73-254-144.compute-1.amazonaws.com',
        # Empty for localhost through domain sockets or '127.0.0.1' for localhost through TCP.
        'PORT': '5432',
    },

    # Localhost database
    # 'local': {
    #     'ENGINE': 'django.db.backends.sqlite3', # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
    #     'NAME': 'D:\\Programowanie\\Eclipse workspace\\sqlite.db',                      # Or path to database file if using sqlite3.
    #     # The following settings are not used with sqlite3:
    #     'USER': '',
    #     'PASSWORD': '',
    #     'HOST': '',                      # Empty for localhost through domain sockets or '127.0.0.1' for localhost through TCP.
    #     'PORT': '',
    # }

    ## Alan's local db
    # 'local': {
    #    'ENGINE': 'django.db.backends.sqlite3', # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
    #    'NAME': '/home/alanhawrot/Dokumenty/sqlite.db',                      # Or path to database file if using sqlite3.
    #    # The following settings are not used with sqlite3:
    #    'USER': '',
    #    'PASSWORD': '',
    #    'HOST': '',                      # Empty for localhost through domain sockets or '127.0.0.1' for localhost through TCP.
    #    'PORT': '',
    # }

    ## Michal's local db
    'local': {
        'ENGINE': 'django.db.backends.sqlite3', # Add 'postgresql_psycopg2', 'mysql', 'sqlite3' or 'oracle'.
        'NAME': os.path.dirname(os.path.dirname(__file__)) + "/db.sqlite",                      # Or path to database file if using sqlite3.
        # The following settings are not used with sqlite3:
        'USER': '',
        'PASSWORD': '',
        'HOST': '',                      # Empty for localhost through domain sockets or '127.0.0.1' for localhost through TCP.
        'PORT': '',
    }
}

# Hosts/domain names that are valid for this site; required if DEBUG is False
# See https://docs.djangoproject.com/en/1.5/ref/settings/#allowed-hosts
ALLOWED_HOSTS = []

# Local time zone for this installation. Choices can be found here:
# http://en.wikipedia.org/wiki/List_of_tz_zones_by_name
# although not all choices may be available on all operating systems.
# In a Windows environment this must be set to your system time zone.
TIME_ZONE = 'Europe/Warsaw'

# Language code for this installation. All choices can be found here:
# http://www.i18nguy.com/unicode/language-identifiers.html
LANGUAGE_CODE = 'en-US'

ugettext = lambda s: s

LANGUAGES = (
    ('en', ugettext('English')),
    ('pl', ugettext('Polish')),
)

SITE_ID = 1

# If you set this to False, Django will make some optimizations so as not
# to load the internationalization machinery.
USE_I18N = True

# If you set this to False, Django will not format dates, numbers and
# calendars according to the current locale.
USE_L10N = True

# If you set this to False, Django will not use timezone-aware datetimes.
USE_TZ = True

# Absolute filesystem path to the directory that will hold user-uploaded files.
# Example: "/var/www/example.com/media/"
MEDIA_ROOT = ''

# URL that handles the media served from MEDIA_ROOT. Make sure to use a
# trailing slash.
# Examples: "http://example.com/media/", "http://media.example.com/"
MEDIA_URL = ''

# Absolute path to the directory static files should be collected to.
# Don't put anything in this directory yourself; store your static files
# in apps' "static/" subdirectories and in STATICFILES_DIRS.
# Example: "/var/www/example.com/static/"

# Static asset configuration
import os




TEMPLATE_CONTEXT_PROCESSORS = DEFAULT_SETTINGS.TEMPLATE_CONTEXT_PROCESSORS + \
                              ('jadebusem_site.context_processors.language_processor',
                               'jadebusem_site.context_processors.new_topics_processor'
)
# List of finder classes that know how to find static files in
# various locations.
STATICFILES_FINDERS = (
    'django.contrib.staticfiles.finders.FileSystemFinder',
    'django.contrib.staticfiles.finders.AppDirectoriesFinder',
    #    'django.contrib.staticfiles.finders.DefaultStorageFinder',
)

# Make this unique, and don't share it with anybody.
SECRET_KEY = 'eu6+-i04v%ia1wj7=mxz=n+e7l3!g+!sb^))+_g+or0=r4kufx'

# List of callables that know how to import templates from various sources.
TEMPLATE_LOADERS = (
    'django.template.loaders.filesystem.Loader',
    'django.template.loaders.app_directories.Loader',
    #     'django.template.loaders.eggs.Loader',
)

MIDDLEWARE_CLASSES = (
    'django.middleware.common.CommonMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.locale.LocaleMiddleware',
    #'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    # Uncomment the next line for simple clickjacking protection:
    # 'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'jadebusem.urls'

# Python dotted path to the WSGI application used by Django's runserver.
WSGI_APPLICATION = 'jadebusem.wsgi.application'

TEMPLATE_DIRS = (
    # Put strings here, like "/home/html/django_templates" or "C:/www/django/templates".
    # Always use forward slashes, even on Windows.
    # Don't forget to use absolute paths, not relative paths.
    "./templates"
)

INSTALLED_APPS = (
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.sites',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    # Uncomment the next line to enable the admin:
    'django.contrib.admin',
    # Uncomment the next line to enable admin documentation:
    # 'django.contrib.admindocs',
    'south',
    'Users',
    'schedules',
    'topics',
    'SearchEngine',
    'rest_framework',
)

SESSION_SERIALIZER = 'django.contrib.sessions.serializers.JSONSerializer'

# A sample logging configuration. The only tangible logging
# performed by this configuration is to send an email to
# the site admins on every HTTP 500 error when DEBUG=False.
# See http://docs.djangoproject.com/en/dev/topics/logging for
# more details on how to customize your logging configuration.
LOGGING = {
    'version': 1,
    'disable_existing_loggers': True,
    'formatters': {
        'verbose': {
            'format': '%(levelname)s %(asctime)s %(module)s %(process)d %(thread)d %(message)s'
        },
        'simple': {
            'format': '%(levelname)s %(message)s'
        },
    },
    'handlers': {
        'null': {
            'level': 'DEBUG',
            'class': 'logging.NullHandler',
        },
        'console':{
            'level': 'DEBUG',
            'class': 'logging.StreamHandler',
            'formatter': 'simple'
        },
        'mail_admins': {
            'level': 'ERROR',
            'class': 'django.utils.log.AdminEmailHandler',
            'filters': [],
        }
    },
    'loggers': {
        'django': {
            'handlers': ['null'],
            'propagate': True,
            'level': 'INFO',
        },
        'django.request': {
            'handlers': ['mail_admins'],
            'level': 'ERROR',
            'propagate': False,
        },
    }
}

LOCALE_PATHS = (
    'locale',
)

##############################################################
#
#   Below commands are added to configure project for heroku
#
##############################################################
# Parse database configuration from $DATABASE_URL
import dj_database_url

PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATABASES['default'] = dj_database_url.config()
if not DATABASES['default']:
    DATABASES['default'] = DATABASES['local']

STATIC_ROOT = os.path.join(PROJECT_ROOT, 'staticfiles')
# URL prefix for static files.
# Example: "http://example.com/static/", "http://static.example.com/"
STATIC_URL = '/static/'


STATICFILES_DIRS = (
    os.path.join(PROJECT_ROOT, 'static'),
)

# Honor the 'X-Forwarded-Proto' header for request.is_secure()
SECURE_PROXY_SSL_HEADER = ('HTTP_X_FORWARDED_PROTO', 'https')

# Allow all host headers
ALLOWED_HOSTS = ['*']

print 'DB', DATABASES['default']
print 'CWD', os.getcwd()
print 'PROJECT_ROOT', PROJECT_ROOT
print 'STATIC_ROOT', STATIC_ROOT
print 'STATIC_URL', STATIC_URL
print 'STATICFILES_DIRS', STATICFILES_DIRS

os.environ['MEMCACHE_SERVERS'] = 'mc4.dev.eu.ec2.memcachier.com:11211'.replace(',', ';')
os.environ['MEMCACHE_USERNAME'] = '95623c'
os.environ['MEMCACHE_PASSWORD'] = '2ac0f71769'

CACHES = {
  'default': {
    'BACKEND': 'django_pylibmc.memcached.PyLibMCCache',
    'TIMEOUT': 500,
    'BINARY': True,
    'OPTIONS': { 'tcp_nodelay': True }
  }
}