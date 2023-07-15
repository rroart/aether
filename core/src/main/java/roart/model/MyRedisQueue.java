package roart.model;

import redis.clients.jedis.Jedis;

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
        jedis.rpush(queuename, (String) o);
    }

    @Override
    public T poll() {
        return (T) jedis.rpop(queuename);
    }

    @Override
    public int size() {
        return (int) jedis.llen(queuename);
    }

}
