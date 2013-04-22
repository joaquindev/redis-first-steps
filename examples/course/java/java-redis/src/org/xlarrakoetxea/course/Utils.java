package org.xlarrakoetxea.course;

import redis.clients.jedis.*;


/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/22/13
 * Time: 4:41 PM
 * To change this template use File | Settings | File Templates.
 */


public class Utils {

    static private JedisPool pool = null;


    static public JedisPool getRedisPool(){
        if (pool == null){
            String host = "localhost";
            Utils.pool = new JedisPool(new JedisPoolConfig(), host);
        }
        return Utils.pool;
    }
}