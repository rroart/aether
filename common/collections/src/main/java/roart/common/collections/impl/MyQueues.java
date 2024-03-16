package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import roart.common.collections.MyQueue;
import roart.common.config.NodeConfig;

import org.apache.curator.framework.CuratorFramework;

public class MyQueues extends MyCollections {
    
    public static MyQueue get(String id, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        return (MyQueue) get(id, nodeConf, new MyQueueFactory(), curatorFramework);
     }

    public static void put(String id) {
        put(id, new MyQueueFactory());
    }
 }
