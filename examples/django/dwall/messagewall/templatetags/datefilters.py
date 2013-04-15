
from django import template
from django.template.defaultfilters import stringfilter

from messagewall import utils

register = template.Library()


@register.filter
@stringfilter
def from_unix_timestamp(value):
    return utils.unix_timestamp_to_datetime(int(value))
