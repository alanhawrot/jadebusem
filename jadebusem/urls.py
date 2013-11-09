from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin

from jadebusem_site.views import about

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'jadebusem.views.home', name='home'),
    # url(r'^jadebusem/', include('jadebusem.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),

    url(r'^users/', include('Users.urls')),
    url(r'^schedules/', include('schedules.urls')),
    url(r'^search/', include('SearchEngine.urls')),
    url(r'^about/', about),
)
