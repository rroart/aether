package roart.common.collections;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

public abstract class MyFactory {

    public abstract Object create(String id, CuratorFramework curatorFramework, HazelcastInstance hz);

}
