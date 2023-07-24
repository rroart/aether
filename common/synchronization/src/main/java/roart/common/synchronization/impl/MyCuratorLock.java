package roart.common.synchronization.impl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import org.apache.curator.framework.CuratorFramework;

public class MyCuratorLock extends MyLock {

    private CuratorFramework curatorClient;
       
    private InterProcessMutex lock;

    public MyCuratorLock(String path, CuratorFramework curatorClient) {
        super();
        this.curatorClient = curatorClient;
        this.lock = new InterProcessMutex(curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
    }

    @Override
    public void lock() throws Exception {
        lock.acquire();
    }

    @Override
    public boolean tryLock() throws Exception {
        lock.acquire(1, TimeUnit.SECONDS);
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
