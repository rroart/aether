package roart.common.collections.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MySet;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.getInmemoryRedis() != null && !nodeConf.getInmemoryRedis().isEmpty()) {
                return new MyRedisSet(nodeConf.getInmemoryRedis(), setid);
            } else {
                return new MyHazelcastSet(hz, setid);
            }
        } else {
            return new MyJavaSet();
        }
    }
}
