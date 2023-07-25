package roart.common.leader.impl;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.config.MyConfig;
import roart.common.leader.MyLeader;

public class MyLeaderFactory {
    public MyLeader create(String id, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.getZookeeper() != null) {
                return new MyCuratorLeader(id, curatorFramework, hz);
            } else {
                return new MyHazelcastLeader(id, curatorFramework, hz);
            }
        } else {
            return new MyLocalLeader();
        }
    }

}
