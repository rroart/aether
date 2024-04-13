package roart.common.collections.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.curator.framework.CuratorFramework;

import com.fasterxml.jackson.databind.ObjectMapper;

import roart.common.collections.MyQueue;
import roart.common.communication.factory.CommunicationFactory;
import roart.common.communication.model.Communication;
import roart.common.config.NodeConfig;
import roart.common.util.JsonUtil;
import org.apache.commons.codec.digest.DigestUtils;

public class MyCommunicationQueue<T> extends MyQueue<T> {
    
    private Queue<T> queue = new ConcurrentLinkedQueue<>();
    
    private MyAtomicLong size;
    private Communication comm;
    
    public MyCommunicationQueue(String queuename, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        log.info("Communication queue real {}", queuename);
        queuename = DigestUtils.md5Hex(queuename);
        log.info("Communication queue {}", queuename);
        String name = nodeConf.getSynchronizationCommunicationName();
        String connection = nodeConf.getSynchronizationCommunicationConnection();
        size = MyAtomicLongs.get(queuename + "size", nodeConf, curatorFramework);
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
                    size.decrementAndGet();
                }
            }
            if (!queue.isEmpty()) {
                return queue.poll();
            } else {
                return (T) null;
            }
        }
    }

    @Override
    public T poll(Class<T> clazz) {
        return JsonUtil.convertnostrip((String) poll(), clazz);
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
