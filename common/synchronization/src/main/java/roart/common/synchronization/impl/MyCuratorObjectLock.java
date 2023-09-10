package roart.common.synchronization.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicValue;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import roart.common.constants.Constants;
import roart.common.synchronization.MyObjectLock;

public class MyCuratorObjectLock extends MyObjectLock {

    private String path;
    
    private CuratorFramework curatorClient;
       
    private DistributedAtomicValue lock;
    
    public MyCuratorObjectLock() {
        // for json
    }
    
    public MyCuratorObjectLock(String path, CuratorFramework curatorClient) {
        super();
        this.path = path;
        this.curatorClient = curatorClient;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3); 
        this.lock = new DistributedAtomicValue(curatorClient, "/" + Constants.AETHER + "/" + Constants.DB + "/" + path, retryPolicy);
    }

    @Override
    public boolean tryLock(String id) throws Exception {
        lock.initialize("".getBytes());
        return lock.compareAndSet("".getBytes(), id.getBytes()).succeeded();
    }

    @Override
    public void unlock() {
        try {
            lock.forceSet("".getBytes());
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
    }

    @Override
    public boolean isLocked() {
        try {
            AtomicValue<byte[]> value = lock.get();
            if (value.succeeded()) { 
                return !"".equals(new String(value.postValue()));
            }
        } catch (Exception e) {
            log.error(Constants.EXCEPTION, e);
        }
        return true;
    }

}
