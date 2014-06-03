from django.conf.urls import patterns

urlpatterns = patterns('topics.views',
                       (r'^list_all$', 'list_all'),
                       (r'^show/(?P<topic>\d+)$', 'show'),
                       (r'^create/(?P<schedule>\d+)$', 'create'),
                       (r'^reply/(?P<topic>\d+)$', 'reply'),
                       (r'^close/(?P<topic>\d+)$', 'close'),
)

