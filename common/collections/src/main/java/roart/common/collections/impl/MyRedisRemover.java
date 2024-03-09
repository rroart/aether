package roart.common.collections.impl;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import roart.common.collections.util.JedisPools;

public class MyRedisRemover extends MyRemover {

    private JedisPool pool;

    public MyRedisRemover(String server) {
        pool = JedisPools.get(server);
    }
    
    @Override
    public void remove(String id) {
        try (Jedis jedis = pool.getResource()) {
            jedis.del(id);
        }
    }

}
