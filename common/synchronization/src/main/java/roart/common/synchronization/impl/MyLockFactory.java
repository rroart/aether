package roart.common.synchronization.impl;

import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;

public class MyLockFactory {
    public static MyLock create(String locker, CuratorFramework curatorFramework, HazelcastInstance hz) {
        if (locker == null) {
            return new MyDummyLock();
        }
        switch (locker) {
        case Constants.HAZELCAST:
            return new MyHazelcastLock(hz);
        case Constants.CURATOR:
            return new MyCuratorLock(curatorFramework);
        case Constants.LOCAL:
            return new MyLocalLock();
            /*
        case Constants.ZOOKEEPER:
            return new MyZookeeperLock();
            */
        }
        return new MyDummyLock();
    }

}
