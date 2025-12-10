package roart.common.collections.impl;

import java.util.Map;
import java.util.Set;

import tools.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import roart.common.collections.MyMap;
import roart.common.util.JsonUtil;
import roart.common.collections.util.JedisPools;
import roart.common.collections.util.RedisUtil;

public class MyRedisMap<K, V> extends MyMap<K, V>  {

    private String mapname;
    
    private JedisPool pool;

    public MyRedisMap(String server, String mapname) {
        this.mapname = mapname;
        pool = JedisPools.get(server);
        //jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public V put(K k, V v) {
        String string = RedisUtil.convert(v);
        try (Jedis jedis = pool.getResource()) {
            V old = (V) jedis.hget(mapname, (String) k);
            jedis.hset(mapname, (String) k, string);
            return old;
        }
    }

    @Override
    public V remove(K k) {
        try (Jedis jedis = pool.getResource()) {
            String string = jedis.hget(mapname, (String) k);
            //V v0 = RedisUtil.convert0(string, V);
            V v = (V) jedis.hget(mapname, (String) k);
            jedis.hdel(mapname, (String) k);
            return v;
        }
    }

    @Override
    public V remove(K k, Class<V> clazz) {
        return JsonUtil.convertnostrip((String) remove(k), clazz);
    }

    @Override
    public Map<K, V> getAll() {
        try (Jedis jedis = pool.getResource()) {
            return (Map<K, V>) jedis.hgetAll(mapname);
        }
    }

    @Override
    public Set<K> keySet() {
        try (Jedis jedis = pool.getResource()) {
            return (Set<K>) jedis.hkeys(mapname);
        }
    }

    @Override
    public int size() {
        try (Jedis jedis = pool.getResource()) {
            return (int) jedis.hlen(mapname);
        }
    }
    
    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            for (String member : jedis.hkeys(mapname)) {
                jedis.hdel(mapname, member);
            }
        }
    }

    @Override
    public void destroy() {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(mapname);
        }
    }
}
