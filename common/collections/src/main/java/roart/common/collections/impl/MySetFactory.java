package roart.common.collections.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import roart.common.collections.MyFactory;
import roart.common.collections.MySet;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.isInmemoryServerRedis()) {
                return new MyRedisSet(nodeConf.getInmemoryRedis(), setid);
            } else {
                return new MyHazelcastSet(nodeConf.getInmemoryHazelcast(), setid);
            }
        } else {
            return new MyJavaSet();
        }
    }
}
