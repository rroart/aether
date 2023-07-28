package roart.common.collections;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.config.NodeConfig;

public abstract class MyFactory {

    public abstract Object create(String id, NodeConfig nodeConf, CuratorFramework curatorFramework, HazelcastInstance hz);

}
