from django.conf.urls import patterns

urlpatterns = patterns('SearchEngine.views',
    (r'^$', 'search'),
)