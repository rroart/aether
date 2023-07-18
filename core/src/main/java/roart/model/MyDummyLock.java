package roart.model;

import roart.common.synchronization.MyLock;

public class MyDummyLock extends MyLock {
    
    @Override
    public void lock(String path) throws Exception {
    }

    @Override
    public boolean tryLock(String path) throws Exception {
        return false;
    }

    @Override
    public void unlock() {
    }

    @Override
    public boolean isLocked() {
        return false;
    }

}
