from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
from jadebusem import settings

from jadebusem_site.views import about, language

admin.autodiscover()

js_info_dict = {
    'packages': ('jadebusem',),
}

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'jadebusem.views.home', name='home'),
    # url(r'^jadebusem/', include('jadebusem.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),

    url(r'^$', include('SearchEngine.urls')),
    url(r'^i18n/', include('django.conf.urls.i18n')),
    url(r'^jsi18n/$', 'django.views.i18n.javascript_catalog', js_info_dict),
    url(r'^users/', include('Users.urls')),
    url(r'^schedules/', include('schedules.urls')),
    url(r'^topics/', include('topics.urls')),
    url(r'^about/', about),
    url(r'^language', language),
)
urlpatterns += patterns('',
    (r"^static/(.*)$", 'django.views.static.serve', {'document_root': settings.STATIC_ROOT}),
)