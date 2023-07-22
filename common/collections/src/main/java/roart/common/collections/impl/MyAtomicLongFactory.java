package roart.common.collections.impl;

import org.apache.curator.framework.CuratorFramework;

import com.hazelcast.core.HazelcastInstance;

import roart.common.collections.MyFactory;
import roart.common.config.MyConfig;

public class MyAtomicLongFactory extends MyFactory {

    public MyAtomicLong create(String listid, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (MyConfig.conf.wantDistributedTraverse()) {
            return new MyHazelcastAtomicLong(hz, listid);
        } else {
            return new MyJavaAtomicLong();
        }
    }
}
