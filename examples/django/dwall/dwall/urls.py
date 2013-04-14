from django.conf.urls import patterns, include, url

import messagewall.urls
urlpatterns = patterns(
    '',
    url(r'^', include(messagewall.urls)),
)
