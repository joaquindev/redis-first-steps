package org.xlarrakoetxea.course;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/22/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedisPubsub {
    private static final String CHANNEL = "Redis:pubsub:example";

    public static void main(String[] args) throws InterruptedException {
        class MyListener extends JedisPubSub {

            @Override
            public void onMessage(String s, String s2) {
                System.out.println(s2);
            }

            @Override
            public void onPMessage(String s, String s2, String s3) {}

            @Override
            public void onSubscribe(String s, int i) {}

            @Override
            public void onUnsubscribe(String s, int i) {}

            @Override
            public void onPUnsubscribe(String s, int i) {}

            @Override
            public void onPSubscribe(String s, int i) {}
        }


        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();

        try{

            if (args.length != 1){
                System.out.print("use: command [pub|sub]");
            }
            else if(args[0].equals("sub")){
                System.out.print(MessageFormat.format("Start subscriber on {0}", RedisPubsub.CHANNEL));
                MyListener l = new MyListener();
                r.subscribe(l, RedisPubsub.CHANNEL);

            }else if(args[0].equals("pub")){
                System.out.print(MessageFormat.format("Start publisher on {0}", RedisPubsub.CHANNEL));
                while(true){
                    r.publish(RedisPubsub.CHANNEL, (new Date()).toString());
                    Thread.sleep(1000);
                }
            }else{

                System.out.print("use: command [pub|sub]");
            }
        }finally {
            pool.returnResource(r);
            pool.destroy();
        }

    }

}
