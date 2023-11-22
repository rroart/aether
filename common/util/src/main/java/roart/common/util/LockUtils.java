package roart.common.util;

import org.apache.curator.framework.CuratorFramework;

import roart.common.model.IndexFiles;
import roart.common.synchronization.MyObjectLock;
import roart.common.synchronization.impl.MyCuratorObjectLock;
import roart.common.synchronization.impl.MyObjectLockFactory;

public class LockUtils {

    public static void fix(IndexFiles indexFiles, String locker, CuratorFramework curatorClient) {
        MyObjectLock lock = indexFiles.getObjectlock();
        if (lock != null && lock instanceof MyCuratorObjectLock) {
            MyCuratorObjectLock curatorLock = (MyCuratorObjectLock) lock;
            lock = MyObjectLockFactory.create(curatorLock.getPath(), locker, curatorClient);
            indexFiles.setObjectlock(lock);
        }
    }

}
