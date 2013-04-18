package models;

import play.Play;
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
            //TODO: Fix the test so they start a fake application when the test starts (needed to take the conf)
            //String host = Play.application().configuration().getString("redis.host");
            Utils.pool = new JedisPool(new JedisPoolConfig(), "localhost");

        }
        return Utils.pool;
    }
}