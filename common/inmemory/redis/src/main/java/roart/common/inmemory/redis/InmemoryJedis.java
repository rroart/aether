package roart.common.inmemory.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import roart.common.constants.Constants;
import roart.common.inmemory.common.Inmemory;

public class InmemoryJedis extends Inmemory {

    private static Map<String, JedisPool> map = new HashMap<>();

    private String server;
    
    public InmemoryJedis(String server) {
        super(server);
        this.server = server;
        map.computeIfAbsent(server, v -> new JedisPool(server));
        //jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    protected String getServer() {
        return Constants.REDIS;
    }

    @Override
    public int getLimit() {
        return 256000000;
    }

    @Override
    protected void set(String key, String value) {
        try (Jedis jedis = pool().getResource()) {
            jedis.set(key, value);
        }
    }

    @Override
    protected String get(String key) {
        try (Jedis jedis = pool().getResource()) {
            String string = jedis.get(key);
            return string;
        }
    }

    @Override
    protected void del(String key) {
        try (Jedis jedis = pool().getResource()) {
            jedis.del(key);
        }
    }
    
    private JedisPool pool() {
        return map.get(server);
    }
}
