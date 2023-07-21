package roart.common.synchronization.impl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import org.apache.curator.framework.CuratorFramework;

public class MyCuratorLock extends MyLock {

    private CuratorFramework curatorClient;
       
    public MyCuratorLock(CuratorFramework curatorClient) {
        super();
        this.curatorClient = curatorClient;
    }

    InterProcessMutex lock;

    @Override
    public void lock(String path) throws Exception {
        log.debug("before lock {}", path);
        lock = new InterProcessMutex(curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
        lock.acquire();
        log.debug("after lock {}", path);
    }

    @Override
    public boolean tryLock(String path) throws Exception {
        log.debug("before lock {}", path);
        lock = new InterProcessMutex(curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
        lock.acquire(1, TimeUnit.SECONDS);
        log.debug("after lock {}", path);
        return lock.isAcquiredInThisProcess();
    }

    @Override
    public void unlock() {
        if (lock != null) {
            try {
                lock.release();
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
           }
        }
    }

    @Override
    public boolean isLocked() {
        return lock.isAcquiredInThisProcess();
    }

}
