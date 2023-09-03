package roart.common.collections.impl;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;

public class MyAtomicLongFactory extends MyFactory {

    public MyAtomicLong create(String listid, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            return new MyHazelcastAtomicLong(hz, listid);
        } else {
            return new MyJavaAtomicLong();
        }
    }
}
