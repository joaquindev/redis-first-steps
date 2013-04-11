import time

import redis

TIMES = 1000000


def with_pipelining():
    r = redis.Redis()
    p = r.pipeline(transaction=False)
    map(p.incr, range(TIMES))
    p.execute()


def without_pipelining():
    r = redis.Redis()
    map(r.incr, range(TIMES))


start = time.clock()
without_pipelining()
print("Without pipelining: {0} seconds".format(time.clock() - start))

start = time.clock()
with_pipelining()
print("With pipelining: {0} seconds".format(time.clock() - start))
