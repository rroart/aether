package roart.common.collections.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import roart.common.collections.MyFactory;
import roart.common.collections.MyQueue;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyQueueFactory extends MyFactory {
    
    public MyQueue create(String listid, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.wantSynchronizationCommunication()) {
                return new MyCommunicationQueue(listid, nodeConf, curatorFramework);
            }
            if (nodeConf.isInmemoryServerRedis()) {
                return new MyRedisQueue(nodeConf.getInmemoryRedis(), listid);
            } else {
                return new MyHazelcastQueue(nodeConf.getInmemoryHazelcast(), listid);
            }
        } else {
            return new MyJavaQueue();
        }
    }
}
