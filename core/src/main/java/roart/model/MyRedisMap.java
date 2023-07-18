package roart.model;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import roart.common.collections.MyMap;
import roart.common.util.JsonUtil;
import roart.util.RedisUtil;

public class MyRedisMap<K, V> extends MyMap<K, V>  {

    private String mapname;
    
    private Jedis jedis;

    public MyRedisMap(String server, String mapname) {
        this.mapname = mapname;
        jedis = new Jedis(server);
        jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public V put(K k, V v) {
        String string = RedisUtil.convert(v);
        V old = (V) jedis.hget(mapname, (String) k);
        jedis.hset(mapname, (String) k, string);
        return old;
    }

    @Override
    public V remove(K k) {
        String string = jedis.hget(mapname, (String) k);
        //V v0 = RedisUtil.convert0(string, V);
        V v = (V) jedis.hget(mapname, (String) k);
        jedis.hdel(mapname, (String) k);
        return v;
    }

    @Override
    public Map<K, V> getAll() {
        return (Map<K, V>) jedis.hgetAll(mapname);
    }

    @Override
    public int size() {
        return (int) jedis.hlen(mapname);
    }
    
    @Override
    public void clear() {
        for (String member : jedis.hkeys(mapname)) {
            jedis.hdel(mapname, member);
        }
    }
}
