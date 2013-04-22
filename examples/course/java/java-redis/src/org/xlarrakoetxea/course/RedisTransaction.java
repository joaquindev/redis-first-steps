package org.xlarrakoetxea.course;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/22/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedisTransaction {

    private static final String KEY = "counter";
    private static final int TIMES = 100000;


    private static void incrCounterPipe(JedisPool pool){
        Jedis  r = pool.getResource();

        try{
            Pipeline p = r.pipelined();

            for (int i=0; i < RedisTransaction.TIMES; i++){
                p.incr(RedisTransaction.KEY);
            }
            p.sync();
        }finally {
            pool.returnResource(r);
        }

    }

    private static void incrCounterTransaction(JedisPool pool){
        Jedis  r = pool.getResource();

        try{
            Transaction t = r.multi();

            for (int i=0; i < RedisTransaction.TIMES; i++){
                t.incr(RedisTransaction.KEY);
            }
            t.exec();
        }finally {
            pool.returnResource(r);
        }

    }

    public static void main(String[] args) {

        class CounterWatcher implements Runnable {
            private JedisPool pool;

            public CounterWatcher(JedisPool pool){
                this.pool = pool;
            }

            @Override
            public void run() {
                Jedis  r = pool.getResource();

                try{
                    int before;
                    try{
                        before = Integer.valueOf(r.get(RedisTransaction.KEY));
                    }catch(Exception e){
                        before = 0;
                    }


                   int res;
                   while (true){
                       try{
                           res = Integer.valueOf(r.get(RedisTransaction.KEY));
                       }catch(Exception e){
                           res = 0;
                       }

                       if (res != before){
                           System.out.println(MessageFormat.format("Result: {0}", res));
                           break;
                       }
                   }
                }finally {
                    pool.returnResource(r);
                }
            }
        }

        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();

        CounterWatcher cw = new CounterWatcher(pool);
        Thread myThread = new Thread(cw);
        myThread.start();

        System.out.println(MessageFormat.format("Executing pipeline, result shouldnt be: {0}", RedisTransaction.TIMES));
        RedisTransaction.incrCounterPipe(pool);

        r.flushDB();

        cw = new CounterWatcher(pool);
        myThread = new Thread(cw);
        myThread.start();

        System.out.println(MessageFormat.format("Executing transaction, result should be: {0}", RedisTransaction.TIMES));
        RedisTransaction.incrCounterTransaction(pool);


        r.flushDB();
        pool.returnResource(r);
        pool.destroy();
    }
}
