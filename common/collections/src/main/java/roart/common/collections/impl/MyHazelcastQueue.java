package roart.common.collections.impl;

import java.util.Queue;

import roart.common.collections.MyQueue;
import roart.common.constants.Constants;

import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastQueue<T> extends MyQueue<T> {
    HazelcastInstance hz;
    
    Queue<T> queue = null;
    
    public MyHazelcastQueue(HazelcastInstance hz, String queuename) {
        this.hz = hz;
        queue = hz.getQueue("queue");       
    }

    @Override
    public void offer(T o) {
        try {
            queue.offer(o);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
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
         return poll();
    }
}
