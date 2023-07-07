package roart.model;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import roart.common.constants.Constants;
import roart.common.synchronization.MyLock;
import roart.service.ControlService;

public class MyCuratorLock extends MyLock {

    InterProcessMutex lock;

    @Override
    public void lock(String path) throws Exception {
        log.info("before lock");
        lock = new InterProcessMutex(ControlService.curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path);
        lock.acquire();
        log.info("after lock");
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
