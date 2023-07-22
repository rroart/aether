package roart.common.collections.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import roart.common.collections.MyCollections;
import roart.common.collections.MyMap;
import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

public class MyMaps extends MyCollections {
    
    public static MyMap get(String id, CuratorFramework curatorFramework, HazelcastInstance hz) {
        return (MyMap) get(id, new MyMapFactory(), curatorFramework, hz);
     }

    public static void put(String id) {
        put(id, new MyMapFactory());
    }
}
