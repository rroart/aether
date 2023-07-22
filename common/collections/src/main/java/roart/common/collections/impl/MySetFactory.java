package roart.common.collections.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.collections.MySet;
import roart.common.config.MyConfig;

public class MySetFactory extends MyFactory {
    
    public MySet create(String setid, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            if (MyConfig.conf.getRedis() != null) {
                return new MyRedisSet(MyConfig.conf.getRedis(), setid);
            } else {
                return new MyHazelcastSet(hz, setid);
            }
        } else {
            return new MyJavaSet();
        }
    }
}
