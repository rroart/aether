package roart.common.collections.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MyQueue;
import roart.common.config.MyConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.wantSynchronizationCommunication()) {
                return new MyCommunicationQueue(listid, curatorFramework, hz);
            }
            if (MyConfig.conf.getRedis() != null) {
                return new MyRedisQueue(MyConfig.conf.getRedis(), listid);
            } else {
                return new MyHazelcastQueue(hz, listid);
            }
        } else {
            return new MyJavaQueue();
        }
    }
}
