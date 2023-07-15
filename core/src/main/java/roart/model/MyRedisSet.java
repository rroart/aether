package roart.model;

import java.util.Set;

import redis.clients.jedis.Jedis;
import roart.common.collections.MySet;

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
        return jedis.sadd(setname, (String) o) == 1;
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

}
