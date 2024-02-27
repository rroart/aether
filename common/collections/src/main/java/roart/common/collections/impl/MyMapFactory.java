package roart.common.collections.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MyMap;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyMapFactory extends MyFactory {
    
    public MyMap create(String mapid, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.getInmemoryRedis() != null && !nodeConf.getInmemoryRedis().isEmpty()) {
                return new MyRedisMap(nodeConf.getInmemoryRedis(), mapid);
            } else {
                return new MyHazelcastMap(hz, mapid);
            }
        } else {
            return new MyJavaMap();
        }
    }
}
