from django.conf.urls import patterns, include, url
from django.conf import settings

import messagewall.urls

urlpatterns = patterns(
    '',
    url(r'^', include(messagewall.urls)),
)

if settings.DEBUG:
    urlpatterns += patterns(
        '',
        (r'^static/(?P<path>.*)$',
            'django.views.static.serve',
            {'document_root': settings.STATIC_ROOT}
        ),
    )
