import models.RMessage;
import models.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.RMessage;
import models.Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.MessageFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: slok
 * Date: 4/16/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RMessageTest {

    public JedisPool pool = null;
    public Jedis r = null;

    @Before
    public void setUp() throws Exception {
        this.pool = Utils.getRedisPool();
        this.r = pool.getResource();
    }

    @After
    public void tearDown() throws Exception {
        r.flushDB();
        this.pool.returnResource(r);
    }

    @Test
    public void saveTest(){

        String message = "Message{0}";
        String by = "author{0}";
        String to = "receiver{0}";

        int times = 5;
        List<RMessage> messages = new ArrayList<RMessage>();

        for(int i=0; i<times; i++){
            RMessage msg = new RMessage(MessageFormat.format(message, "This is a test"),
                                        MessageFormat.format(by, "Slok"),
                                        MessageFormat.format(to, "Junit"));
            msg.save();
            messages.add(msg);
        }

        assertEquals(Long.valueOf(times), r.llen(RMessage.MESSAGES_KEY));

        List<String> ids = r.lrange(RMessage.MESSAGES_KEY, 0, -1);

        for (int i=0; i<ids.size(); i++){
            String id = ids.get(times-i-1);
            RMessage goodM = messages.get(i);

            Map<String, String> m = r.hgetAll(MessageFormat.format(RMessage.MESSAGE_KEY, id));
            assertEquals(goodM.getId(), m.get("id"));
            assertEquals(goodM.getMessage(), m.get("message"));
            assertEquals(goodM.getBy(), m.get("by"));
            assertEquals(goodM.getTo(), m.get("to"));
            assertEquals(goodM.getDate(), Long.valueOf(m.get("date")));
        }

    }

    @Test
    public void countTest(){
        int times = 100;

        for(int i=0; i<times; i++){
            RMessage msg = new RMessage("You suck", "Bruce Dickinson", "Justin Bieber");
            msg.save();
        }

        assertEquals(Long.valueOf(times), RMessage.count());
    }

    @Test
    public void getAllOneTest(){

        RMessage goodM = new RMessage("this is a test", "Slok", "Junit");
        goodM.save();

        List<RMessage> result = RMessage.getAll();
        assertEquals(1, result.size());

        RMessage msg = result.get(0);

        assertEquals(goodM.getId(), msg.getId());
        assertEquals(goodM.getMessage(), msg.getMessage());
        assertEquals(goodM.getBy(), msg.getBy());
        assertEquals(goodM.getTo(), msg.getTo());
        assertEquals(goodM.getDate(), msg.getDate());

    }

    @Test
    public void getAllTest(){
        String message = "Message{0}";
        String by = "author{0}";
        String to = "receiver{0}";

        int times = 100;

        List<RMessage> messages = new ArrayList<RMessage>();

        for(int i=0; i<times; i++){
            RMessage msg = new RMessage(MessageFormat.format(message, i),
                    MessageFormat.format(by, i),
                    MessageFormat.format(to, i));
            msg.save();
            messages.add(msg);
        }

        List<RMessage> result = RMessage.getAll();
        assertEquals(times, result.size());

        for (int i=0; i<result.size(); i++){
            RMessage msg= result.get(i);
            RMessage goodM = messages.get(times-i-1);

            assertEquals(goodM.getId(), msg.getId());
            assertEquals(goodM.getMessage(), msg.getMessage());
            assertEquals(goodM.getBy(), msg.getBy());
            assertEquals(goodM.getTo(), msg.getTo());
            assertEquals(goodM.getDate(), msg.getDate());
        }
    }

    @Test
    public void getAllLimitTest(){

        String message = "Message{0}";
        String by = "author{0}";
        String to = "receiver{0}";

        int times = 100;
        // From 90 to 60 (both included)
        int offset = 10;
        int count = 30;

        List<RMessage> messages = new ArrayList<RMessage>();

        for(int i=0; i<times; i++){
            RMessage msg = new RMessage(MessageFormat.format(message, i),
                    MessageFormat.format(by, i),
                    MessageFormat.format(to, i));
            msg.save();
            messages.add(msg);
        }

        messages = messages.subList(times - count - offset, times - offset);

        List<RMessage> result = RMessage.getAll(Long.valueOf(offset), Long.valueOf(count));
        assertEquals(count, result.size());

        for (int i=0; i<result.size(); i++){
            RMessage msg= result.get(i);
            RMessage goodM = messages.get(count-i-1);

            assertEquals(goodM.getId(), msg.getId());
            assertEquals(goodM.getMessage(), msg.getMessage());
            assertEquals(goodM.getBy(), msg.getBy());
            assertEquals(goodM.getTo(), msg.getTo());
            assertEquals(goodM.getDate(), msg.getDate());
        }
    }
}
