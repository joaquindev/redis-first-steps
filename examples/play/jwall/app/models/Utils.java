package models;

import play.Play;
import redis.clients.jedis.*;

import java.net.URI;
import java.net.URISyntaxException;

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
            try {
                String env = System.getenv("ENVIRONMENT");
                if (env != null && env.equals("heroku")){
                URI redisURI = new URI(System.getenv("REDISCLOUD_URL"));

                Utils.pool = new JedisPool(new JedisPoolConfig(),
                        redisURI.getHost(),
                        redisURI.getPort(),
                        Protocol.DEFAULT_TIMEOUT,
                        redisURI.getUserInfo().split(":",2)[1]);
                }else{
                    String host = Play.application().configuration().getString("redis.host");
                    Utils.pool = new JedisPool(new JedisPoolConfig(), host);
                }
            } catch (URISyntaxException e) {
                // URI couldn't be parsed. Handle exception
            }


        }
        return Utils.pool;
    }
}