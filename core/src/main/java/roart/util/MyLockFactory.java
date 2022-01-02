package roart.util;

import roart.common.config.MyConfig;
import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;

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
