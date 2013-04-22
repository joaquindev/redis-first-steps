"""
This example shows the differences betweet pipeline and transaction.

The main difference is that transactions are atomic. In this example we will
launch a pipeline with a number of incr in a counter. also in another thread
there will be a loop sending continous request fo se the state of the counter.

In a transaction redius should block the request until all the transaction is
executed so we would only saw the last change of the multiple increments. On
the other hand with the pipeline we would saw some middle stage because our
watcher commands would mix between the pipeline commands
"""
import sys
import threading

import redis

r = redis.Redis()
key = "counter"
times = 100000


def counter_watcher():
    before = None
    while(True):
        res = r.get(key)
        if before != res:
            print("result: {0}".format(res))
            break


def counter_incr(transaction):
    p = r.pipeline(transaction=transaction)
    for i in range(times):
        p.incr(key)
    p.execute()


def with_pipeline():
    print("Executing pipeline result shouldn't be: {0}".format(times))
    counter_incr(False)


def with_transaction():
    print("Executing transaction result should be: {0}".format(times))
    counter_incr(True)


r.flushdb()
t = threading.Thread(target=counter_watcher)
t.start()

if sys.argv[1] == "pipeline":
    with_pipeline()
elif sys.argv[1] == "transaction":
    with_transaction()
else:
    t.stop()
    print("usage: command pipeline|transaction")
