from django.conf.urls import patterns

# Uncomment the next two lines to enable the admin:
from django.contrib import admin
from jadebusem import settings
from schedules import views

admin.autodiscover()

urlpatterns = patterns('schedules.views',
                       (r'^add/$', 'add'),
                       (r'^show_list/$', 'show_list'),
                       (r'^modify/(?P<schedule_id>\d+)$', 'modify'),
                       (r'^delete/(?P<schedule_id>\d+)$', 'delete'),
                       (r'^all_schedules/(?P<page>\d+)$', 'get_all_schedules'),
                       (r'^new_schedule/$', 'create_new_schedule'),
)
urlpatterns += patterns('', (r'^static/(.*)$', 'django.views.static.serve', {'document_root': settings.STATIC_ROOT}),
)