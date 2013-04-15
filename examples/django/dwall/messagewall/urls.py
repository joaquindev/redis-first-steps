
from django.conf.urls import patterns, url
import messagewall.views

urlpatterns = patterns(
    '',
    url(r'^$',
        messagewall.views.index, name="messagewall-index"),
    url(r'^wall$',
        messagewall.views.show_messages, name="messagewall-show_messages"),
)

ajax_patters = patterns(
    '',
    url(r'^get-messages$',
        messagewall.views.ajax_show_messages,
        name="messagewall-ajax-get_messages"),
)

urlpatterns += ajax_patters
