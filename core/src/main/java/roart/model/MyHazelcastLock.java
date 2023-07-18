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
    public boolean tryLock(String path) throws Exception {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        lock = hz.getCPSubsystem().getLock(path);
        return lock.tryLock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public boolean isLocked() {
        return lock.isLocked();
    }

    public FencedLock getLock() {
        return lock;
    }

    public void setLock(FencedLock lock) {
        this.lock = lock;
    }

}
