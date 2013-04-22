import time
import datetime

import redis

r = redis.Redis()
key = "this:is:a:key"
secs = 5


def expiration(secs):
    r.set(key, "some value")
    r.expire(key, secs)

print("Expiration seconds: {0}".format(secs))

expiration(secs)
print("result at {0}: {1}".format(datetime.datetime.now(), r.get(key)))

for i in range(secs * 3):
    print("TTL: {0}".format(r.ttl(key)))
    time.sleep(0.4)

print("result at {0}: {1}".format(datetime.datetime.now(), r.get(key)))
