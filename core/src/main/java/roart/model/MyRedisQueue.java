package roart.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import roart.common.collections.MyQueue;
import roart.common.util.JsonUtil;
import roart.util.RedisUtil;

public class MyRedisQueue<T> extends MyQueue<T> {

    private String queuename;
    
    private Jedis jedis;

    public MyRedisQueue(String server, String queuename) {
        this.queuename = queuename;
        jedis = new Jedis(server);
        jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public void offer(T o) {
        String string = RedisUtil.convert(o);
        jedis.rpush(queuename, string);
    }

    @Override
    public T poll() {
        return (T) jedis.rpop(queuename);
    }

    @Override
    public int size() {
        return (int) jedis.llen(queuename);
    }
    
    @Override
    public void clear() {
        while (!"nil".equals(jedis.rpop(queuename))) { }
    }

    @Override
    public T poll(Class<T> clazz) {
        return JsonUtil.convert((String) poll(), clazz);
    }

}
