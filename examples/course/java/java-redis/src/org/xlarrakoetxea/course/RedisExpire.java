package org.xlarrakoetxea.course;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/22/13
 * Time: 6:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedisExpire {

    private static final String KEY = "counter";
    private static final int MILISECS = 5000;

    public static void main(String[] args) throws InterruptedException {
        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();
        try{
            r.set(RedisExpire.KEY, "Some value");
            r.expire(RedisExpire.KEY, RedisExpire.MILISECS/1000);
            System.out.println(MessageFormat.format("Result at {0}: {1}", new Date(), r.get(RedisExpire.KEY)));
            for (int i=0; i < RedisExpire.MILISECS/ 500; i++){
                System.out.println(MessageFormat.format("Expiration seconds: {0}", r.ttl(RedisExpire.KEY)));
                Thread.sleep(500);
            }
            System.out.println(MessageFormat.format("Result at {0}: {1}", new Date(), r.get(RedisExpire.KEY)));
        }finally {
            pool.returnResource(r);
            pool.destroy();
        }
    }
}
