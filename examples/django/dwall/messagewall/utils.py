import datetime
import calendar

from django.conf import settings
import redis


def get_redis_connection():
    return redis.StrictRedis(connection_pool=settings.REDIS_POOL)


def get_unix_timestamp_now():
    return calendar.timegm(datetime.datetime.utcnow().utctimetuple())


def unix_timestamp_to_datetime(unix_date):
    return datetime.datetime.fromtimestamp(int(unix_date))
