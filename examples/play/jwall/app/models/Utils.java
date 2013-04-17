package models;

import redis.clients.jedis.*;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/16/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    static private JedisPool pool = null;


    static public JedisPool getRedisPool(){
        if (pool == null){
            Utils.pool = new JedisPool(new JedisPoolConfig(), "localhost");

        }
        return Utils.pool;
    }
}
