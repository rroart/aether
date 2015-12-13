package roart.util;

import roart.service.ControlService;

public class MyLockFactory {
    public static MyLock create() {
        if (ControlService.locker == null) {
            return new MyDummyLock();
        }
        switch (ControlService.locker) {
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
