package roart.common.collections.impl;

import org.apache.curator.framework.CuratorFramework;

import roart.common.collections.MyFactory;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyAtomicLongFactory extends MyFactory {

    public MyAtomicLong create(String listid, NodeConfig nodeConf, CuratorFramework curatorFramework) {
        if ((nodeConf.wantDistributedTraverse() || nodeConf.wantAsync())) {
            if (nodeConf.isInmemoryServerRedis()) {
                return new MyRedissonAtomicLong(nodeConf.getInmemoryRedis(), listid);
            } else {
                return new MyHazelcastAtomicLong(nodeConf.getInmemoryHazelcast(), listid);
            }
        } else {
            return new MyJavaAtomicLong();
        }
    }
}
