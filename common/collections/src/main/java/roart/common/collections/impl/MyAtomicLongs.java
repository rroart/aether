package roart.common.collections.impl;

import roart.common.collections.MyCollections;
import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

public class MyAtomicLongs extends MyCollections {
    
   public static MyAtomicLong get(String id, CuratorFramework curatorFramework, HazelcastInstance hz) {
       return (MyAtomicLong) get(id, new MyAtomicLongFactory(), curatorFramework, hz);
    }

   public static void put(String id) {
       put(id, new MyAtomicLongFactory());
   }

}
