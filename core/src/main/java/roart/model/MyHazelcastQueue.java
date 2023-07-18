package roart.model;

import java.util.Queue;

import roart.common.collections.MyQueue;
import roart.common.constants.Constants;
import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastQueue<T> extends MyQueue<T> {
    Queue<T> queue = null;
    
    @Override
    public void offer(T o) {
        try {
            queue.offer(o);
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    public MyHazelcastQueue(String queuename) {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        queue = hz.getQueue("queue");       
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
