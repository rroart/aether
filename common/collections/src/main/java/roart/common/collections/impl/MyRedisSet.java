package roart.common.collections.impl;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import roart.common.collections.MySet;
import roart.common.util.JsonUtil;
import roart.common.collections.util.JedisPools;
import roart.common.collections.util.RedisUtil;

public class MyRedisSet<T> extends MySet<T> {

    private String setname;
    
    private JedisPool pool;
    
    public MyRedisSet(String server, String setname) {
        this.setname = setname;
        pool = JedisPools.get(server);
        //jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public boolean add(T o) {
        String string = RedisUtil.convert(o);
        try (Jedis jedis = pool.getResource()) {
            return jedis.sadd(setname, string) == 1;
        }
    }

    @Override
    public boolean remove(T o) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.srem(setname, (String) o) == 1;
        }
    }

    @Override
    public Set<T> getAll() {
        try (Jedis jedis = pool.getResource()) {
            return (Set<T>) jedis.smembers(setname);
        }
    }

    @Override
    public int size() {
        try (Jedis jedis = pool.getResource()) {
            return (int) jedis.scard(setname);
        }
    }
    
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            for (String member : jedis.smembers(setname)) {
                jedis.srem(setname, member);
            }
        }
    }

    @Override
    public void destroy() {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(setname);
        }
    }
}
