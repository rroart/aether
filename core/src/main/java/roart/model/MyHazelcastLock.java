package roart.model;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;

import roart.common.synchronization.MyLock;
import roart.hcutil.GetHazelcastInstance;

public class MyHazelcastLock extends MyLock {
    FencedLock lock;
    
    @Override
    public void lock(String path) throws Exception {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        lock = hz.getCPSubsystem().getLock(path);
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public FencedLock getLock() {
        return lock;
    }

    public void setLock(FencedLock lock) {
        this.lock = lock;
    }

}
