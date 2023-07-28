package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import roart.common.collections.MyList;
import roart.common.config.NodeConfig;

import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

public class MyLists extends MyCollections {
    
    public static MyList get(String id, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        return (MyList) get(id, nodeConf, new MyListFactory(), curatorFramework, hz);
     }

    public static void put(String id) {
        put(id, new MyAtomicLongFactory());
    }
 }
