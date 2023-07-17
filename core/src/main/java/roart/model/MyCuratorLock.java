package roart.model;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import roart.service.ControlService;

public class MyCuratorLock extends MyLock {

    InterProcessMutex lock;

    @Override
    public void lock(String path) throws Exception {
        log.debug("before lock {}", path);
        lock = new InterProcessMutex(ControlService.curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
        lock.acquire();
        log.debug("after lock {}", path);
    }

    @Override
    public boolean tryLock(String path) throws Exception {
        log.debug("before lock {}", path);
        lock = new InterProcessMutex(ControlService.curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
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

}
