package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import roart.common.config.NodeConfig;

import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

public class MyAtomicLongs extends MyCollections {
    
   public static MyAtomicLong get(String id, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
       return (MyAtomicLong) get(id, nodeConf, new MyAtomicLongFactory(), curatorFramework, hz);
    }

   public static void put(String id) {
       put(id, new MyAtomicLongFactory());
   }

}
