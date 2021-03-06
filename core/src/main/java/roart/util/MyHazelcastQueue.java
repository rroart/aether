package roart.util;

import roart.common.constants.Constants;
import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class MyHazelcastQueue<T> extends MyQueue<T> {
    IQueue<T> queue = null;
    
    @Override
    public void offer(T o) {
        try {
            queue.put(o);
        } catch (InterruptedException e) {
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
}
