package roart.util;

import java.util.concurrent.locks.Lock;

import roart.hcutil.GetHazelcastInstance;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

public class MyHazelcastLock extends MyLock {
    ILock lock;
    
    @Override
    public void lock(String path) throws Exception {
        HazelcastInstance hz = GetHazelcastInstance.instance();
        lock = hz.getLock(path);
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    public ILock getLock() {
        return lock;
    }

    public void setLock(ILock lock) {
        this.lock = lock;
    }

}
