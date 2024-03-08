package roart.common.collections.impl;

import org.redisson.Redisson;
import org.redisson.RedissonAtomicLong;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

public class MyRedissonAtomicLong extends MyAtomicLong {

    private volatile RedissonAtomicLong mylong;

    public MyRedissonAtomicLong(String jserver, String name) {
        String server = jserver.replace("http", "redis");
        Config config = new Config();
        config.useSingleServer().setAddress(server);
        RedissonClient client = Redisson.create(config);
        CommandAsyncExecutor exe = ((Redisson) client).getCommandExecutor();
        mylong = new RedissonAtomicLong(exe, name);
    }
    
    @Override
    public long addAndGet(long delta) {
        return mylong.addAndGet(delta);
    }

    @Override
    public long incrementAndGet() {
        return mylong.incrementAndGet();
    }

    @Override
    public long decrementAndGet() {
        return mylong.decrementAndGet();
    }

    @Override
    public long get() {
        return mylong.get();
    }

    @Override
    public void set(long value) {
        mylong.set(value);
    }

    @Override
    public void destroy() {
        mylong.delete();
    }
}
