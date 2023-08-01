package roart.common.synchronization.impl;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import org.apache.curator.framework.CuratorFramework;

public class MyCuratorLock extends MyLock {

    private String path;
    
    private CuratorFramework curatorClient;
       
    private InterProcessMutex lock;

    public MyCuratorLock(String path, CuratorFramework curatorClient) {
        super();
        this.path = path;
        this.curatorClient = curatorClient;
        this.lock = new InterProcessMutex(curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
    }

    @Override
    public void lock() throws Exception {
        log.debug("lock {}", path);
        lock.acquire();
        log.debug("locka {}", path);
    }

    @Override
    public boolean tryLock() throws Exception {
        log.debug("lock {}", path);
        lock.acquire(1, TimeUnit.SECONDS);
        log.debug("locka {}", path);
        return lock.isOwnedByCurrentThread();
    }

    @Override
    public void unlock() {
        log.debug("unlock {}", path);
        if (lock != null) {
            try {
                lock.release();
            } catch (Exception e) {
                log.error(Constants.EXCEPTION, e);
           }
        }
        log.debug("unlocka {}", path);
    }

    @Override
    public boolean isLocked() {
        return lock.isAcquiredInThisProcess();
    }

}
