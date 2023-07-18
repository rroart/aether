package roart.model;

import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import roart.common.collections.MySet;
import roart.common.util.JsonUtil;
import roart.util.RedisUtil;

public class MyRedisSet<T> extends MySet<T> {

    private String setname;
    
    private Jedis jedis;

    public MyRedisSet(String server, String setname) {
        this.setname = setname;
        jedis = new Jedis(server);
        jedis.configSet("stop-writes-on-bgsave-error", "no");
    }

    @Override
    public boolean add(T o) {
        String string = RedisUtil.convert(o);
        return jedis.sadd(setname, string) == 1;
    }

    @Override
    public boolean remove(T o) {
        return jedis.srem(setname, (String) o) == 1;
    }

    @Override
    public Set<T> getAll() {
        return (Set<T>) jedis.smembers(setname);
    }

    @Override
    public int size() {
        return (int) jedis.scard(setname);
    }
    
    @Override
    public void clear() {
        for (String member : jedis.smembers(setname)) {
            jedis.srem(setname, member);
        }
    }
}
