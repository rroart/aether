package roart.common.collections.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.curator.framework.CuratorFramework;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyQueue;
import roart.common.communication.factory.CommunicationFactory;
import roart.common.communication.model.Communication;
import roart.common.config.NodeConfig;
import roart.common.util.JsonUtil;

public class MyCommunicationQueue<T> extends MyQueue<T> {
    
    private Queue<T> queue = new ConcurrentLinkedQueue<>();
    
    private MyAtomicLong size;
    private Communication comm;
    
    public MyCommunicationQueue(String queuename, CuratorFramework curatorFramework, HazelcastInstance hz) {
        String name = NodeConfig.conf.getSynchronizationCommunicationName();
        String connection = NodeConfig.conf.getSynchronizationCommunicationConnection();
        size = MyAtomicLongs.get(queuename + "size", curatorFramework, hz);
        comm = CommunicationFactory.get(name, String.class, queuename, new ObjectMapper(), true, true, false, connection, false);
    }
    
    @Override
    public void offer(T o) {
        comm.send(o);
        size.incrementAndGet();
    }

    @Override
    public T poll() {
        synchronized(MyCommunicationQueue.class) {
            if (queue.isEmpty()) {
                Object[] array = comm.receive();
                for (Object o : array) {
                    queue.offer((T) o);
                }
            }
            if (!queue.isEmpty()) {
                size.decrementAndGet();
                return queue.poll();
            } else {
                return (T) null;
            }
        }
    }

    @Override
    public T poll(Class<T> clazz) {
        return JsonUtil.convert((String) poll(), clazz);
    }

    @Override
    public int size() {
        return queue.size() + (int) size.get();
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub        
    }

    @Override
    public void destroy() {
        comm.destroy();
    }

}
