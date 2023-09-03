package roart.common.leader.impl;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.config.MyConfig;
import roart.common.config.NodeConfig;
import roart.common.leader.MyLeader;

public class MyLeaderFactory {
    public MyLeader create(String id, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (nodeConf.wantDistributedTraverse() || nodeConf.wantAsync()) {
            if (nodeConf.getZookeeper() != null) {
                return new MyCuratorLeader(id, curatorFramework, hz);
            } else {
                return new MyHazelcastLeader(id, curatorFramework, hz);
            }
        } else {
            return new MyLocalLeader();
        }
    }

}
