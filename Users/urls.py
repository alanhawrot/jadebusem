from django.conf.urls import patterns

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('Users.views',
    (r'^registration/$', 'register'),
    (r'^sign_in/$', 'sign_in'),
    (r'^user_panel/$', 'user_panel'),
)
