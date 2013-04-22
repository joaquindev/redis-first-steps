package org.xlarrakoetxea.course;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.text.MessageFormat;

public class RedisPipes {

    private static final int TIMES = 1000000;

    private static void withPipeline(JedisPool pool){
        Jedis r = pool.getResource();
        Pipeline p = r.pipelined();

        try{
            r.flushDB();
            for(int i=0; i < RedisPipes.TIMES; i++)
                p.incr(String.valueOf(i));
            p.sync();
        }finally {
            pool.returnResource(r);
        }

    }

    private static void withoutPipeline(JedisPool pool){

        Jedis r = pool.getResource();

        try{
            r.flushDB();
            for(int i=0; i < RedisPipes.TIMES; i++)
                r.incr(String.valueOf(i));
        }finally {
            pool.returnResource(r);
        }

    }

    public static void main(String[] args) {
        JedisPool pool = Utils.getRedisPool();

        long endTime = 0L;
        long startTime = System.currentTimeMillis();
        RedisPipes.withoutPipeline(pool);
        endTime = System.currentTimeMillis();
        System.out.println(MessageFormat.format("Without pipelining: {0} seconds", (endTime - startTime) / 1000));

        startTime = System.currentTimeMillis();
        RedisPipes.withPipeline(pool);
        endTime = System.currentTimeMillis();
        System.out.println(MessageFormat.format("With pipelining: {0} seconds", (endTime - startTime) / 1000));

        pool.destroy();
    }
}
