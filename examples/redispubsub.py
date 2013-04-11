import sys
import time
import datetime

import redis

channel = "Redis:pubsub:example"
r = redis.Redis()


def publish():
    while(True):
        r.publish(channel, datetime.datetime.now())
        time.sleep(1)


def subscribe():
    p = r.pubsub(channel)
    p.subscribe(channel)

    for data in p.listen():
        print data['data']

if sys.argv[1] == "pub":
    publish()
elif sys.argv[1] == "sub":
    subscribe()
else:
    print("use: command [pub|sub]")
