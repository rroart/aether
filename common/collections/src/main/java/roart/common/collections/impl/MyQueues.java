package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import roart.common.collections.MyQueue;
import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

public class MyQueues extends MyCollections {
    
    public static MyQueue get(String id, CuratorFramework curatorFramework, HazelcastInstance hz) {
        return (MyQueue) get(id, new MyQueueFactory(), curatorFramework, hz);
     }

    public static void put(String id) {
        put(id, new MyQueueFactory());
    }
 }
