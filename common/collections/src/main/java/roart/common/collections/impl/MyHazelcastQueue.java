package roart.common.collections.impl;

import java.util.Queue;

import roart.common.collections.MyQueue;
import roart.common.constants.Constants;
import roart.common.hcutil.GetHazelcastInstance;

import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

public class MyHazelcastQueue<T> extends MyQueue<T> {
    HazelcastInstance hz;
    
    Queue<T> queue = null;
    
    public MyHazelcastQueue(String server, String queuename) {
        this.hz = GetHazelcastInstance.instance(server);
        queue = hz.getQueue(queuename);       
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

    @Override
    public void destroy() {
        ((IQueue) queue).destroy();
    }
}
