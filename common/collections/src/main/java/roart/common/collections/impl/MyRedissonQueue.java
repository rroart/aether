package roart.common.collections.impl;

import roart.common.collections.MyQueue;
import roart.common.collections.util.RedisUtil;
import roart.common.util.JsonUtil;

import org.redisson.Redisson;
import org.redisson.RedissonQueue;
import org.redisson.api.RedissonClient;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.config.Config;

public class MyRedissonQueue<T> extends MyQueue<T>  {

    private String queuename;

    private RedissonQueue<T> queue;

    public MyRedissonQueue(String server, String queuename) {
        this.queuename = queuename;
        Config config = new Config();
        config.useSingleServer().setAddress(server);
        RedissonClient client = Redisson.create(config);
        CommandAsyncExecutor exe = ((Redisson) client).getCommandExecutor();
        queue = new RedissonQueue(exe, queuename, client);
    }

    @Override
    public void offer(T o) {
        String string = RedisUtil.convert(o);
        queue.offer(o);
    }

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public T poll(Class<T> clazz) {
        return JsonUtil.convertnostrip((String) poll(), clazz);
    }

    @Override
    public void destroy() {
        queue.delete();
    }
}
