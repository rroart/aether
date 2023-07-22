package roart.common.collections.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MyMap;
import roart.common.config.MyConfig;

public class MyMapFactory extends MyFactory {
    
    public MyMap create(String mapid, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.getRedis() != null) {
                return new MyRedisMap(MyConfig.conf.getRedis(), mapid);
            } else {
                return new MyHazelcastMap(hz, mapid);
            }
        } else {
            return new MyJavaMap();
        }
    }
}
