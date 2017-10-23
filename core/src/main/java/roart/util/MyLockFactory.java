package roart.util;

import roart.config.MyConfig;

public class MyLockFactory {
    public static MyLock create() {
        if (MyConfig.conf.getLocker() == null) {
            return new MyDummyLock();
        }
        switch (MyConfig.conf.getLocker()) {
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
