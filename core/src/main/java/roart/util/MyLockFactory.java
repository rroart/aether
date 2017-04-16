package roart.util;

import roart.config.MyConfig;

public class MyLockFactory {
    public static MyLock create() {
        if (MyConfig.conf.locker == null) {
            return new MyDummyLock();
        }
        switch (MyConfig.conf.locker) {
        case Constants.HAZELCAST:
            return new MyHazelcastLock();
        case Constants.CURATOR:
            return new MyCuratorLock();
            /*
        case Constants.ZOOKEEPER:
            return new MyZookeeperLock();
            */
        }
        return new MyDummyLock();
    }

}
