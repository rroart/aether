package roart.common.collections.impl;

import tools.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import roart.common.collections.MyQueue;
import roart.common.util.JsonUtil;
import roart.common.collections.util.JedisPools;
import roart.common.collections.util.RedisUtil;

public class MyRedisQueue<T> extends MyQueue<T> {

    private String queuename;
    
    private JedisPool pool;

    public MyRedisQueue(String server, String queuename) {
        this.queuename = queuename;
        pool = JedisPools.get(server);
        //jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public void offer(T o) {
        String string = RedisUtil.convert(o);
        try (Jedis jedis = pool.getResource()) {
            jedis.rpush(queuename, string);
        }
    }

    @Override
    public T poll() {
        try (Jedis jedis = pool.getResource()) {
            String polled = jedis.lpop(queuename);
            if ("nil".equals(polled)) {
                polled = null;
            }
            return (T) polled;
        }
    }

    @Override
    public int size() {
        try (Jedis jedis = pool.getResource()) {
            return (int) jedis.llen(queuename);
        }
    }
    
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            while (jedis.rpop(queuename) != null) { }
        }
    }

    @Override
    public T poll(Class<T> clazz) {
        try (Jedis jedis = pool.getResource()) {
            return JsonUtil.convertnostrip((String) poll(), clazz);
        }
    }

    @Override
    public void destroy() {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(queuename);
        }
    }
}
