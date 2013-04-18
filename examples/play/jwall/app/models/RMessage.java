package models;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/16/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class RMessage {

    private String id;
    private String message;
    private String by;
    private String to;
    private Long date;

    static final public String MESSAGE_KEY = "Mdiss:message:{0}";
    static final public String MESSAGES_KEY = "Mdiss:messages";

    static final private String LUA_SAVE = "local id = ARGV[1]\n" +
                                    "local msg = ARGV[2]\n" +
                                    "local by = ARGV[3]\n" +
                                    "local to = ARGV[4]\n" +
                                    "local date = tonumber(ARGV[5])\n" +
                                    "local res\n" +
                                    "\n" +
                                    "-- Save the id in the list\n" +
                                    "redis.call('lpush',KEYS[1], id)\n" +
                                    "\n" +
                                    "-- Save the data of the message\n" +
                                    "res = redis.call('hmset',KEYS[2], \"id\", id, \"message\", msg, \"by\", by, \"to\", to, \"date\", date)\n" +
                                    "return res";

    static final private String LUA_RETRIEVE = "local messages = {}\n" +
                                        "local min = tonumber(ARGV[1])\n" +
                                        "local max = tonumber(ARGV[2])\n" +
                                        "\n" +
                                        "-- Get all the IDs\n" +
                                        "local messageIds = redis.call('lrange',KEYS[1], min, max)\n" +
                                        "\n" +
                                        "--And now with the IDs get all the messages\n" +
                                        "for key, value in pairs(messageIds) do\n" +
                                        "    local tempMessage = redis.call('hgetall', \"Mdiss:message:\"..value)\n" +
                                        "     -- Jedis doesnt support double lists, so we use '|' as object separation\n" +
                                        "    table.insert(messages, '|')\n" +
                                        "    for k,v in pairs(tempMessage) do table.insert(messages, v) end\n" +
                                        "end\n" +
                                        "return  messages";

    public RMessage(){}

    public RMessage(String message, String by, String to){
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.by = by;
        this.to = to;
        this.date = System.currentTimeMillis() / 1000L; // Unix timestamp

    }

    public void save(){
        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();
        try {
            List keys = new ArrayList();
            List args = new ArrayList();

            keys.add(MESSAGES_KEY);
            keys.add(MessageFormat.format(MESSAGE_KEY, this.id));

            args.add(this.id);
            args.add(this.message);
            args.add(this.by);
            args.add(this.to);
            args.add(String.valueOf(this.date));

            r.eval(RMessage.LUA_SAVE, keys, args);

        } finally{
            pool.returnResource(r);
        }
    }

    public static Long count(){
        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();
        try {
            return r.llen(RMessage.MESSAGES_KEY);
        } finally{
            pool.returnResource(r);
        }

    }

    public static List<RMessage> getAll(){
        return RMessage.getAll(0L, -1L);
    }

    public static List<RMessage> getAll(Long offset, Long count){
        if (count != -1L)
            count--;

        JedisPool pool = Utils.getRedisPool();
        Jedis r = pool.getResource();
        ArrayList<RMessage> results = new ArrayList<RMessage>();

        try {
            List keys = new ArrayList();
            List args = new ArrayList();

            keys.add(MESSAGES_KEY);

            args.add(String.valueOf(offset));
            args.add(String.valueOf(offset + count));

            List temp = (List) r.eval(RMessage.LUA_RETRIEVE, keys, args);
            RMessage tempMessage = null;
            for (int i=0; i < temp.size(); i++){
                String attr = (String)temp.get(i);

                if (attr.equals("|")){
                    if (tempMessage != null) // for the first loop
                        results.add(tempMessage);
                    tempMessage = new RMessage();

                    attr = (String)temp.get(++i);
                }

                // reflection to slow
                //tempMessage.setIntField(attr, temp.get(++i));

                // Use if/else
                String value = (String)temp.get(++i);
                if (attr.equals("id"))
                    tempMessage.setId(value);
                else if (attr.equals("message"))
                    tempMessage.setMessage(value);
                else if (attr.equals("by"))
                    tempMessage.setBy(value);
                else if (attr.equals("to"))
                    tempMessage.setTo(value);
                else if (attr.equals("date"))
                    tempMessage.setDate(Long.valueOf(value));
            }

            // add the last one
            if (tempMessage != null)
                results.add(tempMessage);

        } finally{
            pool.returnResource(r);
        }
        return results;
    }

    public void setIntField(String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.set(this, value);
    }


    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getBy() {
        return by;
    }

    public String getTo() {
        return to;
    }

    public Long getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString(){
        return MessageFormat.format("{0} - {1} by {2} to {3} on {4}", this.id, this.message, this.by, this.to, this.date);
    }


}
