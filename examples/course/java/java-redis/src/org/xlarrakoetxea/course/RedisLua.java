package org.xlarrakoetxea.course;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/22/13
 * Time: 7:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedisLua {

    private static final String luaScript =  " local i = tonumber(ARGV[1])\n"+
                                            "    local first = 0\n"+
                                            "    local second = 1\n"+
                                            "    local res\n"+
                                            "\n"+
                                            "    local function fibo(x, y, max)\n"+
                                            "        if max ~= 0 then\n"+
                                            "            res = redis.call('rpush',KEYS[1],x)\n"+
                                            "            return fibo(y, x+y, max -1)\n"+
                                            "        else\n"+
                                            "            return res\n"+
                                            "        end\n"+
                                            "    end\n"+
                                            "\n"+
                                            "    return fibo(first, second, i)";

    private static final String KEY = "fibonacci:example";
    private static final int fiboDigits = 100;

    public static void main(String[] args){
        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();

        try{
            List keys = new ArrayList();
            List argsRedis = new ArrayList();

            keys.add(RedisLua.KEY);

            argsRedis.add(String.valueOf(RedisLua.fiboDigits));

            Long temp = (Long) r.eval(RedisLua.luaScript, keys, argsRedis);

            System.out.println(MessageFormat.format("Result of calling fibonacci with lua in Redis: {0}", temp));
            System.out.println(MessageFormat.format("Fibonacci result:\n{0}", r.lrange(RedisLua.KEY, 0, -1)));
        }finally {
            pool.returnResource(r);
            pool.destroy();
        }

    }

}
