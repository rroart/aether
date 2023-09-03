package roart.common.collections.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MyQueue;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.wantSynchronizationCommunication()) {
                return new MyCommunicationQueue(listid, nodeConf, curatorFramework, hz);
            }
            if (nodeConf.getRedis() != null && !nodeConf.getRedis().isEmpty()) {
                return new MyRedisQueue(nodeConf.getRedis(), listid);
            } else {
                return new MyHazelcastQueue(hz, listid);
            }
        } else {
            return new MyJavaQueue();
        }
    }
}
